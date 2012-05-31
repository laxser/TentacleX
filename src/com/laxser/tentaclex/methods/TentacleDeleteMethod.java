package com.laxser.tentaclex.methods;

/**
 * DELETE方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:37:07
 */
public class TentacleDeleteMethod extends TentacleBodilessMethod {

	/*public XoaDeleteMethod(String domain, String path) {
		super(domain, path);
	}*/

	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.DELETE;
	}

}
