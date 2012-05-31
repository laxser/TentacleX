package com.laxser.tentaclex.commons.binaryparam;

import java.util.List;


public interface ParameterWrapper {

    public String getKey();
    
    public List<Object> getValues();
    
    public ParamFormat getFormat();
    
    public void addValue(Object value);
}
