package com.laxser.tentaclex.methods;

/**
 * POST方法
 * 
 * @author laxser  Date 2012-6-1 上午8:55:00
@contact [duqifan@gmail.com]
@TentaclePostMethod.java

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
