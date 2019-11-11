package com.android.secureit;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.secureit.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileBrowserActivity extends ListActivity {
    private List<String> directoryEntries = new ArrayList<>();
    private File currentDirectory = new File(
            Environment.getExternalStorageDirectory().getAbsolutePath()
    );

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        int activityMode = getIntent().getIntExtra(
                Constants.ACTIVITY_FILE_MODE, Constants.ACTIVITY_MODE_INVALID);

        if (activityMode == Constants.FILE_DECRYPT) {
            setTitle("Choose file to decrypt");
        } else if (activityMode == Constants.FILE_ENCRYPT) {
            setTitle("Choose file to encrypt");
        }
        // setContentView() gets called within the next line,
        // so we do not need it here.
        browseToRoot();
    }

    @Override
    public void onBackPressed() {
        // if we're not in the sdcard root, back button will go back to current dir's parent.
        if (!currentDirectory.getName().contentEquals(
                Environment.getExternalStorageDirectory().getName())
                && currentDirectory.getParentFile() != null
        ) {
            browseTo(currentDirectory.getParentFile());
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This function browses to the root-directory of the file-system.
     */
    private void browseToRoot() {
        browseTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
    }

    /**
     * This function browses up one level according to the field:
     * currentDirectory
     */
    private void upOneLevel() {
        if (currentDirectory.getParent() != null && currentDirectory.getParentFile() != null) {
            browseTo(currentDirectory.getParentFile());
        }
    }

    private void browseTo(final File aDirectory) {
        if (aDirectory.isDirectory()) {
            currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
        }
    }

    private void fill(File[] files) {
        directoryEntries.clear();

        if (currentDirectory.getParent() != null
                && !currentDirectory.getName().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getName())) {
            // don't browse upper than /sdcard
            directoryEntries.add("/..");
        }

        // On relative Mode, we have to add the current-path to the beginning
        int currentPathStringLength = currentDirectory.getAbsolutePath().length();
        String[] fileList = new String[files.length];

        for (int i = 0; i < fileList.length; i++) {
            if (files[i].isDirectory())
                fileList[i] = files[i].getAbsolutePath().substring(
                        currentPathStringLength);
            else
                fileList[i] = files[i].getAbsolutePath().substring(
                        currentPathStringLength + 1);
        }

        // Arrays.sort(fileList);
        Arrays.sort(fileList, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });
        directoryEntries.addAll(new ArrayList<>(Arrays
                .asList(fileList)));

        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this,
                R.layout.file_row, directoryEntries);

        setListAdapter(directoryList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selectedFileString = directoryEntries.get((int) id);
        if (selectedFileString.equals("/..")) {
            upOneLevel();
        } else {
            File clickedFile = new File(
                    currentDirectory.getAbsolutePath()
                            + ((!directoryEntries.get((int) id)
                            .startsWith("/")) ? "/" : "")
                            + directoryEntries.get((int) id)
            );

            if (clickedFile.isDirectory()) {
                browseTo(clickedFile);
            } else {
                // a file has been selected
                Intent intent = getIntent();
                intent.putExtra(Constants.FILE_SELECTED,
                        clickedFile.getAbsolutePath());

                setResult(RESULT_OK, intent);
                exitActivity();
            }
        }
    }

    /**
     * Exits this activity.
     */
    protected void exitActivity() {
        finish();
    }
}
