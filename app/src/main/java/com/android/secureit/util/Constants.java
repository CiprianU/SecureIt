package com.android.secureit.util;

public class Constants {
    // TODO: this should come from NFC
    public static final String SHA256_KEY = "BL2P#QdB6UZftaXuV4GrT#azKZDI1$pi";

    public static final String ENCRYPTION_KEY_TYPE = "AES";
    public static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static final String FILE_SELECTED = "selected_file";

    public static final int GET_FILE_RETURN_CODE = 0;

    public static final String ACTIVITY_FILE_MODE = "activity_mode";
    public static final int ACTIVITY_MODE_INVALID = -1;
    public static final int FILE_ENCRYPT = 0;
    public static final int FILE_DECRYPT = 1;
}
