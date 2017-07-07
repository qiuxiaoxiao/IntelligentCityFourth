package com.jiangda.qiucheng.birdspreliminary;

/**
 * Created by qiucheng on 2017/7/3.
 */

public class Utils {
    static byte[] stringConvert2ByteArray(String str) {
        String[] str_array = str.split(" ");
        byte[] result = new byte[str_array.length];
        for (int i = 0; i < str_array.length; i++) {
            String tmp = str_array[0];
            byte tmp_byte = 0x00;
            tmp_byte = (byte) Integer.parseInt(tmp, 16);
            result[i] = tmp_byte;
        }
        return result;
    }

    static String byteConvert2String(byte[] data, int start, int end) {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < end; i++) {
//            sb.append("0x");
            String tmp = Integer.toHexString(data[i]);
            if (tmp.length() > 2)
                tmp = tmp.substring(0, 2);
            sb.append(tmp.length() == 1 ? "0" + tmp : tmp);
            sb.append(" ");
        }
        return sb.toString();
    }


    private static String hexStr =  "0123456789ABCDEF";
    public static byte[] hexStr2BinArr(String hexString){
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length()/2;
        byte[] bytes = new byte[len];
        byte high = 0;//字节高四位
        byte low = 0;//字节低四位
        for(int i=0;i<len;i++){
            //右移四位得到高位
            high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
            low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
            bytes[i] = (byte) (high|low);//高地位做或运算
        }
        return bytes;
    }
}
