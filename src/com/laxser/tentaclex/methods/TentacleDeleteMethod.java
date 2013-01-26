package com.laxser.tentaclex.methods;

/**
 * DELETE方法
 * 
 * @author laxser  Date 2012-6-1 上午8:54:29
@contact [duqifan@gmail.com]
@TentacleDeleteMethod.java

 */
public class TentacleDeleteMethod extends TentacleBodilessMethod {

	/*public txDeleteMethod(String domain, String path) {
		super(domain, path);
	}*/

	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.DELETE;
	}

}
