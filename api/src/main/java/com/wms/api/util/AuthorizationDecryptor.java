package com.wms.api.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class AuthorizationDecryptor {
    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";
    private static final String CRYPT_KEY = "q3tVm4n9l2k8j7s1";

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(CRYPT_KEY.getBytes(CHARSET), ALGORITHM);
    }

    //加密
    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes(CHARSET));
        return Base64.encodeBase64String(encryptedByteValue);
    }

    //解密
    public static String decrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decodeBase64(value.getBytes());
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue, CHARSET);
    }

    public static String anotherDecrypt(String value) throws Exception {
        String[] parts = value.split("&");
        String encryptedBase64 = parts[0];
        String ivBase64 = parts[1];

        byte[] ivBytes = Base64.decodeBase64(ivBase64);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        byte[] encryptedBytes = Base64.decodeBase64(encryptedBase64);

        // 获取密钥
        byte[] keyBytes = CRYPT_KEY.getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 使用相同的密钥和IV初始化Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // 解密数据，返回原始明文
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
