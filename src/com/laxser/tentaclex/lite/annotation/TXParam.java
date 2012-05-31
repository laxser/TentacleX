package com.laxser.tentaclex.lite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一次XOA调用中的参数
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-17 下午03:15:23
 */
@Target( { ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TXParam {
	
    public static final String TYPE_RAW = "raw";
    
    public static final String TYPE_JSON = "json";
    
    public static final String TYPE_JAVA = "java";
    
	/**
	 * @return 参数名
	 */
	String value();
	
	/**
	 * @return 参数类型，支持raw和json两种类型。
	 * json类型的对象参数会被转化为json传输，这通常用户复杂的java bean对象。
	 * java类型的对象参数会按照java内置的序列化格式传输
	 */
	String type() default "raw";
}
