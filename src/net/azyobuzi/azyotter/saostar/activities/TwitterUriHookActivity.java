package net.azyobuzi.azyotter.saostar.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemId;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TwitterUriHookActivity extends Activity {
	private static final Pattern tweetPermalinkPattern = Pattern.compile("^https?://(?:www\\.|api\\.)?twitter\\.com/(?:#!/)?[a-zA-Z0-9_]+/status(?:es)?/(\\d+)/?(?:\\?.*)?$");
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
        Uri uri = intent.getData();

        Matcher m = tweetPermalinkPattern.matcher(uri.toString());
        if (m.find()) {
        	startActivity(new Intent(this, TweetDetailActivity.class)
        		.putExtra(TweetDetailActivity.ID, new TimelineItemId(TimelineItemId.TYPE_TWEET, Long.valueOf(m.group(1)))));
        	finish();
        	return;
        }

        if (UpdateStatusActivity.isTweetIntentUri(uri)) {
        	startActivity(new Intent(this, UpdateStatusActivity.class)
        		.setData(uri)
        		.putExtras(intent));
        	finish();
        	return;
        }
        
        finish();
	}
}
