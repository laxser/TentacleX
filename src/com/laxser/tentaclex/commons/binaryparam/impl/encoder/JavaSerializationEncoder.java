package com.laxser.tentaclex.commons.binaryparam.impl.encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.laxser.tentaclex.commons.binaryparam.Encoder;
/**
 * 
 * @author laxser
 * @ contact qifan.du@renren-inc.com
 * date: 2012-5-31
 */
public class JavaSerializationEncoder implements Encoder {

    @Override
    public Object decode(byte[] data) {

        try {
            ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(data));
            return oi.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public byte[] encode(Object value) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bo);
            os.writeObject(value);
            return bo.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
