package net.azyobuzi.azyotter.saostar.activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Func2;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.widget.CustomizedUrlImageView;
import android.app.ActionBar;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	private Tab tab;
	private TimelineItemAdapter adapter = new TimelineItemAdapter();

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
		public void invoke(TimelineItem arg) {
			items.remove(arg);
		}
    };

    private final Action1<Tab> filterChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
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
    };

    private final Action1<Tab> tabNameChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			if (actionBarTab != null)
				actionBarTab.setText(tab.getName());
		}
    };


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
			viewHolder.dateAndSource.setText(item.createdAt.toLocaleString() + " / via " + item.sourceName);

			return re;
		}
    }

    private static class TimelineItemAdapterViewHolder {
    	public CustomizedUrlImageView profileImage;
    	public TextView name;
    	public TextView text;
    	public TextView dateAndSource;
    }
}
