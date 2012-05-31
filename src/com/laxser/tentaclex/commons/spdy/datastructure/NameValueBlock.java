package com.laxser.tentaclex.commons.spdy.datastructure;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * 
 * <pre>
 *   +----------------------------------+
 *   |     Length of name (int16)       |
 *   +----------------------------------+
 *   |           Name (string)          |
 *   +----------------------------------+
 *   |     Length of value  (int16)     |
 *   +----------------------------------+
 *   |          Value   (string)        |
 *   +----------------------------------+
 *   |           (repeats)              |
 * 
 * </pre>
 * 
 * @author Lookis (lookisliu@gmail.com)
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 */
public class NameValueBlock implements Map<String, String>, Serializable {

    private static final long serialVersionUID = 1L;

    private static int NAME_LENGTH_IN_BYTE = 2;

    private static int VALUE_LENGTH_IN_BYTE = 2;

    private static final int LENGTH_OF_LENGTH_IN_BYTE = 2;
    
    private Map<String, String> data = new HashMap<String, String>();

    /**
     * 默认构造函数
     */
    public NameValueBlock() {

    }
    
    /**
     * 从给定的字节数组中还原出{@link NameValueBlock}实例
     * @param fromByteStructure
     * @deprecated 这种构造方式使用了大量的内存copy，不够高效，
     * 请使用NameValueBlock(byte[] data, int offset, int length)
     * 来替代
     */
    public NameValueBlock(byte[] fromByteStructure) {

        for (int index = 0; index < fromByteStructure.length;) {
            //get name length 2bytes
            byte[] nameLengthInByte = Arrays.copyOfRange(fromByteStructure, index, index
                    + LENGTH_OF_LENGTH_IN_BYTE);
            int nameLength = Utils.bytesToInt(nameLengthInByte);
            index += LENGTH_OF_LENGTH_IN_BYTE;//skip length of length

            //get name String
            byte[] nameStringInByte = Arrays.copyOfRange(fromByteStructure, index, index
                    + nameLength);
            String nameString = new String(nameStringInByte);
            index += nameLength;//skip name length

            //get value length
            byte[] valueLengthInByte = Arrays.copyOfRange(fromByteStructure, index, index
                    + LENGTH_OF_LENGTH_IN_BYTE);
            int valueLength = Utils.bytesToInt(valueLengthInByte);
            index += LENGTH_OF_LENGTH_IN_BYTE;//skip length of length

            //get value String
            byte[] valueStringInByte = Arrays.copyOfRange(fromByteStructure, index, index
                    + valueLength);
            String valueString = new String(valueStringInByte);
            index += valueLength;//skip value length

            put(nameString, valueString);
        }
    }
    
    /**
     * 从给定的字节数组中还原出{@link NameValueBlock}实例
     * 
     * @param data 数据
     * @param offset 起始
     * @param length 长度
     */
    public NameValueBlock(byte[] data, int offset, int length) {
    	int end = offset + length;
    	while (offset < end) {
    		//get name length 2bytes
    		int nameLength = Utils.bytesToInt(data, offset, LENGTH_OF_LENGTH_IN_BYTE);
    		offset += LENGTH_OF_LENGTH_IN_BYTE;
    		
    		//get name String
    		String name = new String(data, offset, nameLength);
    		offset += nameLength;
    		
    		//get value length
    		int valueLength = Utils.bytesToInt(data, offset, LENGTH_OF_LENGTH_IN_BYTE); 
    		offset += LENGTH_OF_LENGTH_IN_BYTE;
    		
    		//get value String
    		String value = new String(data, offset, valueLength);
    		offset += valueLength;
    		
    		put(name, value);
    	}
    }
    
