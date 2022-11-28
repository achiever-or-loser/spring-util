package com.csc.spring.common.utils.cryption;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.Signature;

/**
 * @Description: 使用签名的主要作用是为了防止发送的信息被串改，防止A发送到B的消息被其他人修改后再发送给B
 * 借助RSAUtil对公钥私钥的处理
 * @Author: csc
 * @Create: 2022-11-25
 * @Version: 1.0
 */
public class SignatureUtils {
    public static final String MD5_RSA = "MD5withRSA";

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     */
    public static String sign(String data, String privateKey) throws Exception {
        Signature signature = Signature.getInstance(MD5_RSA);
        signature.initSign(RSAUtils.getPrivateKey(privateKey));
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64URLSafe(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     */
    public static boolean verify(String srcData, String publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance(MD5_RSA);
        signature.initVerify(RSAUtils.getPublicKey(publicKey));
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64URLSafe(sign));
    }

    public static void main(String[] args) throws Exception {
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" + "有关的命令，但看到大批盛装的官员来临，也就";

        System.out.println("私钥签名——公钥验证：A->B(第三方商户)");
        // RSA签名
        String sign = sign(str, RSAUtils.PRIVATE_KEY);
        System.out.println(sign);
        // RSA验签
        boolean result = verify(str, RSAUtils.PUBLIC_KEY, sign);
        System.out.println(result);
    }
}
