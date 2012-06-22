package net.azyobuzi.azyotter.saostar

import java.util.Random

import net.azyobuzi.azyotter.saostar.activities.AzyotterActivity
import net.azyobuzi.azyotter.saostar.activities.RetryActivity

import twitter4j.StatusUpdate
import twitter4j.TwitterException
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class NotificationCenter {
	private static val rnd = new Random()
	def private static getRandomId() {
		rnd.nextInt()
	}

	def static notifyStartedTweeting(Context ctx) {
		Toast.makeText(ctx, R.string.tweeting, Toast.LENGTH_SHORT).show()
	}

	def static notifyFailedTweet(Context ctx, TwitterException ex, StatusUpdate status, String mediaUri) {
		val mng = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE)

		val retryIntent = new Intent(Intent.ACTION_VIEW)
			.setData(TwitterWebIntentUriGenerator.tweet(status.getStatus(), status.getInReplyToStatusId()))
		if (!StringUtil.isNullOrEmpty(mediaUri))
			retryIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaUri))
		if (status.getLocation() != null)
			retryIntent.putExtra("latitude", status.getLocation().getLatitude())
				.putExtra("longitude", status.getLocation().getLongitude())
		//CALLED_FROM_AZYOTTERは指定しない

		val notif = new Notification(android.R.drawable.stat_notify_error, ctx.getText(R.string.tweet_failed), System.currentTimeMillis())
		notif.setLatestEventInfo(
			ctx,
			ctx.getText(R.string.tweet_failed),
			StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
			PendingIntent.getActivity(ctx, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		)
		notif.flags |= Notification.FLAG_AUTO_CANCEL
		mng.notify(getRandomId(), notif)
	}

	def static notifyStartedFavoriting(Context ctx) {
		Toast.makeText(ctx, R.string.favoriting, Toast.LENGTH_SHORT).show()
	}

	def static notifyFailedFavorite(Context ctx, TwitterException ex, String statuses) {
		val mng = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE)

		val retryIntent = new Intent(ctx, RetryActivity.class)
			.putExtra(RetryActivity.TYPE, RetryActivity.TYPE_FAVORITE)
			.putExtra(RetryActivity.STATUSES, statuses)
			.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true)

		val notif = new Notification(android.R.drawable.stat_notify_error, ctx.getText(R.string.favorite_failed), System.currentTimeMillis())
		notif.setLatestEventInfo(
			ctx,
			ctx.getText(R.string.favorite_failed),
			StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
			PendingIntent.getActivity(ctx, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		)
		notif.flags |= Notification.FLAG_AUTO_CANCEL
		mng.notify(getRandomId(), notif)
	}

	def static notifyStartedRetweeting(Context ctx) {
		Toast.makeText(ctx, R.string.retweeting, Toast.LENGTH_SHORT).show()
	}

	def static notifyFailedRetweet(Context ctx, TwitterException ex, String statuses) {
		val mng = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE)

		val retryIntent = new Intent(ctx, RetryActivity.class)
			.putExtra(RetryActivity.TYPE, RetryActivity.TYPE_RETWEET)
			.putExtra(RetryActivity.STATUSES, statuses)
			.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true)

		val notif = new Notification(android.R.drawable.stat_notify_error, ctx.getText(R.string.retweet_failed), System.currentTimeMillis())
		notif.setLatestEventInfo(
			ctx,
			ctx.getText(R.string.retweet_failed),
			StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
			PendingIntent.getActivity(ctx, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
		)
		notif.flags |= Notification.FLAG_AUTO_CANCEL
		mng.notify(getRandomId(), notif)
	}
}
