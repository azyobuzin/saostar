package net.azyobuzi.azyotter.saostar.activities;

import jp.ne.hatena.d.shogo0809.widget.SortableListView;
import jp.ne.hatena.d.shogo0809.widget.SortableListView.SimpleDragListener;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Action3;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TabsFragment extends ListFragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tabs_list, null);
	}

	private int mDraggingPosition = -1;
	private int selectedIndex = -1;
	private final TabAdapter adapter = new TabAdapter();

	private boolean dualPane;

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(adapter);

        SortableListView lv = (SortableListView)getListView();
        lv.setDragListener(new DragListener());
        lv.setSortable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showDetails(arg2);
			}
        });

        //View detailsFrame = getActivity().findViewById(R.id.layout_tab_details);
        //dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        Tabs.addedHandler.add(tabsChangedHandler);
		Tabs.removedHandler.add(tabsChangedHandler);
		Tabs.movedHandler.add(tabsMovedHandler);

    	int index = getActivity().getIntent().getIntExtra(TabPreferenceFragment.TAB_INDEX, -1);

        if (dualPane) {
        	getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        	showDetails(index != -1 ? index : 0);
        } else if (index != -1) {
        	startActivity(new Intent(getActivity(), TabPreferenceActivity.class)
				.putExtra(TabPreferenceFragment.TAB_INDEX, index)
				.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true));
        }
	}

	@Override
	public void onDestroy() {
		Tabs.addedHandler.remove(tabsChangedHandler);
		Tabs.removedHandler.remove(tabsChangedHandler);
		Tabs.movedHandler.remove(tabsMovedHandler);

		super.onDestroy();
	}

	private final Action1<Tab> tabsChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab tab) {
			adapter.notifyDataSetChanged();

			if (dualPane) {
				showDetails(selectedIndex);
			}
		}
	};

	private final Action3<Tab, Integer, Integer> tabsMovedHandler = new Action3<Tab, Integer, Integer>() {
		@Override
		public void invoke(Tab tab, Integer from, Integer to) {
			tabsChangedHandler.invoke(tab);
		}
	};

	public void showDetails(int index) {
		if (Tabs.getTabsCount() <= index) {
			showDetails(Tabs.getTabsCount() - 1);
			return;
		}

		selectedIndex = index;

		if (dualPane) {
			/*if (selectedIndex < 0) {
				getFragmentManager().beginTransaction()
					.replace(R.id.layout_tab_details, new Fragment(), null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
				return;
			}

			getListView().setItemChecked(index, true);

			TabPreferenceFragment fragment = new TabPreferenceFragment();
			Bundle arg = new Bundle();
			arg.putLong(TabPreferenceFragment.TAB_INDEX, index);
			fragment.setArguments(arg);

			getFragmentManager().beginTransaction()
				.replace(R.id.layout_tab_details, fragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();*/
		} else {
			startActivity(new Intent(getActivity(), TabPreferenceActivity.class)
				.putExtra(TabPreferenceFragment.TAB_INDEX, index)
				.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true));
		}
	}

	private class TabAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return Tabs.getTabsCount();
		}

		public Tab getTabItem(int index) {
			return Tabs.get(index);
		}

		@Override
		public Object getItem(int arg0) {
			return getTabItem(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TextView re = arg1 != null
				? (TextView)arg1
				: (TextView)getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, null);
			Tab t = getTabItem(arg0);
			re.setText(t.getName());
			re.setVisibility(arg0 == mDraggingPosition ? View.INVISIBLE : View.VISIBLE);
			return re;
		}

	}

	private class DragListener extends SimpleDragListener {
		@Override
        public int onStartDrag(int position) {
            mDraggingPosition = position;
            getListView().invalidateViews();
            return position;
        }

        @Override
        public int onDuringDrag(int positionFrom, int positionTo) {
            if (positionFrom < 0 || positionTo < 0
                    || positionFrom == positionTo) {
                return positionFrom;
            }
            Tabs.move(positionFrom, positionTo);
            mDraggingPosition = positionTo;
            if (dualPane && positionFrom == selectedIndex)
            	getListView().setItemChecked(selectedIndex = positionTo, true);
            getListView().invalidateViews();
            return positionTo;
        }

        @Override
        public boolean onStopDrag(int positionFrom, int positionTo) {
            mDraggingPosition = -1;
            getListView().invalidateViews();
            return super.onStopDrag(positionFrom, positionTo);
        }
	}
}
