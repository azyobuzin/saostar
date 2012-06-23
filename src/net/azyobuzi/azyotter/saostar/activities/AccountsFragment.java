package net.azyobuzi.azyotter.saostar.activities;

import jp.ne.hatena.d.shogo0809.widget.SortableListView;
import jp.ne.hatena.d.shogo0809.widget.SortableListView.SimpleDragListener;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.system.Action;
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

public class AccountsFragment extends ListFragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.accounts_list, null);
	}

	private int mDraggingPosition = -1;
	private int selectedIndex = -1;
	private final AccountAdapter adapter = new AccountAdapter();

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

        View detailsFrame = getActivity().findViewById(R.id.layout_account_page_details);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        Accounts.accountsChangedHandler.add(accountsChangedHandler);

    	long id = getActivity().getIntent().getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1);

        if (dualPane) {
        	getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        	showDetails(id != -1 ? Accounts.indexOf(id) : 0);
        } else if (id != -1) {
        	startActivity(new Intent(getActivity(), AccountPreferenceActivity.class)
				.putExtra(AccountPreferenceFragment.ACCOUNT_ID, id)
				.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true));
        }
	}

	@Override
	public void onDestroy() {
		Accounts.accountsChangedHandler.remove(accountsChangedHandler);

		super.onDestroy();
	}

	private final Action accountsChangedHandler = new Action() {
		@Override
		public void invoke() {
			adapter.notifyDataSetChanged();

			if (dualPane) {
				showDetails(selectedIndex);
			}
		}
	};

	public void showDetails(int index) {
		if (Accounts.getAccountsCount() <= index) {
			showDetails(Accounts.getAccountsCount() - 1);
			return;
		}

		selectedIndex = index;

		if (dualPane) {
			if (selectedIndex < 0) {
				getFragmentManager().beginTransaction()
					.replace(R.id.layout_account_page_details, new Fragment(), null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
				return;
			}

			getListView().setItemChecked(index, true);

			AccountPreferenceFragment fragment = new AccountPreferenceFragment();
			Bundle arg = new Bundle();
			arg.putLong(AccountPreferenceFragment.ACCOUNT_ID, adapter.getAccountItem(index).getId());
			fragment.setArguments(arg);

			getFragmentManager().beginTransaction()
				.replace(R.id.layout_account_page_details, fragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
		} else {
			startActivity(new Intent(getActivity(), AccountPreferenceActivity.class)
				.putExtra(AccountPreferenceFragment.ACCOUNT_ID, adapter.getAccountItem(index).getId())
				.putExtra(MainActivity.CALLED_FROM_AZYOTTER, true));
		}
	}

	private class AccountAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return Accounts.getAccountsCount();
		}

		public Account getAccountItem(int index) {
			return Accounts.getAllAccounts().elementAtOrDefault(index, null);
		}

		@Override
		public Object getItem(int arg0) {
			return getAccountItem(arg0);
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
			Account a = getAccountItem(arg0);
			re.setText(a.getScreenName());
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
            Accounts.move(positionFrom, positionTo);
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
