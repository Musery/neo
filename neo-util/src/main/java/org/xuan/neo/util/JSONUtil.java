package org.xuan.neo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TimeZone;

public class JSONUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 忽略空字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 增加LocalDateTime序列化配置
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 获取对应序列化用ObjectMapper
     */
    private static ObjectMapper getOM() {
        return objectMapper;
    }

    public static Map toMap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof Map) {
            return (Map) obj;
        }
        if (obj instanceof String) {
            return toObject((String) obj, Map.class);
        }
        return getOM().convertValue(obj, Map.class);
    }

    /**
     * 对象转化成JSON格式字符串
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            ObjectMapper om = getOM();
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON格式数据转化成对象
     */
    public static <T> T toObject(String json, Class<T> valueType) {
        try {
            if (null == json) {
                return null;
            }
            if (valueType.equals(String.class)) {
                return (T) json;
            }
            ObjectMapper om = getOM();
            return om.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON格式字符串转化对象(深度转化)
     */
    public static <T> T deepToObject(String json, TypeReference<T> valueTypeRef) {
        try {
            ObjectMapper om = getOM();
            return om.readValue(json, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deepToObject(InputStream stream, TypeReference<T> valueTypeRef) {
        try {
            ObjectMapper om = getOM();
            return om.readValue(stream, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
