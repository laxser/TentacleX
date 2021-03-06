package com.laxser.tentaclex.commons.binaryparam;

import java.util.HashMap;
import java.util.Map;

import com.laxser.tentaclex.commons.binaryparam.impl.encoder.JavaSerializationEncoder;
import com.laxser.tentaclex.commons.binaryparam.impl.encoder.JsonEncoder;
import com.laxser.tentaclex.commons.binaryparam.impl.encoder.RawEncoder;


/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:54:20
 */

public class Encoders {

	/**
	 * @author laxser  Date 2012-6-4 下午3:08:47
@contact [duqifan@gmail.com] //leap ahead
@Encoders.java

	 */
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
