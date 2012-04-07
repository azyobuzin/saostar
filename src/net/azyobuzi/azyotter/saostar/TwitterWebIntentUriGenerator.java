package net.azyobuzi.azyotter.saostar;

import android.net.Uri;

public class TwitterWebIntentUriGenerator {
	public static Uri tweet(String status, long inReplyTo) {
		Uri.Builder builder = new Uri.Builder().scheme("https").authority("twitter.com").path("/intent/tweet");
		
		if (!StringUtil.isNullOrEmpty(status))
			builder.appendQueryParameter("status", status);
		
		if (inReplyTo >= 0)
			builder.appendQueryParameter("in_reply_to", String.valueOf(inReplyTo));
		
		return builder.build();
	}
}
