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
    private File currentDirectory = new File(Environment
            .getExternalStorageDirectory().getAbsolutePath());

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        int activityMode = getIntent().getIntExtra(
                Constants.ACTIVITY_FILE_MODE, Constants.ACTIVITY_MODE_INVALID);

        if (activityMode == Constants.FILE_DECRYPT) {
            this.setTitle("Choose file to decrypt");
        } else if (activityMode == Constants.FILE_ENCRYPT) {
            this.setTitle("Choose file to encrypt");
        }
        // setContentView() gets called within the next line,
        // so we do not need it here.
        browseToRoot();

//        ListView listView = getListView();
//        listView.setTextFilterEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // if we're not in the sdcard root, back button will go back to current dir's parent.
        if (this.currentDirectory.getName().contentEquals(
                Environment.getExternalStorageDirectory().getName()
        ))
            super.onBackPressed();
        else
            this.browseTo(this.currentDirectory.getParentFile());
    }

    /**
     * This function browses to the root-directory of the file-system.
     */
    private void browseToRoot() {
        browseTo(new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()));
    }

    /**
     * This function browses up one level according to the field:
     * currentDirectory
     */
    private void upOneLevel() {
        if (this.currentDirectory.getParent() != null)
            this.browseTo(this.currentDirectory.getParentFile());
    }

    private void browseTo(final File aDirectory) {
        if (aDirectory.isDirectory()) {
            this.currentDirectory = aDirectory;
            fill(aDirectory.listFiles());
        }
    }

    private void fill(File[] files) {
        this.directoryEntries.clear();

        // Add the ".." == 'Up one level'
        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        if (this.currentDirectory.getParent() != null
                && !this.currentDirectory.getName().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getName())) {
            // don't browse upper than /sdcard
            this.directoryEntries.add("/..");
        }

        // On relative Mode, we have to add the current-path to
        // the beginning
        int currentPathStringLenght = this.currentDirectory.getAbsolutePath()
                .length();
        String[] fileList = new String[files.length];

        for (int i = 0; i < fileList.length; i++) {
            if (files[i].isDirectory())
                fileList[i] = files[i].getAbsolutePath().substring(
                        currentPathStringLenght);
            else
                fileList[i] = files[i].getAbsolutePath().substring(
                        currentPathStringLenght + 1);
        }

        // Arrays.sort(fileList);
        Arrays.sort(fileList, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareToIgnoreCase(rhs);
            }
        });
        this.directoryEntries.addAll(new ArrayList<>(Arrays
                .asList(fileList)));

        ArrayAdapter<String> directoryList = new ArrayAdapter<>(this,
                R.layout.file_row, this.directoryEntries);

        this.setListAdapter(directoryList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selectedFileString = this.directoryEntries.get((int) id);
        if (selectedFileString.equals("/..")) {
            this.upOneLevel();
        } else {
            File clickedFile = new File(
                    this.currentDirectory.getAbsolutePath()
                            + ((!this.directoryEntries.get((int) id)
                            .startsWith("/")) ? "/" : "")
                            + this.directoryEntries.get((int) id)
            );

            if (clickedFile.isDirectory()) {
                this.browseTo(clickedFile);
            } else {
                // a file has been selected
                Intent intent = this.getIntent();
                intent.putExtra(Constants.FILE_SELECTED,
                        clickedFile.getAbsolutePath());

                this.setResult(RESULT_OK, intent);
                exitActivity();
            }
        }
    }

    /**
     * Exits this activity.
     */
    protected void exitActivity() {
        this.finish();
    }
}