package com.laxser.tentaclex.commons.binaryparam.impl.encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laxser.tentaclex.commons.binaryparam.Encoder;
import com.laxser.tentaclex.commons.binaryparam.Encoding;

/**
 * 
 * @author laxser
 * @ contact qifan.du@renren-inc.com
 * date: 2012-5-31
 */
public class JsonEncoder implements Encoder{

    private static ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        //设置ObjectMapper只序列化非null的属性，这样可以节省流量
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
   
        //disable掉FAIL_ON_UNKNOWN_PROPERTIES属性，增强容错性；因为现在有从客户端发json到服务端，在服务端反解的情况了，
        //所以要加上这个容错选项。
       
       mapper.disable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    @Override
    public Object decode(byte[] data) {

        try {
            ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(data));
            int classNameLength = oin.readInt();
            byte[] buff = new byte[classNameLength];
            oin.read(buff);
            String className = new String(buff, Encoding.DEFAULT);
            Class<?> klass = Class.forName(className);
            return mapper.readValue(oin, klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public byte[] encode(Object value) {
        
        
        try {
            String className = value.getClass().getName();
            //TODO 这里最好能动态预测长度
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            byte[] classNameInBytes = className.getBytes(Encoding.DEFAULT);
            oos.writeInt(classNameInBytes.length);
            oos.write(classNameInBytes);
            mapper.writeValue(oos, value);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
