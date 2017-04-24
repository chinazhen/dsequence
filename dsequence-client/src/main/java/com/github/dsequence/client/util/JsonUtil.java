package com.github.dsequence.client.util;

import com.alibaba.fastjson.JSONObject;

import java.util.List;


/**
 * Json 对象转换
 * <p>
 * 1、值如果是对象转成json,如果是字符串不变
 * 2、将字符串转为对象
 * 3、将字符串转为集合对象
 * </p>
 * User: xiewenda Date: 2016/3/22 ProjectName: commons Version: 1.0
 */
public class JsonUtil {

    /**
     * 值如果是对象转成json,如果是字符串不变
     *
     * @param obj 值
     * @return 值
     */
    public static String toJSONString(Object obj) {
        final String value;
        if (obj instanceof String) {
            value = obj + "";
        } else {
            value = JSONObject.toJSONString(obj);
        }
        return value;
    }

    /**
     * 将字符串转为对象
     *
     * @param key   字符串
     * @param clazz 目标类类型
     * @return 转换后的对象
     */
    public static <T> T toObject(String key, Class<T> clazz) {
        return JSONObject.parseObject(key, clazz);
    }

    /**
     * 将字符串转为集合对象
     *
     * @param key   字符串
     * @param clazz 目标类类型
     * @return 转换后的对象
     */
    public static <T> List<T> toList(String key, Class<T> clazz) {
        return JSONObject.parseArray(key, clazz);
    }
}
