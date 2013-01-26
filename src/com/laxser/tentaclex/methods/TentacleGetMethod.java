package com.laxser.tentaclex.methods;


/**
 * GET方法
 * 
 * @author laxser  Date 2012-6-1 上午8:54:39
@contact [duqifan@gmail.com]
@TentacleGetMethod.java

 */
public class TentacleGetMethod extends TentacleBodilessMethod {

	/*public txGetMethod(String domain, String path) {
		super(domain, path);
	}*/
	
	@Override
	public TentacleMethodName getName() {
		return TentacleMethodName.GET;
	}
	
}
