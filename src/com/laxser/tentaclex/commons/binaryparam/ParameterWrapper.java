package com.laxser.tentaclex.commons.binaryparam;

import java.util.List;

/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:55:28
 */
public interface ParameterWrapper {

    public String getKey();
    
    public List<Object> getValues();
    
    public ParamFormat getFormat();
    
    public void addValue(Object value);
}
