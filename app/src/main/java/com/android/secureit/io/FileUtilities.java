package com.android.secureit.io;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtilities {
    public static final String defaultFilePath = Environment
            .getExternalStorageDirectory() + "/secureit/";

    public static boolean isSDCardPresent() {
        // returns true if the sdcard is present, false if not.
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String readFile(String absPath) {
        try {
            String str;
            StringBuilder buf = new StringBuilder();
            File file = new File(absPath);
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((str = reader.readLine()) != null) {
                buf.append(str).append("\n");
            }
            is.close();

            return buf.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeToFile(String information, String filePath) {
        File file = new File(filePath);

        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        FileOutputStream writeStream = null;

        try {
            writeStream = new FileOutputStream(file, true);
            writeStream.write(information.getBytes());
            writeStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (writeStream != null) {
                writeStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
