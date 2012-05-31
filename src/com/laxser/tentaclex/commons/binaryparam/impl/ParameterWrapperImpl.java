package com.laxser.tentaclex.commons.binaryparam.impl;


import java.util.ArrayList;
import java.util.List;

import com.laxser.tentaclex.commons.binaryparam.ParamFormat;
import com.laxser.tentaclex.commons.binaryparam.ParameterWrapper;


/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-5-26 上午11:55:49
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
