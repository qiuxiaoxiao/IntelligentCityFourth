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
}
