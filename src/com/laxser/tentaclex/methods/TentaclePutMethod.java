package com.laxser.tentaclex.methods;

/**
 * PUT方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:36:32
 */
public class TentaclePutMethod extends TentacleBodilessMethod {

	/*public XoaPutMethod(String domain, String path) {
		super(domain, path);
	}*/

	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.PUT;
	}

}
