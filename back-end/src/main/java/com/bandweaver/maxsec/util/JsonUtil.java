package com.bandweaver.maxsec.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    public static <T> T readJsonFromClassPath(String path) throws IOException {

        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            return JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, JSONObject.class,
                    Feature.AutoCloseSource,
                    // 允许注释
                    Feature.AllowComment,
                    // 允许单引号
                    Feature.AllowSingleQuotes,
                    // 使用 Big decimal
                    Feature.UseBigDecimal);
        } else {
            throw new IOException();
        }
    }
}
