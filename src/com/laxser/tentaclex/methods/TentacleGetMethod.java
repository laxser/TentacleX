package com.laxser.tentaclex.methods;


/**
 * GET方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:36:57
 */
public class TentacleGetMethod extends TentacleBodilessMethod {

	/*public XoaGetMethod(String domain, String path) {
		super(domain, path);
	}*/
	
	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.GET;
	}
	
}
