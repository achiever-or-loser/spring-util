package com.csc.spring.common.utils.calculation;

import com.csc.spring.common.constant.CharsetInfo;
import com.csc.spring.common.enums.ApplicationStatus;
import com.csc.spring.common.exception.BusinessException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP压缩及解压缩工具类
 */
public class GZIPUtils {

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str
     * @return
     */
    public static byte[] compress(String str) {
        return compress(str, CharsetInfo.UTF8);
    }

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str      字符串
     * @param encoding 编码
     * @return 压缩后的字节数组
     */
    public static byte[] compress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), "字符串压缩异常");
        }
        return out.toByteArray();
    }

    /**
     * GZIP解压缩
     *
     * @param bytes
     * @return
     */
    public static byte[] decompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), "字符串解压缩异常");
        }
        return out.toByteArray();
    }

    /**
     * 将字节数组解压缩为字符串
     *
     * @param bytes 字节数组
     * @return
     */
    public static String decompressToString(byte[] bytes) {
        return decompressToString(bytes, CharsetInfo.UTF8);
    }

    /**
     * 将字节数组解压缩为字符串
     *
     * @param bytes    字节数组
     * @param encoding 编码
     * @return
     */
    public static String decompressToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT_EXCEPTION.getCode(), "字符串解压缩异常");
        }
    }
}
