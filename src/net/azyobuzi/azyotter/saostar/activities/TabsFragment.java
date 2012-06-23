package net.azyobuzi.azyotter.saostar.activities;

import jp.ne.hatena.d.shogo0809.widget.SortableListView;
import jp.ne.hatena.d.shogo0809.widget.SortableListView.SimpleDragListener;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private final TabAdapter adapter = new TabAdapter();

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

    	int index = getActivity().getIntent().getIntExtra(TabPreferenceActivity.TAB_INDEX, -1);

        if (index != -1) {
        	startActivity(new Intent(getActivity(), TabPreferenceActivity.class)
				.putExtra(TabPreferenceActivity.TAB_INDEX, index)
				.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true));
        }
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged(); //タブ名変更に対応
	}

	public void showDetails(int index) {
		if (Tabs.getTabsCount() <= index) {
			showDetails(Tabs.getTabsCount() - 1);
			return;
		}

		startActivity(new Intent(getActivity(), TabPreferenceActivity.class)
			.putExtra(TabPreferenceActivity.TAB_INDEX, index)
			.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true));
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
