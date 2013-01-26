package com.laxser.tentaclex.lite;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.laxser.tentaclex.InvocationInfo;
import com.laxser.tentaclex.Method;
import com.laxser.tentaclex.StatusNotOkException;
import com.laxser.tentaclex.Octopus.TentacleResponseCallback;
import com.laxser.tentaclex.Tentacle;
import com.laxser.tentaclex.TentacleResponse;

/**
 * 默认的{@link ServiceFuture}实现
 * 
 @author laxser  Date 2012-6-1 上午8:55:52
@contact [duqifan@gmail.com]
@DefaultServiceFutrue.java

 * @param <T>
 */
public class DefaultServiceFutrue<T> implements ServiceFuture<T> {

	private static final Throwable CANCELLED = new Throwable();
	
	/**
	 * 创建状态
	 */
	private static final int STATE_CREATED = 0;
	
	/**
	 * 提交后，未完成状态
	 */
	private static final int STATE_SUBMITED = 1;
	
	/**
	 * 当前状态
	 */
	private volatile int state = STATE_CREATED;
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
	/**
	 * 此次调用的response
	 */
	private TentacleResponse response;

	/**
	 * 保存此次调用的method对象
	 */
	private Method method;
	
	/**
	 * 此次调用使用的txClient
	 */
	private Tentacle txClient;
	
	/**
	 * 是否可以被cancel
	 */
	private final boolean cancellable;
	
	/**
	 * 第一个listener
	 */
	private volatile ServiceFutureListener firstListener;
    
	/**
     * 其他listener
     */
    private volatile List<ServiceFutureListener> otherListeners;
    
    /**
     * 请求是否完成，不管成功失败，只要返回，就算完成
     */
    private volatile boolean done;
    
    /**
     * 造成失败的原因
     */
    private Throwable cause;
    
    /**
     * 有多少线程在等待
     */
    private int waiters;
	
	/**
	 * 返回类型
	 */
	private Type contentType;
	
	/**
	 * 本次tx调用相关的信息
	 */
	private InvocationInfo invocationInfo;
	
	public DefaultServiceFutrue(Type contentType, boolean cancellable) {
		this.contentType = contentType;
		this.cancellable = cancellable;
	}
	
	public DefaultServiceFutrue(Type contentType) {
		this(contentType, false);
	}
	
	void setMethod(Method method) {
		this.method = method;
	}

	void settxClient(Tentacle txClient) {
		this.txClient = txClient;
	}
	
	/**
	 * 为await操作来检查当前status，如果还没submit就做await操作了，那肯定是不对的
	 */
	private void checkStatusForAwait() {
		if (state != STATE_SUBMITED) {
			throw new IllegalStateException("Service requst has not been submitted yet. Please submit first.");
		}
	}

	@Override
	public void addListener(ServiceFutureListener listener) {
		if (listener == null) {
            throw new NullPointerException("listener");
        }

        boolean notifyNow = false;
        synchronized (this) {
            if (done) {
                notifyNow = true;
            } else {
                if (firstListener == null) {
                    firstListener = listener;
                } else {
                    if (otherListeners == null) {
                        otherListeners = new ArrayList<ServiceFutureListener>(1);
                    }
                    otherListeners.add(listener);
                }
            }
        }

        if (notifyNow) {
            notifyListener(listener);
        }
	}

	@Override
	public void removeListener(ServiceFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }

