package com.android.secureit.util;

public class Constants {
    public static final String ALGORITHM_3DES_KEY = "3E4A2241531449444827384777273233390682150734084D";
    // TODO: this should come from NFC
    public static final String ALGORITHM_SHA1_KEY = "Secure-it!";
    public static final String ALGORITHM_ID = "002";

    public static final String ENCRYPTION_KEY_TYPE = "DESede";
    public static final String ENCRYPTION_ALGORITHM = "DESede/CBC/PKCS5Padding";
    public static final String ENCRYPTION_KEY_ID = "001";

    public static final String FILE_SELECTED = "selected_file";

    public static final int GET_FILE_RETURN_CODE = 0;

    public static final String ACTIVITY_FILE_MODE = "activity_mode";
    public static final int ACTIVITY_MODE_INVALID = -1;
    public static final int FILE_ENCRYPT = 0;
    public static final int FILE_DECRYPT = 1;
}
