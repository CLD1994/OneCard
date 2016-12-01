package com.zhuoli.onecard.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author mcxiaoke
 * @version 1.0 2013.03.16
 */
public final class CryptoUtils {
    public static final String TAG = CryptoUtils.class.getSimpleName();
    public static final String ENC_UTF8 = Charsets.ENCODING_UTF_8;

    private CryptoUtils() {
    }

    static String getRandomString() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(random.nextLong());
    }

    static byte[] getRandomBytes(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }

    static byte[] getRawBytes(String text) {
        try {
            return text.getBytes(ENC_UTF8);
        } catch (UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }

    static String getString(byte[] data) {
        try {
            return new String(data, ENC_UTF8);
        } catch (UnsupportedEncodingException e) {
            return new String(data);
        }
    }

    static byte[] base64Decode(String text) {
        return Base64.decode(text, Base64.NO_WRAP);
    }

    static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static final class AES {
        static final int ITERATION_COUNT_DEFAULT = 100;
        static final int KEY_SIZE_DEFAULT = 256;
        static final int IV_SIZE_DEFAULT = 16;
        static final String KEY_AES_SPEC = "AES/CBC/PKCS7Padding";

        public static String encrypt(String text) {
            return encrypt(text, getSimplePassword(), getSimpleSalt(),
                    getSimpleIV());
        }

        public static String decrypt(String text) {
            return decrypt(text, getSimplePassword(), getSimpleSalt(),
                    getSimpleIV());
        }

        public static byte[] encrypt(byte[] data) {
            return encrypt(data, getSimplePassword(), getSimpleSalt(),
                    getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data) {
            return decrypt(data, getSimplePassword(), getSimpleSalt(),
                    getSimpleIV(), KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password) {
            return encrypt(text, password, getSimpleSalt(), getSimpleIV());
        }

        public static String decrypt(String text, String password) {
            return decrypt(text, password, getSimpleSalt(), getSimpleIV());
        }

        public static byte[] encrypt(byte[] data, String password) {
            return encrypt(data, password, getSimpleSalt(), getSimpleIV(),
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password) {
            return decrypt(data, password, getSimpleSalt(), getSimpleIV(),
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password, byte[] salt) {
            return encrypt(text, password, salt, getSimpleIV());
        }

        public static String decrypt(String text, String password, byte[] salt) {
            return decrypt(text, password, salt, getSimpleIV());
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt) {
            return encrypt(data, password, salt, getSimpleIV(),
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt) {
            return decrypt(data, password, salt, getSimpleIV(),
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public static String encrypt(String text, String password, byte[] salt,
                                     byte[] iv) {
            byte[] data = getRawBytes(text);
            byte[] encryptedData = encrypt(data, password, salt, iv,
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
            return base64Encode(encryptedData);
        }

        public static String decrypt(String text, String password, byte[] salt,
                                     byte[] iv) {
            byte[] encryptedData = base64Decode(text);
            byte[] data = decrypt(encryptedData, password, salt, iv,
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
            return getString(data);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv) {
            return encrypt(data, password, salt, iv, KEY_SIZE_DEFAULT,
                    ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv) {
            return decrypt(data, password, salt, iv, KEY_SIZE_DEFAULT,
                    ITERATION_COUNT_DEFAULT);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv, int keySize) {
            return encrypt(data, password, salt, iv, keySize,
                    ITERATION_COUNT_DEFAULT);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv, int keySize) {
            return decrypt(data, password, salt, iv, keySize,
                    ITERATION_COUNT_DEFAULT);
        }

        public static byte[] encrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv, int keySize, int iterationCount) {
            return process(data, Cipher.ENCRYPT_MODE, password, salt, iv,
                    keySize, iterationCount);
        }

        public static byte[] decrypt(byte[] data, String password, byte[] salt,
                                     byte[] iv, int keySize, int iterationCount) {
            return process(data, Cipher.DECRYPT_MODE, password, salt, iv,
                    keySize, iterationCount);
        }

        /**
         * AES encrypt function
         *
         * @param original
         * @param key      16, 24, 32 bytes available
         * @param iv       initial vector (16 bytes) - if null: ECB mode, otherwise:
         *                 CBC mode
         * @return
         */
        public static byte[] encrypt(byte[] original, byte[] key, byte[] iv) {
            if (key == null
                    || (key.length != 16 && key.length != 24 && key.length != 32)) {
                return null;
            }
            if (iv != null && iv.length != 16) {
                return null;
            }

            try {
                SecretKeySpec keySpec = null;
                Cipher cipher = null;
                if (iv != null) {
                    keySpec = new SecretKeySpec(key, KEY_AES_SPEC);
                    cipher = Cipher.getInstance(KEY_AES_SPEC);
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec,
                            new IvParameterSpec(iv));
                } else // if(iv == null)
                {
                    keySpec = new SecretKeySpec(key, KEY_AES_SPEC);
                    cipher = Cipher.getInstance(KEY_AES_SPEC);
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                }

                return cipher.doFinal(original);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * AES decrypt function
         *
         * @param encrypted
         * @param key       16, 24, 32 bytes available
         * @param iv        initial vector (16 bytes) - if null: ECB mode, otherwise:
         *                  CBC mode
         * @return
         */
        public static byte[] decrypt(byte[] encrypted, byte[] key, byte[] iv) {
            if (key == null
                    || (key.length != 16 && key.length != 24 && key.length != 32)) {
                return null;
            }
            if (iv != null && iv.length != 16) {
                return null;
            }

            try {
                SecretKeySpec keySpec = null;
                Cipher cipher = null;
                if (iv != null) {
                    keySpec = new SecretKeySpec(key, "AES/CBC/PKCS7Padding");// AES/ECB/PKCS5Padding
                    cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec,
                            new IvParameterSpec(iv));
                } else // if(iv == null)
                {
                    keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
                    cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec);
                }

                return cipher.doFinal(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        static byte[] process(byte[] data, int mode, String password,
                              byte[] salt, byte[] iv, int keySize, int iterationCount) {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    iterationCount, keySize);
            try {
                SecretKeyFactory keyFactory = SecretKeyFactory
                        .getInstance("PBKDF2WithHmacSHA1");
                byte[] keyBytes = keyFactory.generateSecret(keySpec)
                        .getEncoded();
                SecretKey key = new SecretKeySpec(keyBytes, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec ivParams = new IvParameterSpec(iv);
                cipher.init(mode, key, ivParams);
                return cipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        static String getSimplePassword() {
            return "GZ9Gn2U5nhpea8hw";
        }

        static byte[] getSimpleSalt() {
            return "rUiey8D2GNzV69Mp".getBytes();
        }

        static byte[] getSimpleIV() {
            byte[] iv = new byte[AES.IV_SIZE_DEFAULT];
            Arrays.fill(iv, (byte) 5);
            return iv;
        }
    }

    // http://nelenkov.blogspot.jp/2012/04/using-password-based-encryption-on.html
    public static class AESCrypto {
        private static final int ITERATION_COUNT_DEFAULT = 100;
        private static final int ITERATION_COUNT_MIN = 10;
        private static final int ITERATION_COUNT_MAX = 5000;
        private static final int KEY_SIZE_DEFAULT = 256;
        private static final int KEY_SIZE_MIN = 64;
        private static final int KEY_SIZE_MAX = 1024;
        private static final int IV_SIZE = 16;
        private String password;
        private byte[] salt;
        private byte[] iv;
        private int keySize;
        private int iterCount;

        public AESCrypto(String password) {
            initialize(password, AES.getSimpleSalt(), AES.getSimpleIV(),
                    KEY_SIZE_DEFAULT, ITERATION_COUNT_DEFAULT);
        }

        public AESCrypto(String password, byte[] salt) {
            initialize(password, salt, AES.getSimpleIV(), KEY_SIZE_DEFAULT,
                    ITERATION_COUNT_DEFAULT);
        }

        public AESCrypto(String password, int keySize, byte[] salt, byte[] iv) {
            initialize(password, salt, iv, keySize, ITERATION_COUNT_DEFAULT);
        }

        private void initialize(String password, byte[] salt, byte[] iv,
                                int keySize, int iterCount) {
            Preconditions.checkNotEmpty(password, "password must not be null or empty");
            Preconditions.checkNotNull(salt, "salt must bot be null");
            Preconditions.checkNotNull(iv, "iv must not be null");
            Preconditions.checkArgument(keySize >= KEY_SIZE_MIN
                    && keySize <= KEY_SIZE_MAX, "keySize must between "
                    + KEY_SIZE_MIN + " and " + KEY_SIZE_MAX);
            Preconditions.checkArgument(iterCount >= ITERATION_COUNT_MIN
                            && iterCount <= ITERATION_COUNT_MAX,
                    "iterCount must between " + ITERATION_COUNT_MIN + " and "
                            + ITERATION_COUNT_MAX
            );
            this.password = password;
            this.salt = salt;
            this.iv = iv;
            this.keySize = keySize;
            this.iterCount = iterCount;
        }

        public String encrypt(String text) {
            byte[] data = getRawBytes(text);
            byte[] encryptedData = encrypt(data);
            return base64Encode(encryptedData);
        }

        public byte[] encrypt(byte[] data) {
            return process(data, Cipher.ENCRYPT_MODE);
        }

        public String decrypt(String text) {
            byte[] encryptedData = base64Decode(text);
            byte[] data = decrypt(encryptedData);
            return getString(data);
        }

        public byte[] decrypt(byte[] encryptedData) {
            return process(encryptedData, Cipher.DECRYPT_MODE);
        }

        private byte[] process(byte[] data, int mode) {
            return AES.process(data, mode, password, salt, iv, keySize,
                    iterCount);
        }

    }

    public static final class HEX {

        public static String decToHexString(String number, int TotalNum){
            return String.format("%0" + TotalNum + "x",Integer.valueOf(number));
        }

        public static String decToHexString(Integer number, int TotalNum){
            return String.format("%0" + TotalNum + "x", number);
        }

        public static String hexToDecString(String hex){
            return String.valueOf(Integer.valueOf(hex,16));
        }

        public static String hexToDecString(Integer hex){
            return String.valueOf(hex);
        }

        public static String deUnicode(String content){//将16进制数转换为汉字
            String enUnicode=null;
            String deUnicode=null;
            for(int i=0;i<content.length();i++){
                if(enUnicode==null){
                    enUnicode=String.valueOf(content.charAt(i));
                }else{
                    enUnicode=enUnicode+content.charAt(i);
                }
                if(i%4==3){
                    if(enUnicode!=null){
                        if(deUnicode==null){
                            deUnicode=String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
                        }else{
                            deUnicode=deUnicode+String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
                        }
                    }
                    enUnicode=null;
                }

            }
            return deUnicode;
        }

        public static String enUnicode(String content){//将汉字转换为16进制数
            String enUnicode=null;
            for(int i=0;i<content.length();i++){
                if(i==0){
                    enUnicode=getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
                }else{
                    enUnicode=enUnicode+getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
                }
            }
            return enUnicode;
        }

        private static String getHexString(String hexString){
            String hexStr="";
            for(int i=hexString.length();i<4;i++){
                if(i==hexString.length())
                    hexStr="0";
                else
                    hexStr=hexStr+"0";
            }
            return hexStr+hexString;
        }
    }

    public static final class BCD {

        public static byte[] strToBcd(String asc) {
            int len = asc.length();
            int mod = len % 2;
            if (mod != 0) {
                asc = "0" + asc;
                len = asc.length();
            }
            byte abt[];
            if (len >= 2) {
                len = len / 2;
            }
            byte bbt[] = new byte[len];
            abt = asc.getBytes();
            int j, k;
            for (int p = 0; p < asc.length() / 2; p++) {
                if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                    j = abt[2 * p] - '0';
                } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                    j = abt[2 * p] - 'a' + 0x0a;
                } else {
                    j = abt[2 * p] - 'A' + 0x0a;
                }
                if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                    k = abt[2 * p + 1] - '0';
                } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                    k = abt[2 * p + 1] - 'a' + 0x0a;
                } else {
                    k = abt[2 * p + 1] - 'A' + 0x0a;
                }
                int a = (j << 4) + k;
                byte b = (byte) a;
                bbt[p] = b;
            }
            return bbt;
        }

        public static String bcdToStr(byte[] bytes) {
            StringBuffer temp = new StringBuffer(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
                temp.append((byte) (bytes[i] & 0x0f));
            }
            return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                    .toString().substring(1) : temp.toString();
        }
    }

    public static final class HASH {
        private static final String MD5 = "MD5";
        private static final String SHA_1 = "SHA-1";
        private static final String SHA_256 = "SHA-256";
        private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        public static String md5(byte[] data) {
            return new String(encodeHex(md5Bytes(data)));
        }

        public static String md5(String text) {
            return new String(encodeHex(md5Bytes(getRawBytes(text))));
        }

        public static byte[] md5Bytes(byte[] data) {
            return getDigest(MD5).digest(data);
        }

        public static String sha1(byte[] data) {
            return new String(encodeHex(sha1Bytes(data)));
        }

        public static String sha1(String text) {
            return new String(encodeHex(sha1Bytes(getRawBytes(text))));
        }

        public static byte[] sha1Bytes(byte[] data) {
            return getDigest(SHA_1).digest(data);
        }

        public static String sha256(byte[] data) {
            return new String(encodeHex(sha256Bytes(data)));
        }

        public static String sha256(String text) {
            return new String(encodeHex(sha256Bytes(getRawBytes(text))));
        }

        public static byte[] sha256Bytes(byte[] data) {
            return getDigest(SHA_256).digest(data);
        }

        private static MessageDigest getDigest(String algorithm) {
            try {
                return MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static char[] encodeHex(byte[] data) {
            return encodeHex(data, true);
        }

        private static char[] encodeHex(byte[] data, boolean toLowerCase) {
            return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
        }

        private static char[] encodeHex(byte[] data, char[] toDigits) {
            int l = data.length;
            char[] out = new char[l << 1];
            for (int i = 0, j = 0; i < l; i++) {
                out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
                out[j++] = toDigits[0x0F & data[i]];
            }
            return out;
        }

    }

}