        synchronized (this) {
            if (!done) {
                if (listener == firstListener) {
                    if (otherListeners != null && !otherListeners.isEmpty()) {
                        firstListener = otherListeners.remove(0);
                    } else {
                        firstListener = null;
                    }
                } else if (otherListeners != null) {
                    otherListeners.remove(listener);
                }
            }
        }
    }
	
	@Override
	public ServiceFuture<T> await() throws InterruptedException {
		checkStatusForAwait();
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        synchronized (this) {
            while (!done) {
                checkDeadLock();
                waiters++;
                try {
                    this.wait();
                } finally {
                    waiters--;
                }
            }
        }
        return this;
    }

	@Override
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return await0(unit.toNanos(timeout), true);
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
        return await0(MILLISECONDS.toNanos(timeoutMillis), true);
    }

	private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
		checkStatusForAwait();
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException();
        }

        long startTime = timeoutNanos <= 0 ? 0 : System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;

        try {
            synchronized (this) {
                if (done) {
                    return done;
                } else if (waitTime <= 0) {
                    return done;
                }

                checkDeadLock();
                waiters++;
                try {
                    for (;;) {
                        try {
                            this.wait(waitTime / 1000000, (int) (waitTime % 1000000));
                        } catch (InterruptedException e) {
                            if (interruptable) {
                                throw e;
                            } else {
                                interrupted = true;
                            }
                        }

                        if (done) {
                            return true;
                        } else {
                            waitTime = timeoutNanos - (System.nanoTime() - startTime);
                            if (waitTime <= 0) {
                                return done;
                            }
                        }
                    }
                } finally {
                    waiters--;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
	
	@Override
	public ServiceFuture<T> awaitUninterruptibly() {
		checkStatusForAwait();
        boolean interrupted = false;
        synchronized (this) {
            while (!done) {
                checkDeadLock();
                waiters++;
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    waiters--;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

	@SuppressWarnings("unchecked")
	public T getContent() {
		return (T)getContent0();
	}
	
	private Object getContent0() {
		
		if (!isSuccess()) {	//请求未成功的情况下
			if (cause != null) {
				throw new IllegalStateException("Service request not successful", cause);
			} else {
				throw new IllegalStateException("Service request not successful yet.");
			}
		}
		
		if (contentType instanceof ParameterizedType) {
			
			//ParameterizedType pType = (ParameterizedType) contentType;
			//Type atr = pType.getActualTypeArguments()[0];
			//Type rawType = pType.getRawType();
			//TODO implement this
			throw new UnsupportedOperationException();
		} else if (contentType instanceof Class<?>) {
			Class<?> contentClass = (Class<?>)contentType;
			if (contentClass.equals(Void.class)) {	//void return type
				return null;
			}
			return response.getContentAs(contentClass); 
		} else if (contentType instanceof GenericArrayType) {	//返回值如果是对象数组，会进入此逻辑分支
			GenericArrayType gar = (GenericArrayType) contentType;
			Type type = gar.getGenericComponentType();
			if (type instanceof Class<?>) {
				Class<? extends Object> arrayClass = Array.newInstance(
						(Class<?>) type, 0).getClass();
				return response.getContentAs(arrayClass);
			} else {
				throw new RuntimeException("Unsupported content type: "
						+ contentType);
			}
		} else {
			throw new RuntimeException("Unsupported content type: "
					+ contentType);
		}
	}
	
	private void checkDeadLock() {
		// TODO 此方法需要实现吗？
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return await0(unit.toNanos(timeout), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return await0(MILLISECONDS.toNanos(timeoutMillis), false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

	@Override
	public boolean cancel() {
        if (!cancellable) {
            return false;
        }

        synchronized (this) {
            // Allow only once.
            if (done) {
                return false;
            }

            cause = CANCELLED;
            done = true;
            if (waiters > 0) {
                notifyAll();
            }
        }

        notifyListeners();
        return true;
    }

	@Override
	public Throwable getCause() {
		return cause;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public boolean isSuccess() {
		return done && cause == null;
	}

	@Override
	public boolean setFailure(Throwable cause) {
		synchronized (this) {
            // Allow only once.
            if (done) {
                return false;
            }

            this.cause = cause;
            done = true;
            if (waiters > 0) {
                notifyAll();
            }
        }
        notifyListeners();
        return true;
    }

	@Override
	public boolean setSuccess() {
        synchronized (this) {
            // Allow only once.
            if (done) {
                return false;
            }

            done = true;
            if (waiters > 0) {
                notifyAll();
            }
        }

        notifyListeners();
        return true;
    }
	
	/*@Override
	public txResponse getResponse() {
		return response;
	}*/

	public void setResponse(TentacleResponse response) {
		this.response = response;
	}

	private void notifyListeners() {
        // There won't be any visibility problem or concurrent modification
        // because 'ready' flag will be checked against both addListener and
        // removeListener calls.
        if (firstListener != null) {
            notifyListener(firstListener);
            firstListener = null;

            if (otherListeners != null) {
                for (ServiceFutureListener l: otherListeners) {
                    notifyListener(l);
                }
                otherListeners = null;
            }
        }
    }

    private void notifyListener(ServiceFutureListener l) {
    	try {
        	l.operationComplete(this);
        } catch (Throwable t) {
            logger.warn(
                    "An exception was thrown by " +
                    ServiceFutureListener.class.getSimpleName() + ".", t);
        }
    }

	@Override
	public ServiceFuture<T> submit() {
		final DefaultServiceFutrue<?> future = this;
		final Method method = this.method;
		invocationInfo = this.txClient.submit(method, new TentacleResponseCallback() {
			@Override
			public void responseReceived(TentacleResponse response) {
				future.setResponse(response);
				if (response.getStatusCode() == 200) {
					future.setSuccess();
				} else {
					Throwable cause = new StatusNotOkException("["
							+ method.getServiceId() + "] Status code "
							+ response.getStatusCode() + " from remote host "
							+ response.getRemoteHost()).setResponse(response);
					future.setFailure(cause);
				}
			}
		});
		this.state = STATE_SUBMITED;
		return this;
	}

	/**
	 * 检查当前状态，如果尚未提交，那么还是可以添加参数等的
	 */
	private void checkStateForMethodModify(){
		if (state >= STATE_SUBMITED) {
			throw new IllegalStateException("The service request has been submitted. So that it can not be modified anymore.");
		}
	}
	
	@Override
	public void setHeader(String name, String value) {
		checkStateForMethodModify();
		method.setHeader(name, value);
	}

	@Override
	public void setParam(String name, Object value) {
		checkStateForMethodModify();
		
		if (value instanceof String) {
		    method.setParam(name, (String)value);
		} else {
		    method.setParamAsJson(name, value);
		}
	}
	
	/**
	 * @return 本次tx调用相关的信息
	 */
	InvocationInfo getInvocationInfo() {
		return this.invocationInfo;
	}

	public void setTxClient(Tentacle client) {
		// TODO Auto-generated method stub
		
	}
	
}
