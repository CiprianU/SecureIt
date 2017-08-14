package com.android.secureit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.secureit.io.FileUtilities;
import com.android.secureit.util.Encryption;

public class PlainTextActivity extends Activity implements OnClickListener {
    private EditText passwordEditText;
    private EditText toEncryptEditText;
    private TextView enDeTextView;
    private TextView enDeTitleTextView;
    private EditText fileNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plain_text_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        passwordEditText = findViewById(R.id.passwordEditText);
        toEncryptEditText = findViewById(R.id.plainTextEditText);
        fileNameEditText = findViewById(R.id.fileNameEditText);
        enDeTextView = findViewById(R.id.encryptedDecryptedTextView);
        enDeTitleTextView = findViewById(R.id.edTitleTextView);

        findViewById(R.id.encryptButton).setOnClickListener(this);
        findViewById(R.id.decryptButton).setOnClickListener(this);
        findViewById(R.id.storeToFileButton).setOnClickListener(this);
        findViewById(R.id.readFileButton).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.encryptButton:
                encryptPlainText();
                break;

            case R.id.decryptButton:
                decrypt();
                break;

            case R.id.storeToFileButton:
                storeToFile();
                break;

            case R.id.readFileButton:
                readFile();
            default:
                break;
        }
    }

    private void readFile() {
        final Intent intent = new Intent(this, FileBrowserActivity.class);
        startActivity(intent);
    }

    private void storeToFile() {
        final String fileName = fileNameEditText.getText().toString();
        FileUtilities.writeToFile(enDeTextView.getText().toString(),
                FileUtilities.defaultFilePath + fileName + ".sit");

        Toast.makeText(this,
                "Done! You'll find it here: " + FileUtilities.defaultFilePath + fileName + ".sit",
                Toast.LENGTH_LONG).show();
    }

    private void decrypt() {
        final String encryptedText = enDeTextView.getText().toString();
        final String key = passwordEditText.getText().toString();
        final String decryptedText = Encryption.decryptAES(encryptedText, key);

        enDeTextView.setText(decryptedText);
        enDeTitleTextView.setText("Decrypted:");
    }

    private void encryptPlainText() {
        final String textToEncrypt = toEncryptEditText.getText().toString();
        final String key = passwordEditText.getText().toString();

        final String encryptedText = Encryption.encryptAES(textToEncrypt, key);
        enDeTextView.setText(encryptedText);
        enDeTitleTextView.setText("Encrypted:");
    }
}
