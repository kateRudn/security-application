package com.example.security_app;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SharedClass {
    public static ControlSQL dbSQL;
    public static String key;
    public static String vector="4444fhfhdjdjfjfjfkfffwr4t45464";
    public static String block="fhgodksr";

    public static byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

    public static String Encrypt(String str, String key)
    {
        SecretKeySpec sks = null;
        try {
            sks = new SecretKeySpec(genKey(key), "BLOWFISH");
        } catch (Exception e) {
            Log.e("Crypto", "BLOWFISH secret key spec error-1");
        }
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("BLOWFISH/OFB/NoPadding");
            IvParameterSpec ivParams = new IvParameterSpec(block.getBytes());
            c.init(Cipher.ENCRYPT_MODE, sks, ivParams);
            encodedBytes = c.doFinal(str.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e("Crypto", "BLOWFISH encryption error");
        }
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);

    }

    public static String Decrypt (String str, String key) throws Exception {
        SecretKeySpec sks = null;
        byte[] encryptedBytes=Base64.decode(str, Base64.DEFAULT);
        try {
            sks = new SecretKeySpec(genKey(key), "BLOWFISH");
        } catch (Exception e) {
            Log.e("Crypto", "BLOWFISH secret key spec error-2");
        }
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("BLOWFISH/OFB/NoPadding");
            IvParameterSpec ivParams = new IvParameterSpec(block.getBytes());
            c.init(Cipher.DECRYPT_MODE, sks, ivParams);
            decodedBytes = c.doFinal(encryptedBytes);
        } catch (Exception e) {
            Log.e("Crypto", "BLOWFISH decryption error");
        }
        return new String (decodedBytes, "UTF-8");
    }

    public static byte[] genKey(String str_key) {
        int iterationCount = 100;
        int keyLength = 128;
        int saltLength =  8;

        byte[] salt = new byte[saltLength];
        KeySpec keySpec = new PBEKeySpec(str_key.toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return keyBytes;
    }
}
