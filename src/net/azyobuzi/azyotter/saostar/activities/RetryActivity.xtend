package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.services.FavoriteService
import android.app.Activity
import android.content.Intent
import android.os.Bundle

class RetryActivity extends Activity {
	static val TYPE = "net.azyobuzi.azyotter.saostar.activities.RetryActivity.TYPE"
	static val STATUSES = "net.azyobuzi.azyotter.saostar.activities.RetryActivity.STATUSES"
	static val TYPE_FAVORITE = 0
	static val TYPE_RETWEET = 1

	override onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState)

		val intent = getIntent()
		if (intent.getBooleanExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, false)) {
			val type = intent.getIntExtra(TYPE, -1)
			val statuses = intent.getStringExtra(STATUSES)

			switch (type) {
				case TYPE_FAVORITE:
					startService(new Intent(this, FavoriteService.class)
						.putExtra(FavoriteService.STATUSES, statuses))
			}
		}

		finish()
	}
}
