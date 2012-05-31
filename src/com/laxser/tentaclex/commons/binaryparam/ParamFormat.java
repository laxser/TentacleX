package com.laxser.tentaclex.commons.binaryparam;


/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-5-25 下午05:00:12
 */
public enum ParamFormat {

    RAW(0), JAVA_SERIALIZATION(1), JSON(2),;
    
    /**
     * int类型标识的format 
     */
    private final int value;
    
    private ParamFormat(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static ParamFormat getByValue(int value) {
        if (value == 1) {
            return JAVA_SERIALIZATION;
        }
        if (value == 2) {
            return JSON;
        }
        return RAW;
    }
}
