package com.laxser.tentaclex.lite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:57:06
 */
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TXPost {
	
    public static final String CONNTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    
    public static final String CONNTENT_TYPE_MULTIFORMAT = "application/xoa-multiformat";
    
	/**
	 * @return 要操作的资源的URI
	 */
	String value();
	
	String conntentType() default CONNTENT_TYPE_FORM;
}
