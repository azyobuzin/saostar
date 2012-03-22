package net.azyobuzi.azyotter.saostar.services;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import net.azyobuzi.azyotter.saostar.NotificationCenter;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateStatusService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		String text = intent.getStringExtra("text");
		NotificationCenter.notifyTweeting(this, text);

		final StatusUpdate statusUpdate = new StatusUpdate(text);
		statusUpdate.setInReplyToStatusId(intent.getLongExtra("inReplyToStatusId", -1));

		AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
		tw.addListener(new TwitterAdapter() {
			@Override
			public void updatedStatus(Status status) {
				NotificationCenter.notifyTweetComplete(UpdateStatusService.this, null, statusUpdate);
				stopSelf();
			}

			@Override
			public void onException(TwitterException twitterexception, TwitterMethod twittermethod) {
				NotificationCenter.notifyTweetComplete(UpdateStatusService.this, twitterexception, statusUpdate);
				stopSelf();
			}
		});

		tw.updateStatus(statusUpdate);

		return START_STICKY;
	}
}
