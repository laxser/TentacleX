package com.laxser.tentaclex.lite;

import java.util.concurrent.TimeUnit;


/**
 * 封装一次异步调用的返回信息
 * 
 * @author laxser  Date 2012-6-1 上午8:55:34
@contact [duqifan@gmail.com]
@ServiceFuture.java

 * @param <T>
 */
public interface ServiceFuture<T> {

	    /**
	     * Returns {@code true} if and only if this future is
	     * complete, regardless of whether the operation was successful, failed,
	     * or cancelled.
	     */
	    boolean isDone();

	    /**
	     * Returns {@code true} if and only if this future was
	     * cancelled by a {@link #cancel()} method.
	     */
	    boolean isCancelled();

	    /**
	     * Returns {@code true} if and only if the I/O operation was completed
	     * successfully.
	     */
	    boolean isSuccess();

	    /**
	     * Returns the cause of the failed I/O operation if the I/O operation has
	     * failed.
	     *
	     * @return the cause of the failure.
	     *         {@code null} if succeeded or this future is not
	     *         completed yet.
	     */
	    Throwable getCause();

	    /**
	     * Cancels the I/O operation associated with this future
	     * and notifies all listeners if canceled successfully.
	     *
	     * @return {@code true} if and only if the operation has been canceled.
	     *         {@code false} if the operation can't be canceled or is already
	     *         completed.
	     */
	    boolean cancel();

	    /**
	     * Marks this future as a success and notifies all
	     * listeners.
	     *
	     * @return {@code true} if and only if successfully marked this future as
	     *         a success. Otherwise {@code false} because this future is
	     *         already marked as either a success or a failure.
	     */
	    boolean setSuccess();

	    /**
	     * Marks this future as a failure and notifies all
	     * listeners.
	     *
	     * @return {@code true} if and only if successfully marked this future as
	     *         a failure. Otherwise {@code false} because this future is
	     *         already marked as either a success or a failure.
	     */
	    boolean setFailure(Throwable cause);

	    /**
	     * Adds the specified listener to this future.  The
	     * specified listener is notified when this future is
	     * {@linkplain #isDone() done}.  If this future is already
	     * completed, the specified listener is notified immediately.
	     */
	    void addListener(ServiceFutureListener listener);

	    /**
	     * Removes the specified listener from this future.
	     * The specified listener is no longer notified when this
	     * future is {@linkplain #isDone() done}.  If this
	     * future is already completed, this method has no effect
	     * and returns silently.
	     */
	    void removeListener(ServiceFutureListener listener);

	    /**
	     * Waits for this future to be completed.
	     *
	     * @throws InterruptedException
	     *         if the current thread was interrupted
	     */
	    ServiceFuture<T> await() throws InterruptedException;

	    /**
	     * Waits for this future to be completed without
	     * interruption.  This method catches an {@link InterruptedException} and
	     * discards it silently.
	     */
	    ServiceFuture<T> awaitUninterruptibly();
	    
	    /**
	     * Waits for this future to be completed within the
	     * specified time limit.
	     *
	     * @return {@code true} if and only if the future was completed within
	     *         the specified time limit
	     *
	     * @throws InterruptedException
	     *         if the current thread was interrupted
	     */
	    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

	    /**
	     * Waits for this future to be completed within the
	     * specified time limit.
	     *
	     * @return {@code true} if and only if the future was completed within
	     *         the specified time limit
	     *
	     * @throws InterruptedException
	     *         if the current thread was interrupted
	     */
	    boolean await(long timeoutMillis) throws InterruptedException;

	    /**
	     * Waits for this future to be completed within the
	     * specified time limit without interruption.  This method catches an
	     * {@link InterruptedException} and discards it silently.
	     *
	     * @return {@code true} if and only if the future was completed within
	     *         the specified time limit
	     */
	    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

	    /**
	     * Waits for this future to be completed within the
	     * specified time limit without interruption.  This method catches an
	     * {@link InterruptedException} and discards it silently.
	     *
	     * @return {@code true} if and only if the future was completed within
	     *         the specified time limit
	     */
	    boolean awaitUninterruptibly(long timeoutMillis);
	    
	    /**
	     * 获取返回的内容，如果调用尚未成功返回，或者有错误的返回，
	     * 那么会抛出{@link IllegalStateException}，所以调用此方法
	     * 前应该先调用isSuccess方法来判断当前返回状态
	     * 
	     * @return 返回的内容
	     */
	    T getContent();
	    
	    /**
	     * @return 当前调用所对应的{@link XoaResponse}，如果调用
	     * 尚未返回，则return null
	     */
	    //XoaResponse getResponse();
	    
	    /**
	     * 获取{@link ServiceFuture}实例后，其所对应的XOA请求并没有提交出去，
	     * 在提交之前，还是可以添加header和param的，而调用此方法后，请求才会真正
	     * 提交出去。
	     * 
	     * @return
	     */
	    ServiceFuture<T> submit();
	    
	    /**
	     * 在submit方法调用之前，还可以向XOA请求中添加参数
	     * 
	     * @param name 参数名
	     * @param value 参数值
	     */
	    void setParam(String name, Object value);
	    
	    /**
	     * 在submit方法调用之前，还可以向XOA请求中添加header
	     * 
	     * @param name header名
	     * @param value header值
	     */
	    void setHeader(String name, String value);
	}

