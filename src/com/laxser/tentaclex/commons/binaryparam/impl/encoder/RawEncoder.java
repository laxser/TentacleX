package com.laxser.tentaclex.commons.binaryparam.impl.encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.laxser.tentaclex.commons.binaryparam.Encoder;
import com.laxser.tentaclex.commons.binaryparam.Encoding;


public class RawEncoder implements Encoder{

    @Override
    public Object decode(byte[] data) {
        try {
            String value = new String(data, Encoding.DEFAULT);
            return urlDecode(value);
        } catch (UnsupportedEncodingException e) {
          //code never reach here
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encode(Object value) {
        String svalue = urlEncode(value.toString());
        try {
            return svalue.getBytes(Encoding.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            //code never reach here
            throw new RuntimeException(e);
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //code never reach here
        }
        return null;
    }
    
    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //code never reach here
        }
        return null;
    }

}
