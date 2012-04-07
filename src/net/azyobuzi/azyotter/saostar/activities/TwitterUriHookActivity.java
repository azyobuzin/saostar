package net.azyobuzi.azyotter.saostar.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TwitterUriHookActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
        Uri uri = intent.getData();

        if (UpdateStatusActivity.isTweetIntentUri(uri)) {
        	startActivity(new Intent(this, UpdateStatusActivity.class)
        		.setData(uri)
        		.putExtras(intent));
        }

        finish();
	}
}
