package com.android.secureit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.secureit.util.Constants;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // TODO: use navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(false);

        findViewById(R.id.plainTextButton).setOnClickListener(this);
        findViewById(R.id.fileEncryptButton).setOnClickListener(this);
        findViewById(R.id.fileDecryptButton).setOnClickListener(this);
        findViewById(R.id.nfcWriteButton).setOnClickListener(this);
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
        Intent intent = null;
        switch (v.getId()) {
            case R.id.plainTextButton:
                intent = new Intent(this, PlainTextActivity.class);
                break;

            case R.id.fileDecryptButton:
                intent = new Intent(this, FileEncryptDecryptActivity.class);
                intent.putExtra(Constants.ACTIVITY_FILE_MODE,
                        Constants.FILE_DECRYPT);
                break;

            case R.id.fileEncryptButton:
                intent = new Intent(this, FileEncryptDecryptActivity.class);
                intent.putExtra(Constants.ACTIVITY_FILE_MODE,
                        Constants.FILE_ENCRYPT);
                break;

            case R.id.nfcWriteButton:
                intent = new Intent(this, WriteToTagActivity.class);
                break;
        }

        if (intent != null)
            this.startActivity(intent);
    }
}
