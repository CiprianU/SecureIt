package com.android.secureit.tests;

import android.test.InstrumentationTestCase;

import com.android.secureit.util.Encryption;

/**
 * Created by ciprian.ursu on 3/29/14.
 */
public class EncryptionTest extends InstrumentationTestCase {
    public void testHmacSha1_1() throws Exception {
        byte[] hash = Encryption.hmacSha1(null, null);

        assertNull(hash);
    }

    public void testHmacSha1_2() throws Exception {
        byte[] hash = Encryption.hmacSha1(null, new byte[]{'a', 'b'});

        assertNull(hash);
    }

    public void testHmacSha1_3() throws Exception {

        byte[] hash = Encryption.hmacSha1(new byte[]{'a', 'b'}, null);

        assertNotNull(hash);
    }

    public void testHmacSha1_4() throws Exception {
        final String message = "test secureit";
        final String secretKey = "secureit";
        final String goodEncryption = "5f6c9934ed0f1b034cd2479a2a6e7b0cd56e196f";

        byte[] hash = Encryption.hmacSha1(secretKey.getBytes(), message.getBytes());

        final String hashStr = Encryption.bytesToHex(hash);
        assertEquals(hashStr.equals(goodEncryption), true);
    }

    public void testEncrypt3DES() throws Exception {

    }

    public void testDecrypt3DES() throws Exception {

    }
}
