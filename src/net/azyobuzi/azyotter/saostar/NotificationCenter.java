package net.azyobuzi.azyotter.saostar;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import net.azyobuzi.azyotter.saostar.activities.AzyotterActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationCenter {
	private static final int UPDATE_STATUS = 0;

	public static void notifyTweeting(Context ctx, String text) {
		Notification notif = new Notification(R.drawable.ic_stat_tweet, ctx.getText(R.string.tweeting), System.currentTimeMillis());
		notif.setLatestEventInfo(
			ctx,
			ctx.getText(R.string.tweeting),
			text,
			PendingIntent.getActivity(ctx, 0, new Intent(ctx, AzyotterActivity.class), 0)
		);
		notif.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

		((NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE)).notify(UPDATE_STATUS, notif);
	}

	public static void notifyTweetComplete(Context ctx, TwitterException ex, StatusUpdate status) {
		NotificationManager mng = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		if (ex == null) {
			mng.cancel(UPDATE_STATUS);
		} else {
			Intent retryIntent = new Intent("TODO") //TODO
				.putExtra("text", status.getStatus())
				.putExtra("inReplyToStatusId", status.getInReplyToStatusId());

			Notification notif = new Notification(R.drawable.ic_stat_tweet, ctx.getText(R.string.tweet_failed), System.currentTimeMillis());
			notif.setLatestEventInfo(
				ctx,
				ctx.getText(R.string.tweet_failed),
				StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
				PendingIntent.getActivity(ctx, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
			);
			notif.flags |= Notification.FLAG_AUTO_CANCEL;
			mng.notify(UPDATE_STATUS, notif);
		}
	}
}
