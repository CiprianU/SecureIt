package com.android.secureit.util;

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
    final static private char[] hexArray = "0123456789abcdef".toCharArray();

    private static byte[] decodeHex(final char[] data) {
        assert null != data : "Data parameter must not be null";

        int len = data.length;

        if (0 != (len & 0x01)) {
            throw new IllegalArgumentException("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // 2 characters form the hex value.
        for (int idx = 0, jdx = 0, fdx; jdx < len; ++idx) {
            fdx = (Character.digit(data[jdx++], 16) << 4)
                    | (Character.digit(data[jdx++], 16));
            out[idx] = (byte) (fdx & 0xFF);
        }

        return out;
    }

    /**
     * Converts from byte array to hex String
     *
     * @param bytes
     * @return hex String
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * Apply the SHA1 algorithm.
     *
     * @param keyBytes
     * @param text
     * @return the encrypted byte array.
     */
    public static byte[] hmacSha1(byte[] keyBytes, byte[] text) {
        Mac hmacSha1;
        byte[] ret = null;
        try {
            try {
                hmacSha1 = Mac.getInstance("HmacSHA1");
            } catch (final NoSuchAlgorithmException nsae) {
                hmacSha1 = Mac.getInstance("HMAC-SHA-1");
            }

            final SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmacSha1.init(macKey);
            ret = hmacSha1.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Apply the 3DES, encryption type DESede using the default key.
     *
     * @param plainText
     * @return the base 64 encrypted string.
     */
    public static String encrypt3DES(String plainText, String pass) {
        Cipher m_cypher;
        String ret = null;

        byte[] tmpSecret = (pass != null && pass.length() > 0) ? hmacSha1(
                decodeHex(Constants.ALGORITHM_SHA1_KEY.toCharArray()),
                pass.getBytes()) : decodeHex(Constants.ALGORITHM_3DES_KEY
                .toCharArray());

        byte[] secret = new byte[16];
        if (tmpSecret.length > 16) {
            System.arraycopy(tmpSecret, 0, secret, 0, 16);
        } else {
            secret = tmpSecret;
        }

        try {
            m_cypher = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
            // decode hex works ok, tried it on the internet.
            SecretKeySpec key = new SecretKeySpec(secret,
                    Constants.ENCRYPTION_KEY_TYPE);
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);

            m_cypher.init(Cipher.ENCRYPT_MODE, key, iv);

            ret = Base64
                    .encode(m_cypher.doFinal(plainText.getBytes("UTF-8")));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | InvalidKeyException | UnsupportedEncodingException
                | InvalidAlgorithmParameterException | BadPaddingException e1) {
            e1.printStackTrace();
        }

        return ret;
    }

    public static String decrypt3DES(String encryptedText, String pass) {
        String decryptedText = null;
        Cipher m_cypher;

        byte[] tmpSecret = (pass != null && pass.length() > 0) ? hmacSha1(
                decodeHex(Constants.ALGORITHM_SHA1_KEY.toCharArray()),
                pass.getBytes()) : decodeHex(Constants.ALGORITHM_3DES_KEY
                .toCharArray());

        byte[] secret = new byte[16];
        if (tmpSecret.length > 16) {
            System.arraycopy(tmpSecret, 0, secret, 0, 16);
        } else {
            secret = tmpSecret;
        }

        try {
            m_cypher = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
            // decode hex works ok, tried it on the internet.
            SecretKeySpec key = new SecretKeySpec(secret,
                    Constants.ENCRYPTION_KEY_TYPE);
            IvParameterSpec iv = new IvParameterSpec(new byte[8], 0, 8);
            byte[] encodedBytes = android.util.Base64.decode(encryptedText,
                    android.util.Base64.DEFAULT);

            m_cypher.init(Cipher.DECRYPT_MODE, key, iv);

            decryptedText = new String(m_cypher.doFinal(encodedBytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | BadPaddingException | InvalidAlgorithmParameterException
                | IllegalArgumentException | IllegalBlockSizeException e1) {
            e1.printStackTrace();
        }

        return decryptedText;
    }
}
