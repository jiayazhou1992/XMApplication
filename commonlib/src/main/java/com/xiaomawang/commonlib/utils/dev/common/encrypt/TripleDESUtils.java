package com.xiaomawang.commonlib.utils.dev.common.encrypt;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * detail: 3DES对称加密(Triple DES、DESede，进行了三重DES加密的算法，对称加密算法)
 * Created by Ttt
 */
public final class TripleDESUtils {

    private TripleDESUtils() {
    }

    // 日志TAG
    private static final String TAG = TripleDESUtils.class.getSimpleName();

    /**
     * 生成密钥
     * @return
     */
    public static byte[] initKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
            keyGen.init(168);  // 112 168
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "initKey");
        }
        return null;
    }

    /**
     * 3DES 加密
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key){
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherBytes = cipher.doFinal(data);
            return cipherBytes;
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "encrypt");
        }
        return null;
    }

    /**
     * 3DES 解密
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key){
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainBytes = cipher.doFinal(data);
            return plainBytes;
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "decrypt");
        }
        return null;
    }

}
