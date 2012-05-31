package com.laxser.tentaclex.commons.binaryparam;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class MultiFormatParamEncoder {
    
    /**
     * 
     * 
     * @param params
     * @param os
     * @throws IOException
     */
    public void encodeToStream(Map<String, ParameterWrapper> params, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        for (Entry<String, ParameterWrapper> entry : params.entrySet()) {
            
            String key = entry.getKey();
            ParameterWrapper wrapper = entry.getValue();
            
            byte[] keyBytes = key.getBytes();
            List<Object> values = wrapper.getValues();
            for (Object value : values) {
                if (value != null) {
                  //key length
                    oos.writeInt(keyBytes.length);
                    //key data
                    oos.write(key.getBytes(Encoding.DEFAULT));
                    
                    //format
                    oos.writeInt(wrapper.getFormat().getValue());
                    
                    byte[] data = Encoders.encode(wrapper.getFormat(), value);
                    //value length
                    oos.writeInt(data.length);
                    //value data
                    oos.write(data);
                }
                //loop
            }
        }
        oos.flush();    //必须要flush
    }
    
    public Map<String, List<Object>> decodeFromStream(InputStream is) throws IOException {
        Map<String, List<Object>> paramMap = new HashMap<String, List<Object>>();
        
        ObjectInputStream ois = new ObjectInputStream(is);
        while(ois.available() > 0) {   
            int keyLength = ois.readInt();
            
            byte[] keyData = new byte[keyLength];
            ois.read(keyData);
            String key = new String(keyData, Encoding.DEFAULT);
            
            int formatValue = ois.readInt();
            ParamFormat format = ParamFormat.getByValue(formatValue);
            
            int valueLength = ois.readInt();
            byte[] valueData = new byte[valueLength];
            ois.read(valueData);
            
            Object value = Encoders.decode(format, valueData);
            
            if (paramMap.get(key) != null) {
                paramMap.get(key).add(value);
            } else {
                List<Object> list = new LinkedList<Object>();
                list.add(value);
                paramMap.put(key, list);
            }
        }
        return paramMap;
    }
    
    public static void main(String[] args) {
        
        System.out.println(ParamFormat.JAVA_SERIALIZATION);
    }
}
