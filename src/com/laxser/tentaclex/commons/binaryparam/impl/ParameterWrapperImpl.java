package com.laxser.tentaclex.commons.binaryparam.impl;


import java.util.ArrayList;
import java.util.List;

import com.laxser.tentaclex.commons.binaryparam.ParamFormat;
import com.laxser.tentaclex.commons.binaryparam.ParameterWrapper;


/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:54:35
 */
public class ParameterWrapperImpl implements ParameterWrapper {

    private ParamFormat format;
    
    private String key;
    
    private List<Object> values;
    
    @Override
    public ParamFormat getFormat() {
        return format;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public List<Object> getValues() {
        return values;
    }
    
    public void setFormat(ParamFormat format) {
        this.format = format;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setValues(List<Object> values) {
        this.values = values;
    }
    
    public void addValue(Object value) {
        if (values == null) {
            values = new ArrayList<Object>();
        }
        values.add(value);
    }
}
