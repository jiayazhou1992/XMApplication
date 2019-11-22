package com.xiaomawang.commonlib.utils.dev.common.encrypt;

import android.util.Log;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * detail: AES对称加密(Advanced Encryption Standard，高级数据加密标准，AES算法可以有效抵制针对DES的攻击算法，对称加密算法)
 * Created by Ttt
 */
public final class AESUtils {

    private AESUtils() {
    }

    // 日志TAG
    private static final String TAG = AESUtils.class.getSimpleName();

    /**
     * 生成密钥
     * @return
     */
    public static byte[] initKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(192);  //192 256
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "initKey");
        }
        return null;
    }

    /**
     * 生成密钥
     * @return
     */
    public static byte[] initKey(String key){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(192,new SecureRandom(key.getBytes()));  //192 256
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "initKey");
        }
        return null;
    }

    /**
     * AES 加密
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key){
        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherBytes = cipher.doFinal(data);
            return cipherBytes;
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "encrypt");
        }
        return null;
    }

    /**
     * AES 解密
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key){
        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainBytes = cipher.doFinal(data);
            return plainBytes;
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "decrypt");
        }
        return null;
    }

    /* 加密 */
    private static byte[] encryptVoice(String seed, byte[] clearbyte)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt2(rawKey, clearbyte);
        return result;
    }

    /* 解密 */
    private static byte[] decryptVoice(String seed, byte[] encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = decrypt2(rawKey, encrypted);
        return result;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt2(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt2(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    /**
     * 加密 - AES
     */
    public static void Encrypt(String Path, String seed) {
//      Log.d("加密 - AES", "----加密开始时间: " + new Date().getTime()/1000);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean isSuccess = true;
        try {
            File oldFile = new File(Path);
            fis = new FileInputStream(oldFile);
            byte[] oldByte = new byte[(int) oldFile.length()];
            fis.read(oldByte); // 读取
            byte[] newByte = AESUtils.encryptVoice(seed, oldByte);
            // 加密
            fos = new FileOutputStream(oldFile);
            fos.write(newByte);

        } catch (FileNotFoundException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isSuccess)
            Log.d("加密 - AES", "加密成功");
        else
            Log.d("加密 - AES", "加密失败");
//      Log.d("加密 - AES", "----加密结束时间: " + new Date().getTime()/1000);
//      Log.i("加密 - AES", "保存成功");
    }


    /**
     * 解密 - AES
     */
    public static String Decrypt(String playerPath, String seed) {
//      og.d("解密 - AES", "----解密开始时间: " + new Date().getTime());
        File oldFile = new File(playerPath);
        String parent = oldFile.getParent();
        String[] name = oldFile.getName().split("\\.");
        File tempFile = new File(parent + File.separator + name[0] + "_tem." + name[1]);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean isSuccess = true;
        byte[] oldByte = new byte[(int) oldFile.length()];
        try {
            fis = new FileInputStream(oldFile);
            fis.read(oldByte);
            byte[] newByte = AESUtils.decryptVoice(seed, oldByte);
            // 解密
            fos = new FileOutputStream(tempFile);
            fos.write(newByte);

        } catch (FileNotFoundException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        try {
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//      Log.d("解密 - AES", "----解密结束时间: " + new Date().getTime());
        if (isSuccess) {
            Log.i("解密 - AES", "解密成功");
            return tempFile.getPath();
        } else {
            Log.i("解密 - AES", "解密失败");
            return "";
        }
    }
}