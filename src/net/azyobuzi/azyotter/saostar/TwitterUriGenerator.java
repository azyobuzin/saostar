package net.azyobuzi.azyotter.saostar;

import android.net.Uri;

public class TwitterUriGenerator {
	public static Uri tweetWebIntent(String status, long inReplyTo) {
		Uri.Builder builder = new Uri.Builder().scheme("https").authority("twitter.com").path("/intent/tweet");
		
		if (!StringUtil.isNullOrEmpty(status))
			builder.appendQueryParameter("status", status);
		
		if (inReplyTo >= 0)
			builder.appendQueryParameter("in_reply_to", String.valueOf(inReplyTo));
		
		return builder.build();
	}
	
	public static Uri tweetPermalink(String screenName, long id) {
		return Uri.parse("https://twitter.com/" + screenName + "/status/" + id);
	}
	
	public static Uri search(String query) {
		return Uri.parse("https://twitter.com/search/" + Uri.encode(query).replace("#", "%23"));
	}
	
	public static Uri userPermalink(String screenName) {
		return Uri.parse("https://twitter.com/" + screenName);
	}
}