    public void clear() {
        data.clear();
    }

    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return data.entrySet();
    }

    public boolean equals(Object o) {
        return data.equals(o);
    }

    public String get(Object key) {
        return data.get(key);
    }

    public int hashCode() {
        return data.hashCode();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    public String put(String key, String value) {
        int nameLengthMax = (1 << NAME_LENGTH_IN_BYTE * 8) - 1;//Name 最大字节数
        Utils.checkLength(key, nameLengthMax * 8);//转成位来比较
        int valueLengthMax = (1 << VALUE_LENGTH_IN_BYTE * 8) - 1;
        Utils.checkLength(value, valueLengthMax * 8);
        return data.put(key, value);
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        int nameLengthMax = (1 << NAME_LENGTH_IN_BYTE * 8) - 1;//Name 最大字节数
        int valueLengthMax = (1 << VALUE_LENGTH_IN_BYTE * 8) - 1;
        for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
            Utils.checkLength(entry.getKey(), nameLengthMax * 8);//转成位来比较
            Utils.checkLength(entry.getValue(), valueLengthMax * 8);
        }
        data.putAll(m);
    }

    public String remove(Object key) {
        return data.remove(key);
    }

    public int size() {
        return data.size();
    }

    public Collection<String> values() {
        return data.values();
    }
    
    /**
     * @return
     * @deprecated 老版的toByteStructure方法，现在仅用于做对比测试
     */
    public byte[] toByteStructure0() {
        byte[] result = new byte[0];

        for (Entry<String, String> entry : data.entrySet()) {
            //name
            byte[] fixSizeName = entry.getKey().getBytes();
            byte[] nameLengthInByte = Utils.intToByte(fixSizeName.length);
            //only 2 bytes
            byte[] fixSizeNameLength = Utils.trimBytes(nameLengthInByte, 0, NAME_LENGTH_IN_BYTE);
            //value
            byte[] fixSizeValue = entry.getValue().getBytes();
            byte[] valueLengthInByte = Utils.intToByte(fixSizeValue.length);
            byte[] fixSizeValueLength = Utils.trimBytes(valueLengthInByte, 0, VALUE_LENGTH_IN_BYTE);
            //store
            //data存放时已经加入长度判断，这里不再判断长度
            result = Utils.mergeBytes(result, fixSizeNameLength, fixSizeName, fixSizeValueLength,
                    fixSizeValue);
        }
        return result;
    }
    
    /**
     * {@link NameValueBlock}按照SPDY协议的格式转化为
     * byte数组后的长度
     * @return
     */
    public int getLength() {
    	int length = 0;
    	for (Entry<String, String> entry : data.entrySet()) {
    		length += 2;	//length of name, 2 bytes
    		length += entry.getKey().length();
    		length += 2;	//length of value, 2 bytes
    		length += entry.getValue().length();
    	}
    	return length;
    }
    
    /**
     * 
     * 将这个NameValueBlock按SPDY的规范转化为byte数组的形式。
     * 
     * 第一版的实现在toByteStructure0里，本实现是对其的优化，
     * 减少了构造和copy byte[]数组的次数，经过测试，性能大概能提高
     * 10%左右
     * 
     * @author Li Weibo, 2010-04-07
     * 
     * @return
     */
    public byte[] toByteStructure() {
    	int length = getLength();
    	byte[] res = new byte[length];
    	int offset = 0;
    	writeToByteStructure(res, offset);
    	return res;
    }
    
    /**
     * 将这个NameValueBlock按SPDY的规范转化为字节并写入指定byte数组
     * @param dest
     * @param offset
     */
    public void writeToByteStructure(byte[] dest, int offset) {
    	for (Entry<String, String> entry : data.entrySet()) {
    		offset = writeFieldTo(entry.getKey(), dest, offset);
    		offset = writeFieldTo(entry.getValue(), dest, offset);
    	}
    }
    
    private int writeFieldTo(String field, byte[] dest, int offset) {
    	byte[] lengthInBytes = Utils.intToByte(field.length());
    	dest[offset++] = lengthInBytes[2];
    	dest[offset++] = lengthInBytes[3];
    	System.arraycopy(field.getBytes(), 0, dest, offset, field.length());
    	offset += field.length();
    	return offset;
    }
} 
