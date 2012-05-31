/**
 * 
 */
package com.laxser.tentaclex.commons.spdy;

import java.util.Arrays;

/**
 * @author Lookis (lookisliu@gmail.com)
 * 
 */
public class Utils {

    public static byte[] intToByte(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) (i >> 24);
        return bt;
    }

    public static int bytesToInt(byte[] bytesSource) {
    	int num = 0;
        num |= (0xFF & bytesSource[0]);
        int i = 0;
        while (i++ < bytesSource.length - 1) {
            num = (num << 8) | (0xFF & bytesSource[i]);
        }
        return num;
    }
    
    /**
     * 将bytesSource中从offset开始，长度为length的一段subarray转换为int，
     * 高位在前
     * 
     * @param bytesSource
     * @param offset
     * @param length
     * @return
     */
    public static int bytesToInt(byte[] bytesSource, int offset, int length) {
    	
    	if (length <= 0 || length > 4) {
    		throw new IllegalArgumentException("Expected range of length is (0,4], but the input is:" + length);
    	}
    	int num = 0;
        num |= (0xFF & bytesSource[offset]);
        int i = 0;
        while (i++ < length - 1 && i + offset < bytesSource.length) {
        	num = (num << 8) | (0xFF & bytesSource[i + offset]);
        }
        return num;
    }
    
    public static byte[] mergeBytes(byte[]... bytes) {
        int totalLength = 0;
        for (byte[] bs : bytes) {
            totalLength += bs.length;
        }
        byte[] result = new byte[totalLength];
        
        //用arraycopy来进行字符copy
        int offset = 0;
        for (byte[] bs : bytes) {
        	System.arraycopy(bs, 0, result, offset, bs.length);
        	offset += bs.length;
        }
        
        /*int index = 0;

        for (int i = 0; i < bytes.length; i++) {
            byte[] bs = bytes[i];
            for (byte b : bs) {
                result[index++] = b;
            }
        }*/

        return result;
    }

    public static byte[] trimBytes(byte[] source, int fromIndex, int toIndex) {
        int from = source.length - toIndex;
        int to = source.length - fromIndex;
        return Arrays.copyOfRange(source, from, to);
    }

    public static void checkLength(String source, int bitCount) throws IllegalArgumentException {

        int sourceLengthInByte = source.getBytes().length * 8;

        if (sourceLengthInByte > bitCount || sourceLengthInByte < 0) throw new IllegalArgumentException(
                "Invalid Arguments ! Args:" + source + " suppose to be " + bitCount + "bit");
    }

    public static void checkLength(int source, int bitCount) throws IllegalArgumentException {
        if (bitCount >= 32) {
        	throw new IllegalArgumentException("max bit count 31, yours is " + bitCount);
        }
    	int supposeMax = (1 << bitCount) - 1;
        if (source > supposeMax || source < 0) throw new IllegalArgumentException(
                "Invalid Arguments ! Args:" + source + " suppose to be " + bitCount + "bit");
    }

    public static int bitAlignToByte(int bitCount) {
        int byteCount = (bitCount / 8) + (bitCount % 8 == 0 ? 0 : 1);
        return byteCount;
    }

    public static String readByte(byte[] bytes) {
        return new String(bytes);
    }
} 
