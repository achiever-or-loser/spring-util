package com.csc.spring.common.utils;

import com.csc.spring.common.utils.io.FileUtils;

/**
 * @Description: 计算java对象的大小
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class ObjectSizeUtil {

    /**
     * @Description 计算对象占用内存大小，单位：字节
     * @Version 1.0
     */
    public static long getObjectSize(Object o) {
        if (null == o) {
            return 0;
        }
        byte[] obyte = JSONUtils.toByteArray(o);
        return obyte.length;
    }

    /**
     * @Description 计算对象占用内存大小，数值后带单位：GB, MB, KB or bytes
     * @Version 1.0
     */
    public static String getObjectSizeUnit(Object o) {
        return FileUtils.byteCountToDisplaySize(getObjectSize(o));
    }
}
