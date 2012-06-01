package com.laxser.tentaclex.commons.binaryparam;

/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:54:25
 */
public interface Encoder {

    public byte[] encode(Object value);
 
    public Object decode(byte[] data);
    
}
