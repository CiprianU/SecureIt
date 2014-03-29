package com.android.secureit.util;

/**
 * Utilities for encoding and decoding the Base64 representation of binary data.
 */
public final class Base64
{
    private static byte[] Base64Modem = null;

    private static final void initBase64()
    {
        if (null == Base64.Base64Modem)
        {
            Base64.Base64Modem = new byte[128 + 64];
        }

        for (byte idx = 26; --idx >= 0;)
        {
            Base64.Base64Modem[Base64.Base64Modem[128 + idx] = (byte) (idx + 'A')] = idx;
            Base64.Base64Modem[Base64.Base64Modem[128 + 26 + idx] = (byte) (idx + 'a')] = (byte) (idx + 26);

            if (idx < 10)
            {
                Base64.Base64Modem[Base64.Base64Modem[128 + 52 + idx] = (byte) (idx + '0')] = (byte) (idx + 52);
            }
        }

        Base64.Base64Modem[Base64.Base64Modem[128 + 62] = '+'] = 62;
        Base64.Base64Modem[Base64.Base64Modem[128 + 63] = '/'] = 63;
    }

    /**
     * Encodes all of the provided data.
     * 
     * @param in array of binary data to base64 encode.
     * @return a string containing Base64 characters.
     */
    public static final String encode(final byte[] in)
    {
        return Base64.encode(in, 0, in.length);
    }

    /**
     * Encodes all of the provided data.
     * 
     * @param in array of binary data to base64 encode.
     * @param inPos position to start reading data from.
     * @param inAvail amount of bytes available from input for encoding.
     * @return a string containing Base64 characters.
     */
    public static final String encode(final byte[] in, final int inPos, int inAvail)
    {
        if (null == Base64.Base64Modem)
        {
            Base64.initBase64();
        }

        int L = inAvail / 3, V = 0, src = (inAvail * 4) / 3, dst = 0;

        final byte[] Latch = new byte[(src + 3) & -4];

        for (src = inPos; --L >= 0; src += 3, dst += 4)
        {
            V = (((in[src]) & 0xFF) << 16) + (((in[src + 1]) & 0xFF) << 8) + ((in[src + 2]) & 0xFF);

            Latch[dst] = Base64.Base64Modem[128 + (V >>> 18)];
            Latch[dst + 1] = Base64.Base64Modem[128 + ((V >>> 12) & 0x3F)];
            Latch[dst + 2] = Base64.Base64Modem[128 + ((V >>> 6) & 0x3F)];
            Latch[dst + 3] = Base64.Base64Modem[128 + (V & 0x3F)];
        }

        if (0 != (inAvail %= 3))
        {
            V = (((in[src]) & 0xFF) << 8);

            if (inAvail > 1)
            {
                V += (in[src + 1] & 0xFF);
            }

            Latch[dst] = Base64.Base64Modem[128 + (V >> 10)];
            Latch[dst + 1] = Base64.Base64Modem[128 + ((V >> 4) & 0x3F)];
            Latch[dst + 2] = inAvail > 1 ? (byte) Base64.Base64Modem[128 + ((V & 0xF) << 2)] : (byte) '=';
            Latch[dst + 3] = '=';
        }

        return new String(Latch);
    }

    /**
     * Decodes a byte array from Base64 format.
     * 
     * @param S string containing Base64 characters.
     * @return an array of binary data to base64 decoded.
     */
    public static final byte[] decode(final String S)
    {
        final int len = S.length();

        if (0 != (len & 3))
        {
            return null;
        }

        if (null == Base64.Base64Modem)
        {
            Base64.initBase64();
        }

        int L = len >> 2, src = 0, dst = 0, V = 0;
        int Padded = L * 3;

        if ('=' == S.charAt(len - 1))
        {
            --L;
            --Padded;

            if ('=' == S.charAt(len - 2))
            {
                --Padded;
            }
        }

        final byte[] Result = new byte[Padded];

        while (--L >= 0)
        {
            V = (Base64.Base64Modem[S.charAt(src++)] << 18) + (Base64.Base64Modem[S.charAt(src++)] << 12)
                    + (Base64.Base64Modem[S.charAt(src++)] << 6) + (Base64.Base64Modem[S.charAt(src++)]);

            Result[dst++] = (byte) (V >> 16);
            Result[dst++] = (byte) (V >> 8);
            Result[dst++] = (byte) V;
        }

        if (0 != (Padded %= 3))
        {
            V = (Base64.Base64Modem[S.charAt(src)] << 10) + (Base64.Base64Modem[S.charAt(src + 1)] << 4)
                    + (Base64.Base64Modem[S.charAt(src + 2)] >> 2);

            Result[dst] = (byte) (V >> 8);

            if (Padded > 1)
            {
                Result[dst + 1] = (byte) V;
            }
        }

        return Result;
    }
}
