package com.android.secureit.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    final static private int IV_LEN = 16;

    private static byte[] decodeHex(final char[] data) {
        assert null != data : "Data parameter must not be null";

        int len = data.length;

        if (0 != (len & 0x01)) {
            throw new IllegalArgumentException("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // 2 characters form the hex value.
        for (int idx = 0, jdx = 0, fdx; jdx < len; ++idx) {
            fdx = (Character.digit(data[jdx++], 16) << 4) | (Character.digit(data[jdx++], 16));
            out[idx] = (byte) (fdx & 0xFF);
        }

        return out;
    }

    /**
     * Compute SHA-256 hash
     *
     * @param keyBytes
     * @param text
     * @return the encrypted byte array.
     */
    public static byte[] hmacSha256(byte[] keyBytes, byte[] text) {
        try {
            final Mac hmacSha1 = Mac.getInstance("HmacSHA256");
            final SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");

            hmacSha1.init(macKey);
            return hmacSha1.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Encrypt the plainText using AES/CBC algorithm
     *
     * @param plainText
     * @param key
     * @return the base 64 encrypted string.
     */
    public static String encryptAES(String plainText, String key) {
        final byte[] secret = hmacSha256(decodeHex(Constants.SHA256_KEY.toCharArray()), key.getBytes());

        try {
            final Cipher cipher = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
            final SecretKeySpec keySpec = new SecretKeySpec(secret, Constants.ENCRYPTION_KEY_TYPE);
            final IvParameterSpec iv = new IvParameterSpec(new byte[IV_LEN]);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            final byte[] encryptedBase64Bytes = Base64
                    .encode(cipher.doFinal(plainText.getBytes("UTF-8")), Base64.NO_PADDING);
            return new String(encryptedBase64Bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | InvalidKeyException | UnsupportedEncodingException
                | InvalidAlgorithmParameterException | BadPaddingException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    /**
     * Decrypts the encrypted text using the provided key (AES/CBC).
     *
     * @param encryptedText
     * @param key
     * @return
     */
    public static String decryptAES(String encryptedText, String key) {
        final byte[] secret = hmacSha256(decodeHex(Constants.SHA256_KEY.toCharArray()), key.getBytes());

        try {
            final Cipher cipher = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
            final SecretKeySpec keySpec = new SecretKeySpec(secret, Constants.ENCRYPTION_KEY_TYPE);
            final IvParameterSpec iv = new IvParameterSpec(new byte[IV_LEN], 0, IV_LEN);
            final byte[] encodedBytes = Base64.decode(encryptedText, Base64.DEFAULT);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            return new String(cipher.doFinal(encodedBytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | BadPaddingException | InvalidAlgorithmParameterException
                | IllegalArgumentException | IllegalBlockSizeException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}
