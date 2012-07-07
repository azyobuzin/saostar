package net.azyobuzi.azyotter.saostar.activities;

import twitter4j.AsyncTwitter;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import net.azyobuzi.azyotter.saostar.ActivityUtil;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemId;
import net.azyobuzi.azyotter.saostar.widget.CustomizedUrlImageView;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

public class TweetDetailActivity extends ListActivity {
	public static final String ID = "net.azyobuzi.azyotter.saostar.activities.TweetDetailActivity.ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(Setting.getTheme());
		setContentView(R.layout.tweet_detail_page);
		
		Intent intent = getIntent();
        boolean fromAzyotter = intent.getBooleanExtra(MainActivity.CALLED_FROM_AZYOTTER, false);
        
        if (fromAzyotter)
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        
        final TimelineItemId id = (TimelineItemId)intent.getSerializableExtra(ID);
        TimelineItem item = TimelineItemCollection.get(id);
        if (item != null) {
        	showInfo(item);
        } else {
        	if (id.type == TimelineItemId.TYPE_USER_STREAM_EVENT) {
        		ActivityUtil.showAlertDialog(this,
        			android.R.drawable.ic_dialog_alert,
        			android.R.string.dialog_alert_title,
        			R.string.the_userstream_event_is_not_found,
        			true);
        	} else {
        		final ProgressDialog dialog = new ProgressDialog(this);
        		dialog.setIndeterminate(true);
        		dialog.setCancelable(false);
        		dialog.setMessage(getText(
        			id.type == TimelineItemId.TYPE_DIRECT_MESSAGE
        			? R.string.getting_direct_message
        			: R.string.getting_tweet
        		));
        		dialog.show();
        		
        		final Handler h = new Handler();
        		//DMを取得するときに選択されてるアカウントに依存する
        		AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
        		tw.addListener(new TwitterAdapter() {
    				@Override
    				public void gotShowStatus(Status status)
    	            {
    					final TimelineItem item = TimelineItemCollection.addOrMerge(status, false);
    					h.post(new Runnable() {
    						@Override
    						public void run() {
    							showInfo(item);
    							dialog.dismiss();
    						}
    					});
    	            }
    				
    				@Override
    				public void gotDirectMessage(DirectMessage message) {
    					final TimelineItem item = TimelineItemCollection.addOrMerge(message);
    					h.post(new Runnable() {
    						@Override
    						public void run() {
    							showInfo(item);
    							dialog.dismiss();
    						}
    					});
    				}
    				
    				@Override
    				public void onException(final TwitterException ex, TwitterMethod method) {
    					ex.printStackTrace();
    					h.post(new Runnable() {
    						@Override
    						public void run() {
		    					dialog.dismiss();
		    					ActivityUtil.showAlertDialog(
		    						TweetDetailActivity.this,
		    						android.R.drawable.ic_dialog_alert,
		    						id.type == TimelineItemId.TYPE_DIRECT_MESSAGE
			    		        		? R.string.couldnt_get_direct_message
			    		                : R.string.couldnt_get_tweet,
		    		                StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage(),
		    		                true
		    		            );
    						}
    					});
    				}
    			});
        		
        		if (id.type == TimelineItemId.TYPE_DIRECT_MESSAGE)
        			tw.showDirectMessage(id.id);
        		else
        			tw.showStatus(id.id);
        	}
        }
	}
	
	private void showInfo(TimelineItem item) {
		((CustomizedUrlImageView)findViewById(R.id.iv_tweet_detail_profile_image)).setImageUrl(item.from.profileImageUrl);
		((TextView)findViewById(R.id.tv_tweet_detail_name)).setText(item.from.screenName + " / " + item.from.name);
		((TextView)findViewById(R.id.tv_tweet_detail_text)).setText(item.displayText);
		((TextView)findViewById(R.id.tv_tweet_detail_date)).setText(item.createdAt.toLocaleString());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
