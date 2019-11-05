package com.gram.gram_landlord_assistant.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;

public class DesUtil {
    private static Base64.Encoder encoder = Base64.getMimeEncoder();
    private static Base64.Decoder decoder = Base64.getMimeDecoder();


    public static String DESEncrypt(String msg, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "DES"), new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = cipher.doFinal(msg.getBytes());
            return encoder.encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DESDecrypt(String decryptStr, String key, String iv) {
        try {
            byte[] decryptBytes = decoder.decode(decryptStr);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "DES"), new IvParameterSpec(iv.getBytes()));
            byte[] original = cipher.doFinal(decryptBytes);
            return new String(original, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String clearText = "田所浩二喜欢蔡徐坤";
        //key和iv的长度必须是8
        String key = "12345678";
        String iv = "87654321";
        System.out.println("明文: " + clearText + "\n密钥: " + key);
        String encryptText = DESEncrypt(clearText, key, iv);
        System.out.println("加密后: " + encryptText);
        String decryptText = DESDecrypt(encryptText, key, iv);
        System.out.println("解密后: " + decryptText);
    }
}
