/**
 *
 */
package com.android.secureit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * @author ciprian.ursu
 */
public class FileEncryptDecryptActivity extends Activity implements
        OnClickListener {
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

        titleTextView = (TextView) findViewById(R.id.fileDecryptTitleTextView);
        passwordEditText = (EditText) findViewById(R.id.fileDecryptPasswordEditText);
        contentTextView = (TextView) findViewById(R.id.fileDecryptContentTextView);
        encryptDecryptButton = (Button) findViewById(R.id.fileDecryptButton);

        if (activityMode == Constants.FILE_DECRYPT) {
            encryptDecryptButton.setText("Decrypt");
        } else {
            encryptDecryptButton.setText("Encrypt");
            storeContentButton = (Button) findViewById(R.id.storeToFileButton);
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
        switch (requestCode) {
            case Constants.GET_FILE_RETURN_CODE:
                if (resultCode == RESULT_OK) {
                    selectedFilePath = data.getStringExtra(Constants.FILE_SELECTED);

                    if (selectedFilePath == null || selectedFilePath.length() == 0)
                        this.finish();

                    titleTextView.setText(selectedFilePath);

                    fileContent = FileUtilities.readFile(selectedFilePath);
                    contentTextView.setText(fileContent);
                } else {
                    this.finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fileDecryptButton:
                String password = passwordEditText.getText().toString();
                if (password.length() == 0) {
                    infoToast.setText("Empty password!");
                    infoToast.show();
                    break;
                }
                if (activityMode == Constants.FILE_DECRYPT) {
                    String decryptedText = Encryption.decrypt3DES(fileContent,
                            password);
                    contentTextView.setText(decryptedText);
                    if (decryptedText == null || decryptedText.length() == 0) {
                        infoToast
                                .setText("Oups! There was a problem while trying to decrypt the file");
                        infoToast.show();
                    }
                } else {
                    String encryptedText = Encryption.encrypt3DES(fileContent,
                            password);
                    contentTextView.setText(encryptedText);
                    if (encryptedText == null || encryptedText.length() == 0) {
                        infoToast
                                .setText("Oups! There was a problem while trying to encrypt the file");
                        infoToast.show();
                    } else {
                        storeContentButton.setVisibility(View.VISIBLE);
                    }
                }
                encryptDecryptButton.setVisibility(View.GONE);
                break;

            case R.id.storeToFileButton:
                // store the encrypted file on sd card
                int separatorPos = selectedFilePath.lastIndexOf(File.separatorChar);
                if (separatorPos > 0) {
                    String fileName = selectedFilePath.substring(separatorPos + 1);
                    String filePath = FileUtilities.defaultFilePath;
                    int pos = fileName.lastIndexOf('.');
                    if (pos > 0) {
                        filePath += fileName.substring(0, pos) + ".sit";
                    } else {
                        filePath += fileName + ".sit";
                    }
                    FileUtilities.writeToFile(contentTextView.getText().toString(),
                            filePath);
                    infoToast.setText("Successfully stored at " + filePath);
                    infoToast.show();
                } else {
                    infoToast.setText("Oups! file NOT stored");
                    infoToast.show();
                }
                break;
        }
    }
}
