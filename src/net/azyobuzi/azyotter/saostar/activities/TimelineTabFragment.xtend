package net.azyobuzi.azyotter.saostar.activities

import java.util.ArrayList
import java.util.Comparator
import java.util.TreeSet

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Setting
import net.azyobuzi.azyotter.saostar.configuration.Tab
import net.azyobuzi.azyotter.saostar.configuration.Tabs
import net.azyobuzi.azyotter.saostar.system.Action1
import net.azyobuzi.azyotter.saostar.system.Func2
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection
import net.azyobuzi.azyotter.saostar.widget.CustomizedUrlImageView
import android.app.ActionBar
import android.app.ListFragment
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Display
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.OnGestureListener
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.TextView

class TimelineTabFragment extends ListFragment {
	new() {
		//画面回転用
	}

	new(Tab tab) {
		this.tab = tab
	}

	private static val TAB_INDEX = "net.azyobuzi.azyotter.saostar.activities.TimelineTabFragment.TAB_INDEX"

	private int windowWidth
	private int windowHeight

	private Tab tab
	private TimelineItemAdapter adapter = new TimelineItemAdapter()

	private boolean pausing = false
	private boolean haveToExecuteFilter = false

	private ActionBar.Tab actionBarTab

	private Handler h = new Handler()

	private TreeSet<TimelineItem> items = new TreeSet<TimelineItem>(new Comparator<TimelineItem>() {
		override compare(TimelineItem arg0, TimelineItem arg1) {
			-(arg0.createdAt.compareTo(arg1.createdAt))
		}
	})

