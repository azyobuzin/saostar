package net.azyobuzi.azyotter.saostar.services;

import twitter4j.AsyncTwitter;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import net.azyobuzi.azyotter.saostar.NotificationCenter;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

public class UpdateStatusService extends Service {
	public static final String TEXT = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.TEXT";
	public static final String IN_REPLY_TO_STATUS_ID = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.IN_REPLY_TO_STATUS_ID";
	public static final String MEDIA = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.MEDIA";
	public static final String LOCATION = "net.azyobuzi.azyotter.saostar.services.UpdateStatusService.LOCATION";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		String text = intent.getStringExtra(TEXT);
		Toast.makeText(this, R.string.tweeting, Toast.LENGTH_SHORT).show();

		final StatusUpdate statusUpdate = new StatusUpdate(text)
			.inReplyToStatusId(intent.getLongExtra(IN_REPLY_TO_STATUS_ID, -1));
		final String mediaUri = intent.getStringExtra(MEDIA);
		if (!StringUtil.isNullOrEmpty(mediaUri)) {
			try {
				statusUpdate.setMedia("media", getContentResolver().openInputStream(Uri.parse(mediaUri)));
			} catch (Exception e) {
				e.printStackTrace();
				//画像投稿なんてなかったことにする
			}
		}
		Location location = intent.getParcelableExtra(LOCATION);
		if (location != null)
			statusUpdate.setLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));

		AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
		tw.addListener(new TwitterAdapter() {
			@Override
			public void updatedStatus(Status status) {
				stopSelf();
			}

			@Override
			public void onException(TwitterException twitterexception, TwitterMethod twittermethod) {
				twitterexception.printStackTrace();
				NotificationCenter.notifyFailedTweet(UpdateStatusService.this, twitterexception, statusUpdate, mediaUri);
				stopSelf();
			}
		});

		tw.updateStatus(statusUpdate);

		return START_STICKY;
	}
}
