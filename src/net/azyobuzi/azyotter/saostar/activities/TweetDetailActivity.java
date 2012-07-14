package net.azyobuzi.azyotter.saostar.activities;

import java.text.DateFormat;
import java.util.ArrayList;

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
import net.azyobuzi.azyotter.saostar.TwitterUriGenerator;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Action2;
import net.azyobuzi.azyotter.saostar.system.Func2;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemId;
import net.azyobuzi.azyotter.saostar.widget.CustomizedUrlImageView;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
		((TextView)findViewById(R.id.tv_tweet_detail_date)).setText(DateFormat.getDateTimeInstance().format(item.createdAt));
		setListAdapter(new OperationAdapter(item));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		((OperationInterface)l.getItemAtPosition(position)).operate();

		super.onListItemClick(l, v, position, id);
	}
	
	private class OperationAdapter extends BaseAdapter {
		public OperationAdapter(final TimelineItem item) {
			Enumerable.from(item.entities.urls)
				.concat(Enumerable.from(item.entities.media))
				.distinct(new Func2<Uri, Uri, Boolean>() {
					@Override
					public Boolean invoke(Uri arg0, Uri arg1) {
						return arg0.toString().equals(arg1.toString());
					}
				})
				.forEach(new Action2<Uri, Integer>() {
					@Override
					public void invoke(final Uri arg0, Integer arg1) {
						operations.add(new OperationInterface() {
							@Override
							public void operate() {
								startActivity(new Intent(Intent.ACTION_VIEW)
									.setData(arg0)
									.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true)
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								);
							}
							
							@Override
							public String getName() {
								return arg0.toString();
							}
						});
					}
				});
			
			Enumerable.from(item.entities.hashtags)
				.distinct()
				.forEach(new Action2<String, Integer>() {
					@Override
					public void invoke(final String arg0, Integer arg1) {
						operations.add(new OperationInterface() {
							@Override
							public void operate() {
								startActivity(new Intent(Intent.ACTION_VIEW)
									.setData(TwitterUriGenerator.search("#" + arg0))
									.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true)
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								);
							}
							
							@Override
							public String getName() {
								return "#" + arg0.toString();
							}
						});
					}
				});
			
			Enumerable.from(item.entities.userMentions)
				.concat(Enumerable.oneElement(item.from.screenName))
				.concat(Enumerable.oneElement(item.to != null ? item.to.screenName : null))
				.where(new Func2<String, Integer, Boolean>() {
					@Override
					public Boolean invoke(String arg0, Integer arg1) {
						return !StringUtil.isNullOrEmpty(arg0);
					}
				})
				.distinct()
				.forEach(new Action2<String, Integer>() {
					@Override
					public void invoke(final String arg0, Integer arg1) {
						operations.add(new OperationInterface() {
							@Override
							public void operate() {
								startActivity(new Intent(Intent.ACTION_VIEW)
									.setData(TwitterUriGenerator.userPermalink(arg0))
									.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true)
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								);
							}
							
							@Override
							public String getName() {
								return "@" + arg0.toString();
							}
						});
					}
				});
			
			if (item.canReply()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.reply);
					}

					@Override
					public void operate() {
						item.reply(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
			
			if (item.canQuote()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.quote);
					}

					@Override
					public void operate() {
						item.quote(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
			
			if (item.canFavorite()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.favorite);
					}

					@Override
					public void operate() {
						item.favorite(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
			
			if (item.canRetweet()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.retweet);
					}

					@Override
					public void operate() {
						item.retweet(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
			
			if (item.canCook()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.cook);
					}

					@Override
					public void operate() {
						item.cook(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
			
			if (item.canShare()) {
				operations.add(new OperationInterface() {
					@Override
					public String getName() {
						return getString(R.string.share);
					}

					@Override
					public void operate() {
						item.share(TweetDetailActivity.this);
						
						if (Setting.getCloseTweetDetailViewAfterOperation())
							finish();
					}
				});
			}
		}
		
		private final ArrayList<OperationInterface> operations = new ArrayList<OperationInterface>();

		@Override
		public int getCount() {
			return operations.size();
		}

		public OperationInterface getOperation(int index) {
			return operations.get(index);
		}
		
		@Override
		public Object getItem(int arg0) {
			return getOperation(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			OperationInterface item = getOperation(arg0);
			
			TextView view = (TextView)arg1;
			if (view == null)
				view = (TextView)getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, null);
			view.setText(item.getName());
			return view;
		}
	}
	
	private interface OperationInterface {
		String getName();
		void operate();
	}
}
