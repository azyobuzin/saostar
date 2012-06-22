package net.azyobuzi.azyotter.saostar

import android.net.Uri

class TwitterWebIntentUriGenerator {
	def static tweet(String status, long inReplyTo) {
		val builder = new Uri.Builder().scheme("https").authority("twitter.com").path("/intent/tweet")

		if (!StringUtil.isNullOrEmpty(status))
			builder.appendQueryParameter("status", status)

		if (inReplyTo >= 0)
			builder.appendQueryParameter("in_reply_to", String.valueOf(inReplyTo))

		builder.build()
	}
}
