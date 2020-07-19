package com.bandweaver.maxsec.util;

import org.springframework.lang.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtil {
    public static String joinInteger(List<Integer> list, String separator) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(separator));
    }

    public static String joinInteger(List<Integer> list) {
        return joinInteger(list, ",");
    }

    public static String joinString(List<String> list, String separator) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(separator));
    }

    public static String joinString(List<String> list) {
        return joinString(list, ",");
    }

    public static String byteToString(byte[] bytes, @Nullable String code) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        if (code == null || code.isEmpty()) {
            code = "utf-8";
        }
        String strContent = "";
        try {
            strContent = new String(bytes, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strContent.trim();
    }

    public static byte[] stringToByte(String str, @Nullable String code) {
        byte[] retult = null;
        if (code == null || code.isEmpty()) {
            code = "utf-8";
        }
        String strContent = "";
        try {
            retult = str.getBytes(code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retult;
    }
}
