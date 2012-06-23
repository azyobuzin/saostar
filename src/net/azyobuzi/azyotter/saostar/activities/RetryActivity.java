package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.services.FavoriteService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RetryActivity extends Activity {
	public static final String TYPE = "net.azyobuzi.azyotter.saostar.activities.RetryActivity.TYPE";
	public static final String STATUSES = "net.azyobuzi.azyotter.saostar.activities.RetryActivity.STATUSES";
	public static final int TYPE_FAVORITE = 0;
	public static final int TYPE_RETWEET = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent.getBooleanExtra(MainActivity.CALLED_FROM_AZYOTTER, false)) {
			int type = intent.getIntExtra(TYPE, -1);
			String statuses = intent.getStringExtra(STATUSES);

			switch (type) {
				case TYPE_FAVORITE:
					startService(new Intent(this, FavoriteService.class)
						.putExtra(FavoriteService.STATUSES, statuses));
					break;
			}
		}

		finish();
	}
}
