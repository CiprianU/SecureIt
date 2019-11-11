package com.android.secureit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.secureit.io.FileUtilities;
import com.android.secureit.util.Constants;
import com.android.secureit.util.Encryption;

import java.io.File;

public class FileEncryptDecryptActivity extends Activity implements OnClickListener {

    private TextView titleTextView;
    private EditText passwordEditText;
    private TextView contentTextView;
    private Button encryptDecryptButton;
    private Button storeContentButton;

    private Toast infoToast;

    private String selectedFilePath;
    private String fileContent;

    private int activityMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_decrypt_activity);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Init the toast.
        infoToast = Toast.makeText(getApplicationContext(), "",
                Toast.LENGTH_LONG);

        activityMode = getIntent().getIntExtra(Constants.ACTIVITY_FILE_MODE,
                Constants.ACTIVITY_MODE_INVALID);

        if (activityMode == Constants.ACTIVITY_MODE_INVALID) {
            infoToast.setText("Something went wrong!!!");
            infoToast.show();

            this.finish();
        }

        titleTextView = findViewById(R.id.fileDecryptTitleTextView);
        passwordEditText = findViewById(R.id.fileDecryptPasswordEditText);
        contentTextView = findViewById(R.id.fileDecryptContentTextView);
        encryptDecryptButton = findViewById(R.id.fileEncryptDecryptButton);

        if (activityMode == Constants.FILE_DECRYPT) {
            encryptDecryptButton.setText("Decrypt");
        } else {
            encryptDecryptButton.setText("Encrypt");
            storeContentButton = findViewById(R.id.storeToFileButton);
            storeContentButton.setText("Store");
            storeContentButton.setOnClickListener(this);
        }

        encryptDecryptButton.setOnClickListener(this);

        Intent intent = new Intent(this, FileBrowserActivity.class);
        intent.putExtra(Constants.ACTIVITY_FILE_MODE, activityMode);
        this.startActivityForResult(intent, Constants.GET_FILE_RETURN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.GET_FILE_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                selectedFilePath = data.getStringExtra(Constants.FILE_SELECTED);

                if (selectedFilePath == null || selectedFilePath.length() == 0) {
                    finish();
                }

                titleTextView.setText(selectedFilePath);

                fileContent = FileUtilities.readFile(selectedFilePath);
                contentTextView.setText(fileContent);
            } else {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fileEncryptDecryptButton:
                onFileEncryptDecryptClicked();
                break;

            case R.id.storeToFileButton:
                // store the encrypted file on sd card
                onStoreFileClicked();
                break;
        }
    }

    private void onStoreFileClicked() {
        int separatorPos = selectedFilePath.lastIndexOf(File.separatorChar);
        if (separatorPos > 0) {
            final String fileName = selectedFilePath.substring(separatorPos + 1);
            final int pos = fileName.lastIndexOf('.');
            String filePath = FileUtilities.defaultFilePath;
            if (pos > 0) {
                filePath += fileName.substring(0, pos) + ".sit";
            } else {
                filePath += fileName + ".sit";
            }
            FileUtilities.writeToFile(contentTextView.getText().toString(), filePath);
            infoToast.setText("Successfully stored at " + filePath);
            infoToast.show();
        } else {
            infoToast.setText("Oups! file NOT stored");
            infoToast.show();
        }
    }

    private void onFileEncryptDecryptClicked() {
        final String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            infoToast.setText("Empty password!");
            infoToast.show();
            return;
        }

        if (activityMode == Constants.FILE_DECRYPT) {
            decryptFileContent(password);
        } else {
            encryptFileContent(password);
        }

        encryptDecryptButton.setVisibility(View.GONE);
    }

    private void encryptFileContent(String password) {
        final String encryptedText = Encryption.encryptAES(fileContent, password);
        contentTextView.setText(encryptedText);
        if (TextUtils.isEmpty(encryptedText)) {
            infoToast.setText("Oups! There was a problem while trying to encrypt the file");
            infoToast.show();
        } else {
            storeContentButton.setVisibility(View.VISIBLE);
        }
    }

    private void decryptFileContent(String password) {
        final String decryptedText = Encryption.decryptAES(fileContent, password);
        contentTextView.setText(decryptedText);
        if (TextUtils.isEmpty(decryptedText)) {
            infoToast.setText("Oups! There was a problem while trying to decrypt the file");
            infoToast.show();
        }
    }
}
