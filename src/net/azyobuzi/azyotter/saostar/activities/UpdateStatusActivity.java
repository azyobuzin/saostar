package net.azyobuzi.azyotter.saostar.activities;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.widget.AccountSelector;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateStatusActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_status_page);

        Intent intent = getIntent();
        fromAzyotter = intent.getBooleanExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, false);

        setTitle(R.string.update_status);
        if (fromAzyotter)
        	getActionBar().setDisplayHomeAsUpEnabled(true);

        txtStatus = (EditText)findViewById(R.id.txt_update_status_status);

        Uri uri = intent.getData();

        if (isTweetIntentUri(uri)) {
        	String status = uri.getQueryParameter("status");
        	if (StringUtil.isNullOrEmpty(status))
        		status = uri.getQueryParameter("text");
        	txtStatus.setText(status != null ? status : "");

        	String inReplyToStr = uri.getQueryParameter("in_reply_to");
        	if (StringUtil.isNullOrEmpty(inReplyToStr))
        		inReplyToStr = uri.getQueryParameter("in_reply_to_status_id");

        	if (!StringUtil.isNullOrEmpty(inReplyToStr) && inReplyToStr.matches("^\\d+$")) {
        		inReplyToStatusId = Long.valueOf(inReplyToStr);
        		inReplyToStatus = TimelineItemCollection.getTweet(inReplyToStatusId);

        		if (inReplyToStatus == null) {
        			final Handler h = new Handler();
        			AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
        			tw.addListener(new TwitterAdapter() {
        				@Override
        				public void gotShowStatus(Status status)
        	            {
        					inReplyToStatus = TimelineItemCollection.addOrMerge(status, false);
        					h.post(new Runnable() {
        						@Override
        						public void run() {
        							showInReplyTo();
        						}
        					});
        	            }
        			});
        			tw.showStatus(inReplyToStatusId);
        		} else {
        			showInReplyTo();
        		}
        	}
        } else if (intent.hasExtra(Intent.EXTRA_TEXT)) {
        	txtStatus.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        } else if (intent.getAction() != null && intent.getAction().equals("com.shootingstar067.EXP")) {
        	//未使用 int level = intent.getIntExtra("level", 1);
        	int exp = intent.getIntExtra("experience", 0);
        	txtStatus.setText(getText(R.string.kuzu).toString().replace("$exp$", String.valueOf(exp)));
        }
	}

	private boolean fromAzyotter;

	private EditText txtStatus;

	private long inReplyToStatusId = -1;
	private TimelineItem inReplyToStatus = null;

	private void showInReplyTo() {
		if (inReplyToStatus != null) {
        	((TextView)findViewById(R.id.tv_update_status_reply_to_user)).setText(inReplyToStatus.from.screenName);
        	((TextView)findViewById(R.id.tv_update_status_reply_to_text)).setText(inReplyToStatus.displayText);
        	findViewById(R.id.layout_reply_to).setVisibility(View.VISIBLE);

        	if (txtStatus.getText().length() == 0)
        		txtStatus.setText(inReplyToStatus != null ? "@" + inReplyToStatus.from.screenName + " " : "");
        }
	}

	public static boolean isTweetIntentUri(Uri uri) {
		return uri != null && uri.getHost().endsWith("twitter.com")
			&& (uri.getPath().equals("/") || uri.getPath().equals("/home") || uri.getPath().equals("/intent/tweet"));
	}

	@Override
    public void onDestroy() {
    	((AccountSelector)findViewById(R.id.as_update_status)).dispose();
    	super.onDestroy();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.update_status_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (!fromAzyotter) {
					startActivity(new Intent(this, AzyotterActivity.class)
						.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				}
				finish();
				return true;
			case R.id.menu_update_status_tweet:
				EditText txtStatus = (EditText)findViewById(R.id.txt_update_status_status);
				String text = txtStatus.getText().toString();
				if (!StringUtil.isNullOrEmpty(text)) {
					startService(new Intent(this, UpdateStatusService.class)
						.putExtra(UpdateStatusService.TEXT, text));
					finish();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
