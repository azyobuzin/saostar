package net.azyobuzi.azyotter.saostar.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

class TwitterUriHookActivity extends Activity {
	override onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState)

		val intent = getIntent()
        val uri = intent.getData()

        if (UpdateStatusActivity.isTweetIntentUri(uri)) {
        	startActivity(new Intent(this, UpdateStatusActivity.class)
        		.setData(uri)
        		.putExtras(intent))
        }

        finish()
	}
}
