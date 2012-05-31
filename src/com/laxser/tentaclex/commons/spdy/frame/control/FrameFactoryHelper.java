package com.laxser.tentaclex.commons.spdy.frame.control;

/**
 * 由于这个包下Frame实现类的构造函数的访问级别都是包级别的，
 * 所以FrameFactory借助FrameFactoryHelper来访问它们的构造函数。
 * 
 * 包级别的构造函数是为了控制构造过程，这点在重构的过程中尤为重要。
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-6-17 上午11:45:34
 */
public class FrameFactoryHelper {
	
	public static SynReply newSynReply(byte flags, byte[] dataInByte) {
		//return new DefaultSynReply(flags, dataInByte);
		return new OptimizedSynReply(flags, dataInByte);
	}
	
	public static SynReply newSynReply(byte flags, int streamId) {
		//return new DefaultSynReply(flags, dataInByte);
		return new OptimizedSynReply(flags, streamId);
	}
	
	public static SynReply newSynReplyForProxy(byte flags, byte[] dataInByte) {
		return new NVBlockReadonlySynReply(flags, dataInByte);
	}
	
	public static SynStream newSynStream(byte flags, byte[] dataInByte) {
		//return new DefaultSynStream(flags, dataInByte);
		return new OptimizedSynStream(flags, dataInByte);
	}
	
	public static SynStream newSynStream(byte flags, int streamId) {
		//return new DefaultSynStream(flags, streamId);
		return new OptimizedSynStream(flags, streamId);
	}
	
	public static SynStream newSynStreamForProxy(byte flags, byte[] dataInByte) {
		return new NVBlockReadonlySynStream(flags, dataInByte);
	}
}


