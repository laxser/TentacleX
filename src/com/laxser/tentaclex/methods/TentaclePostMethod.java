package com.laxser.tentaclex.methods;

/**
 * POST方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:36:45
 */
public class TentaclePostMethod extends TentacleEntityEnclosingMethod {
	
	/*public XoaPostMethod(String domain, String path) {
		super(domain, path);
	}*/

	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.POST;
	}

}
