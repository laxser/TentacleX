package com.laxser.tentaclex.methods;

/**
 * PUT方法
 * 
 *@author laxser  Date 2012-6-1 上午8:55:05
@contact [duqifan@gmail.com]
@TentaclePutMethod.java

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