    override onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	inflater.inflate(R.layout.empty_list, null)
    }

    override onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState)

    	if (tab == null && savedInstanceState != null) {
    		tab = Tabs.get(savedInstanceState.getInt(TAB_INDEX))
    	}

    	val disp = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
    	windowWidth = disp.getWidth()
    	windowHeight = disp.getHeight()

    	val listener = new GestureListener()
    	val gestureDetector = new GestureDetector(listener)
    	gestureDetector.setOnDoubleTapListener(listener)
    	getListView().setOnTouchListener(new OnTouchListener() {
			override onTouch(View view, MotionEvent motionevent) {
				gestureDetector.onTouchEvent(motionevent)
				false
			}
    	})
    }

    override onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState)
    	outState.putInt(TAB_INDEX, Tabs.indexOf(tab))
    }

    override onStart() {
    	super.onStart()

    	setListAdapter(adapter)

    	TimelineItemCollection.addedHandler.add(addedItemHandler)
    	TimelineItemCollection.removedHandler.add(removedItemHandler)
    	tab.filterChangedHandler.add(filterChangedHandler)
    	tab.nameChangedHandler.add(tabNameChangedHandler)
    	filterChangedHandler.invoke(tab)
    }

    override onDestroy() {
    	TimelineItemCollection.addedHandler.remove(addedItemHandler)
    	TimelineItemCollection.removedHandler.remove(removedItemHandler)
    	tab.filterChangedHandler.remove(filterChangedHandler)
    	tab.nameChangedHandler.remove(tabNameChangedHandler)

    	super.onDestroy()
    }

    def setActionBarTab(ActionBar.Tab actionBarTab) {
    	this.actionBarTab = actionBarTab;
    }

    def private executeFilter() {
    	new AsyncTask<Void, Void, ArrayList<TimelineItem>>() {
			override onPreExecute() {
				items.clear()
				adapter.notifyDataSetChanged()
			}

			override doInBackground(Void... params) {
				TimelineItemCollection.getEnumerable()
					.where(new Func2<TimelineItem, Integer, Boolean>() {
						override invoke(TimelineItem arg0, Integer arg1) {
							return (Boolean)tab.getFilterExpression().invoke(arg0)
						}
					})
					.toArrayList()
			}

			override onPostExecute(ArrayList<TimelineItem> result) {
				items.addAll(result)
				adapter.notifyDataSetChanged()
			}
		}
		.execute()
    }

    private val Action1<TimelineItem> addedItemHandler = new Action1<TimelineItem>() {
		override invoke(final TimelineItem arg) {
			if ((Boolean)tab.getFilterExpression().invoke(arg)) {
				h.post(new Runnable() {
					override run() {
						items.add(arg)
						adapter.notifyDataSetChanged()
					}
				})
			}
		}
    }

    private val Action1<TimelineItem> removedItemHandler = new Action1<TimelineItem>() {
		override invoke(final TimelineItem arg) {
			h.post(new Runnable() {
				override run() {
					items.remove(arg)
					adapter.notifyDataSetChanged()
				}
			});
		}
    }

    private val Action1<Tab> filterChangedHandler = new Action1<Tab>() {
		override invoke(Tab arg) {
			if (pausing)
				haveToExecuteFilter = true
			else
				executeFilter()
		}
    }

    private val Action1<Tab> tabNameChangedHandler = new Action1<Tab>() {
		override invoke(Tab arg) {
			if (actionBarTab != null)
				actionBarTab.setText(tab.getName())
		}
    }

    override onPause() {
    	super.onPause()
    	pausing = true
    }

    override onResume() {
    	super.onResume()
    	pausing = false
    	if (haveToExecuteFilter) executeFilter()
    };

    def private runCommand(TimelineItem item, int command) {
    	switch (command) {
    		case Setting.COMMAND_FAVORITE:
    			item.favorite(getActivity())
    		case Setting.COMMAND_RETWEET:
    			item.retweet(getActivity())
    	}
    }


    private class TimelineItemAdapter extends BaseAdapter {
		override getCount() {
			items.size()
		}

		override getItem(int arg0) {
			items.toArray()[arg0]
		}

		override getItemId(int arg0) {
			arg0;
		}

		override getView(int arg0, View arg1, ViewGroup arg2) {
			val item = (TimelineItem)getItem(arg0)
			val re = arg1 == null
				? getActivity().getLayoutInflater().inflate(R.layout.timeline_item, null)
				: arg1

			val viewHolder = (TimelineItemAdapterViewHolder)re.getTag()

			if (viewHolder == null) {
				viewHolder = new TimelineItemAdapterViewHolder()
				viewHolder.profileImage = (CustomizedUrlImageView)re.findViewById(R.id.iv_timeline_item_profile_image)
				viewHolder.name = (TextView)re.findViewById(R.id.tv_timeline_item_name)
				viewHolder.text = (TextView)re.findViewById(R.id.tv_timeline_item_text)
				viewHolder.dateAndSource = (TextView)re.findViewById(R.id.tv_timeline_item_date_source)
				re.setTag(viewHolder)
			}

			viewHolder.profileImage.setImageUrl(item.from.profileImageUrl)
			viewHolder.name.setText(item.from.screenName + " / " + item.from.name)
			viewHolder.text.setText(item.displayText)
			viewHolder.dateAndSource.setText(item.createdAt.toLocaleString() + " / via " + item.sourceName)

			re
		}
    }

    private static class TimelineItemAdapterViewHolder {
    	public CustomizedUrlImageView profileImage
    	public TextView name
    	public TextView text
    	public TextView dateAndSource
    }

    private class GestureListener implements OnGestureListener, OnDoubleTapListener {
    	def private getItemFromEvent(MotionEvent e) {
    		getListView().getItemAtPosition(getListView().pointToPosition((int)e.getX(), (int)e.getY()))
    			as TimelineItem
    	}

    	private boolean gestured

		override onDoubleTap(MotionEvent e) {
			//Log.d("debug", getItemFromEvent(e).from.screenName + " double tap");
			false
		}

		override onDoubleTapEvent(MotionEvent e) {
			//Log.d("debug", getItemFromEvent(e).from.screenName + " double tap event");
			false
		}

		override onSingleTapConfirmed(MotionEvent e) {
			//Log.d("debug", getItemFromEvent(e).from.screenName + " single tap confirmed");
			false
		}

		override onDown(MotionEvent arg0) {
			//Log.d("debug", getItemFromEvent(arg0).from.screenName + " down");
			gestured = false
			false
		}

		override onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			//Log.d("debug", getItemFromEvent(arg0).from.screenName + " fling");
			false
		}

		override onLongPress(MotionEvent arg0) {
			//Log.d("debug", getItemFromEvent(arg0).from.screenName + " long press");
		}

		override onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			val item = getItemFromEvent(arg0);
			if (!gestured && item != null) {
				if (Math.abs(arg0.getY() - arg1.getY()) < windowHeight * 0.2) {
					val subWidth = arg0.getX() - arg1.getX()
					if (Math.abs(subWidth) > windowWidth * 0.4) {
						gestured = true

						if (subWidth <= 0) {
							//右へフリック
							runCommand(item, Setting.getFlickToRightCommand())
						} else {
							//左へフリック
							runCommand(item, Setting.getFlickToLeftCommand())
						}

						true
					}
				}
			}
			false
		}

		override onShowPress(MotionEvent arg0) {
			//Log.d("debug", getItemFromEvent(arg0).from.screenName + " show press");
		}

		override onSingleTapUp(MotionEvent arg0) {
			//Log.d("debug", getItemFromEvent(arg0).from.screenName + " single tap up");
			false
		}

    }
}
