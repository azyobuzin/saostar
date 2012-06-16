package net.azyobuzi.azyotter.saostar.services;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import net.azyobuzi.azyotter.saostar.NotificationCenter;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FavoriteService extends Service {
	public static final String STATUSES = "net.azyobuzi.azyotter.saostar.services.FavoriteService.STATUSES";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		NotificationCenter.notifyStartedFavoriting(this);

		String statuses = intent.getStringExtra(STATUSES);

		for (final String status : statuses.split(",")) {
			if (!StringUtil.isNullOrEmpty(status)) {
				AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
				tw.addListener(new TwitterAdapter() {
					@Override
					public void createdFavorite(Status status) {
						stopSelf();
					}

					@Override
					public void onException(TwitterException ex, TwitterMethod method) {
						ex.printStackTrace();
						NotificationCenter.notifyFailedFavorite(FavoriteService.this, ex, status);
						stopSelf();
					}
				});

				tw.createFavorite(Long.valueOf(status));
			}
		}

		return START_STICKY;
	}
}
