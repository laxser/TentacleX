package com.laxser.tentaclex.commons.binaryparam;


public interface Encoder {

    public byte[] encode(Object value);
 
    public Object decode(byte[] data);
    
}
