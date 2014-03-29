package com.android.secureit;

import com.android.secureit.io.FileUtilities;
import com.android.secureit.util.Encryption;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PlainTextActivity extends Activity implements OnClickListener {
    private EditText pass;
    private EditText toEncryptEditText;
    private Button encryptButton;
    private Button decryptButton;
    private Button saveButton;
    private Button reloadButton;
    private TextView enDeTextView;
    private TextView enDeTitleTextView;
    private EditText fileNameEditText;

    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plain_text_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        pass = (EditText) findViewById(R.id.passwordEditText);
        toEncryptEditText = (EditText) findViewById(R.id.plainTextEditText);
        fileNameEditText = (EditText) findViewById(R.id.fileNameEditText);
        encryptButton = (Button) findViewById(R.id.encryptButton);
        decryptButton = (Button) findViewById(R.id.decryptButton);
        saveButton = (Button) findViewById(R.id.storeToFileButton);
        reloadButton = (Button) findViewById(R.id.readFileButton);
        enDeTextView = (TextView) findViewById(R.id.encryptedDecryptedTextView);
        enDeTitleTextView = (TextView) findViewById(R.id.edTitleTextView);

        encryptButton.setOnClickListener(this);
        decryptButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        reloadButton.setOnClickListener(this);
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
        String encryptedText;
        switch (v.getId()) {
        case R.id.encryptButton:
            password = pass.getText().toString();
            encryptedText = Encryption.encrypt3DES(
                    toEncryptEditText.getText().toString(), password);
            enDeTextView.setText(encryptedText);
            enDeTitleTextView.setText("Encrypted:");
            break;

        case R.id.decryptButton:
            password = pass.getText().toString();
            encryptedText = enDeTextView.getText().toString();
            String decryptedText = Encryption.decrypt3DES(encryptedText, password);
            enDeTextView.setText(decryptedText);
            enDeTitleTextView.setText("Decrypted:");
            break;

        case R.id.storeToFileButton:
            String fileName = fileNameEditText.getText().toString();
            FileUtilities.writeToFile(enDeTextView.getText().toString(),
                    FileUtilities.defaultFilePath + fileName + ".sit");
            break;

        case R.id.readFileButton:
            /*fileName = fileNameEditText.getText().toString();
            String fileContent = FileUtilities.readFile(FileUtilities.defaultFilePath
                    + fileName + ".sit");
            enDeTextView.setText(fileContent);
            enDeTitleTextView.setText(fileName + ".sit:");*/
        	Intent intent = new Intent(this, FileBrowserActivity.class);
        	this.startActivity(intent);
        default:
            break;
        }
    }
}
