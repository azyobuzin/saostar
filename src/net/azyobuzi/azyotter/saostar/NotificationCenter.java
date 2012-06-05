package net.azyobuzi.azyotter.saostar;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationCenter {
	private static final int FAILED_UPDATE_STATUS = 0;

	public static void notifyFailedTweet(Context ctx, TwitterException ex, StatusUpdate status, String mediaUri) {
		NotificationManager mng = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent retryIntent = new Intent(Intent.ACTION_VIEW)
			.setData(TwitterWebIntentUriGenerator.tweet(status.getStatus(), status.getInReplyToStatusId()));
		if (!StringUtil.isNullOrEmpty(mediaUri))
			retryIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaUri));
		//CALLED_FROM_AZYOTTERは指定しない

		Notification notif = new Notification(R.drawable.ic_stat_tweet, ctx.getText(R.string.tweet_failed), System.currentTimeMillis());
		notif.setLatestEventInfo(
			ctx,
			ctx.getText(R.string.tweet_failed),
			StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
			PendingIntent.getActivity(ctx, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		mng.notify(FAILED_UPDATE_STATUS, notif);
	}
}
