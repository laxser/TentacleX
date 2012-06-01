package com.laxser.tentaclex.lite.impl.listener;

import com.laxser.tentaclex.lite.ServiceFuture;
import com.laxser.tentaclex.lite.ServiceFutureListener;

/**
 * 处理调用失败情况的{@ ServiceFutureListener}，这是一个抽象类，
 * 子类需要继承之，实现onError方法。
 * 
 * 这个Listener可以向上层调用者返回数据，同时也可以抛出异常，返回值的
 * 类型和异常类型需要通过泛型来制定，而具体的逻辑则有onError方法来实现。
 * 
 * @author laxser  Date 2012-6-1 上午8:56:10
@contact [duqifan@gmail.com]
@ServiceFailureListener.java

 * @param <T> getReturn方法的返回值类型
 * @param <E> getReturn方法被调用时，可能抛出的异常
 */
public abstract class ServiceFailureListener<T, E extends Throwable> implements ServiceFutureListener {

	/**
	 * operationComplete方法被调用后
	 */
	boolean done = false;
	
	/**
     * 有多少线程在等待
     */
    private int waiters;
	
	/**
	 * 返回值
	 */
	protected T ret;
	
	/**
	 * 异常
	 */
	protected E myCause;
	
	@SuppressWarnings("unchecked")
	@Override
	public void operationComplete(ServiceFuture<?> future) throws Exception {
		if (!future.isSuccess()) {
			try {
				ret = onError(future);
			} catch (Throwable e) {
				myCause = (E)e;
			}
			
			synchronized (this) {
	            // Allow only once.
	            if (done) {
	                return;
	            }
	            done = true;
	            if (waiters > 0) {
	                notifyAll();
	            }
	        }
		}
	}
	
	/**
	 * ServiceFuture完成的时候，此方法会被调用
	 * 
	 * @param future
	 * @return
	 * @throws E
	 */
	public abstract T onError(ServiceFuture<?> future) throws E;

	/**
	 * 
	 * 获取返回值，这个返回值是在onError方法被回调的时候，onError方法的返回值，
	 * 而如果onError方法被回调的时候抛出了异常E，那么此方法在被调用时也会抛出
	 * 异常E。
	 * 
	 * @return
	 * @throws E
	 * @throws InterruptedException
	 */
	public T getReturn() throws E, InterruptedException {
		
		if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        synchronized (this) {
        	//这里要用while而不是if，是因为wait在this上，
        	//有可能被其他逻辑notify，而此时还没有done呢，要回来再wait
            while (!done) {	
        		waiters++;
                try {
                    this.wait();
                } finally {
                    waiters--;
                }
            }
        }
		
		if (myCause != null) {
			throw myCause;
		}
		return (T)ret;
	}
	
	/*@SuppressWarnings("unchecked")
	public <L extends ServiceFutureListener> L addListener(ServiceFuture<?> future) {
		future.addListener(this);
		return (L)this;
	}*/
	
}
