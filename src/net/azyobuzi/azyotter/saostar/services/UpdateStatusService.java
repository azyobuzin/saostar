package net.azyobuzi.azyotter.saostar.services;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import net.azyobuzi.azyotter.saostar.NotificationCenter;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateStatusService extends Service {
	public static final String TEXT = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.TEXT";
	public static final String IN_REPLY_TO_STATUS_ID = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.IN_REPLY_TO_STATUS_ID";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		String text = intent.getStringExtra(TEXT);
		Toast.makeText(this, R.string.tweeting, Toast.LENGTH_SHORT).show();

		final StatusUpdate statusUpdate = new StatusUpdate(text);
		statusUpdate.setInReplyToStatusId(intent.getLongExtra(IN_REPLY_TO_STATUS_ID, -1));

		AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
		tw.addListener(new TwitterAdapter() {
			@Override
			public void updatedStatus(Status status) {
				stopSelf();
			}

			@Override
			public void onException(TwitterException twitterexception, TwitterMethod twittermethod) {
				NotificationCenter.notifyFailedTweetComplete(UpdateStatusService.this, twitterexception, statusUpdate);
				stopSelf();
			}
		});

		tw.updateStatus(statusUpdate);

		return START_STICKY;
	}
}
