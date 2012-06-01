package com.laxser.tentaclex.lite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注一次XOA调用中的Header信息
 * 
 * @author laxser  Date 2012-6-1 上午8:56:55
@contact [duqifan@gmail.com]
@TXHeader.java

 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TXHeader {
	/**
	 * @return 所代表的header name
	 */
	String value();
}
