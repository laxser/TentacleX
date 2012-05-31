package com.laxser.tentaclex.commons.binaryparam;

import java.util.HashMap;
import java.util.Map;

import com.laxser.tentaclex.commons.binaryparam.impl.encoder.JavaSerializationEncoder;
import com.laxser.tentaclex.commons.binaryparam.impl.encoder.JsonEncoder;
import com.laxser.tentaclex.commons.binaryparam.impl.encoder.RawEncoder;


public class Encoders {

    private static Map<ParamFormat, Encoder> encoders = new HashMap<ParamFormat, Encoder>();

    static {
        registerEncoder(ParamFormat.RAW, new RawEncoder());
        registerEncoder(ParamFormat.JAVA_SERIALIZATION, new JavaSerializationEncoder());
        registerEncoder(ParamFormat.JSON, new JsonEncoder());
    }
    
    public static byte[] encode(ParamFormat format, Object value) {
        Encoder encoder = encoders.get(format);
        return encoder.encode(value);
    }

    public static Object decode(ParamFormat format, byte[] data) {
        Encoder encoder = encoders.get(format);
        return encoder.decode(data);
    }
    
    public static void registerEncoder(ParamFormat format, Encoder encoder) {
        encoders.put(format, encoder);
    }
}
