package net.azyobuzi.azyotter.saostar.activities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import net.azyobuzi.azyotter.saostar.ActivityUtil;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.TwitterUriGenerator;
import net.azyobuzi.azyotter.saostar.configuration.Command;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Func2;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemId;
import net.azyobuzi.azyotter.saostar.widget.CustomizedUrlImageView;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimelineTabFragment extends ListFragment {
	public TimelineTabFragment() {
		//画面回転用
	}

	public TimelineTabFragment(Tab tab) {
		this.tab = tab;
	}

	private static final String TAB_INDEX = "net.azyobuzi.azyotter.saostar.activities.TimelineTabFragment.TAB_INDEX";

	private Point windowSize = new Point();

	private Tab tab;
	private TimelineItemAdapter adapter = new TimelineItemAdapter();

	private boolean pausing = false;
	private boolean haveToExecuteFilter = false;

	private ActionBar.Tab actionBarTab;

	private Handler h = new Handler();

	private final TreeSet<TimelineItem> items = new TreeSet<TimelineItem>(new Comparator<TimelineItem>() {
		@Override
		public int compare(TimelineItem arg0, TimelineItem arg1) {
			return -(arg0.createdAt.compareTo(arg1.createdAt));
		}
	});

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.empty_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

    	if (tab == null && savedInstanceState != null) {
    		tab = Tabs.get(savedInstanceState.getInt(TAB_INDEX));
    	}

    	((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE))
    		.getDefaultDisplay()
    		.getSize(windowSize);

    	GestureListener listener = new GestureListener();
    	final GestureDetector gestureDetector = new GestureDetector(listener);
    	gestureDetector.setOnDoubleTapListener(listener);
    	getListView().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				gestureDetector.onTouchEvent(motionevent);
				return false;
			}
    	});
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt(TAB_INDEX, Tabs.indexOf(tab));
    }

    @Override
    public void onStart() {
    	super.onStart();

    	setListAdapter(adapter);

    	TimelineItemCollection.addedHandler.add(addedItemHandler);
    	TimelineItemCollection.removedHandler.add(removedItemHandler);
    	tab.filterChangedHandler.add(filterChangedHandler);
    	tab.nameChangedHandler.add(tabNameChangedHandler);
    	filterChangedHandler.invoke(tab);
    }

    @Override
    public void onDestroy() {
    	TimelineItemCollection.addedHandler.remove(addedItemHandler);
    	TimelineItemCollection.removedHandler.remove(removedItemHandler);
    	tab.filterChangedHandler.remove(filterChangedHandler);
    	tab.nameChangedHandler.remove(tabNameChangedHandler);

    	super.onDestroy();
    }

    public void setActionBarTab(ActionBar.Tab actionBarTab) {
    	this.actionBarTab = actionBarTab;
    }

    private void executeFilter() {
    	new AsyncTask<Void, Void, ArrayList<TimelineItem>>() {
			@Override
			protected void onPreExecute() {
				items.clear();
				adapter.notifyDataSetChanged();
			}

			@Override
			protected ArrayList<TimelineItem> doInBackground(Void... params) {
				return TimelineItemCollection.getEnumerable()
					.where(new Func2<TimelineItem, Integer, Boolean>() {
						@Override
						public Boolean invoke(TimelineItem arg0, Integer arg1) {
							return (Boolean)tab.getFilterExpression().invoke(arg0);
						}
					})
					.toArrayList();
			}

			@Override
			protected void onPostExecute(ArrayList<TimelineItem> result) {
				items.addAll(result);
				adapter.notifyDataSetChanged();
			}
		}
		.execute();
    }

    private final Action1<TimelineItem> addedItemHandler = new Action1<TimelineItem>() {
		@Override
		public void invoke(final TimelineItem arg) {
			if ((Boolean)tab.getFilterExpression().invoke(arg)) {
				h.post(new Runnable() {
					@Override
					public void run() {
						items.add(arg);
						adapter.notifyDataSetChanged();
					}
				});
			}
		}
    };

    private final Action1<TimelineItem> removedItemHandler = new Action1<TimelineItem>() {
		@Override
		public void invoke(final TimelineItem arg) {
			h.post(new Runnable() {
				@Override
				public void run() {
					items.remove(arg);
					adapter.notifyDataSetChanged();
				}
			});
		}
    };

    private final Action1<Tab> filterChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			if (pausing)
				haveToExecuteFilter = true;
			else
				executeFilter();
		}
    };

    private final Action1<Tab> tabNameChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			if (actionBarTab != null)
				actionBarTab.setText(tab.getName());
		}
    };

    @Override
    public void onPause() {
    	super.onPause();
    	pausing = true;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	pausing = false;
    	if (haveToExecuteFilter) executeFilter();
    };

    @SuppressWarnings("incomplete-switch")
	private void runCommand(final TimelineItem item, Command command) {
    	switch (command) {
    		case SHOW_DETAIL:
    			if (item.id.type == TimelineItemId.TYPE_TWEET)
	    			startActivity(
	    				new Intent(Intent.ACTION_VIEW)
	    					.setData(TwitterUriGenerator.tweetPermalink(item.from.screenName, item.id.id))
	    					.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true)
	    			);
    			else
    				startActivity(
	    				new Intent(getActivity(), TweetDetailActivity.class)
	    					.putExtra(TweetDetailActivity.ID, item.id)
	    					.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true)
	    			);
    			break;
    		case REPLY:
    			if (item.canReply())
    				item.reply(getActivity());
    			break;
    		case QUOTE:
    			if (item.canQuote())
    				item.quote(getActivity());
    			break;
    		case FAVORITE:
    			if (item.canFavorite())
    				item.favorite(getActivity());
    			break;
    		case FAVORITE_SHOW_DIALOG:
    			if (item.canFavorite())
    				new AlertDialog.Builder(getActivity())
    					.setMessage(R.string.want_to_favorite)
    					.setPositiveButton(android.R.string.ok, new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								item.favorite(getActivity());
							}
    					})
    					.setNegativeButton(android.R.string.cancel, ActivityUtil.emptyDialogOnClickListener)
    					.show();
    			break;
    		case RETWEET:
    			if (item.canRetweet())
    				item.retweet(getActivity());
    			break;
    		case RETWEET_SHOW_DIALOG:
    			if (item.canRetweet())
    				new AlertDialog.Builder(getActivity())
    					.setMessage(R.string.want_to_retweet)
    					.setPositiveButton(android.R.string.ok, new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								item.retweet(getActivity());
							}
    					})
    					.setNegativeButton(android.R.string.cancel, ActivityUtil.emptyDialogOnClickListener)
    					.show();
    			break;
    		case COOK:
    			if (item.canCook())
    				item.cook(getActivity());
    			break;
    		case SHARE:
    			if (item.canShare())
    				item.share(getActivity());
    			break;
    		case SELECT:
    			//TODO
    			break;
    	}
    }


    private class TimelineItemAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			return items.toArray()[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TimelineItem item = (TimelineItem)getItem(arg0);
			View re = arg1 == null
				? getActivity().getLayoutInflater().inflate(R.layout.timeline_item, null)
				: arg1;

			TimelineItemAdapterViewHolder viewHolder = (TimelineItemAdapterViewHolder)re.getTag();

			if (viewHolder == null) {
				viewHolder = new TimelineItemAdapterViewHolder();
				viewHolder.profileImage = (CustomizedUrlImageView)re.findViewById(R.id.iv_timeline_item_profile_image);
				viewHolder.name = (TextView)re.findViewById(R.id.tv_timeline_item_name);
				viewHolder.text = (TextView)re.findViewById(R.id.tv_timeline_item_text);
				viewHolder.dateAndSource = (TextView)re.findViewById(R.id.tv_timeline_item_date_source);
				re.setTag(viewHolder);
			}

			viewHolder.profileImage.setImageUrl(item.from.profileImageUrl);
			viewHolder.name.setText(item.from.screenName + " / " + item.from.name);
			viewHolder.text.setText(item.displayText);
			viewHolder.dateAndSource.setText(DateFormat.getDateTimeInstance().format(item.createdAt) + " / via " + item.sourceName);

			return re;
		}
    }

    private static class TimelineItemAdapterViewHolder {
    	public CustomizedUrlImageView profileImage;
    	public TextView name;
    	public TextView text;
    	public TextView dateAndSource;
    }

    private class GestureListener extends SimpleOnGestureListener {
    	private TimelineItem getItemFromEvent(MotionEvent e) {
    		return (TimelineItem)getListView().getItemAtPosition(
    			getListView().pointToPosition((int)e.getX(), (int)e.getY()));
    	}

    	private boolean gestured;

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			TimelineItem item = getItemFromEvent(e);
			if (!gestured && item != null) {
				gestured = true;
				runCommand(item, Setting.getDoubleTapCommand());
				return true;
			}
			
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			TimelineItem item = getItemFromEvent(e);
			if (!gestured && item != null) {
				gestured = true;
				runCommand(item, Setting.getTapCommand());
				return true;
			}
			
			return false;
		}

		@Override
		public boolean onDown(MotionEvent arg0) {
			gestured = false;
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			TimelineItem item = getItemFromEvent(arg0);
			if (!gestured && item != null) {
				gestured = true;
				runCommand(item, Setting.getLongPressCommand());
			}
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			TimelineItem item = getItemFromEvent(arg0);
			if (!gestured && item != null) {
				if (Math.abs(arg0.getY() - arg1.getY()) < windowSize.y * 0.2) {
					float subWidth = arg0.getX() - arg1.getX();
					if (Math.abs(subWidth) > windowSize.x * 0.4) {
						gestured = true;

						if (subWidth <= 0) {
							//右へフリック
							runCommand(item, Setting.getFlickToRightCommand());
						} else {
							//左へフリック
							runCommand(item, Setting.getFlickToLeftCommand());
						}

						return true;
					}
				}
			}
			return false;
		}
    }
}
