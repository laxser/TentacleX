package com.laxser.tentaclex.commons.binaryparam;


/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:55:39
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
