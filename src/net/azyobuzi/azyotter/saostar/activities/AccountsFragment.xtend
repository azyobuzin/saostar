package net.azyobuzi.azyotter.saostar.activities

import jp.ne.hatena.d.shogo0809.widget.SortableListView
import jp.ne.hatena.d.shogo0809.widget.SortableListView.SimpleDragListener
import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Account
import net.azyobuzi.azyotter.saostar.configuration.Accounts
import net.azyobuzi.azyotter.saostar.system.Action
import android.app.Fragment
import android.app.FragmentTransaction
import android.app.ListFragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.TextView

class AccountsFragment extends ListFragment {
    override onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		inflater.inflate(R.layout.accounts_list, null)
	}

	private int mDraggingPosition = -1
	private int selectedIndex = -1
	private val adapter = new AccountAdapter()

	private boolean dualPane

	override onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState)

        setListAdapter(adapter)

        val lv = getListView() as SortableListView;
        lv.setDragListener(new DragListener())
        lv.setSortable(true)
        lv.setOnItemClickListener(new OnItemClickListener() {
			overrride onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showDetails(arg2)
			}
        });

        val detailsFrame = getActivity().findViewById(R.id.layout_account_page_details)
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE

        Accounts.accountsChangedHandler.add(accountsChangedHandler)

    	val id = getActivity().getIntent().getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1)

        if (dualPane) {
        	getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
        	showDetails(id != -1 ? Accounts.indexOf(id) : 0)
        } else if (id != -1) {
        	startActivity(new Intent(getActivity(), AccountPreferenceActivity.class)
				.putExtra(AccountPreferenceFragment.ACCOUNT_ID, id)
				.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true))
        }
	}

	override onDestroy() {
		Accounts.accountsChangedHandler.remove(accountsChangedHandler)

		super.onDestroy()
	}

	private Action accountsChangedHandler = new Action() {
		override invoke() {
			adapter.notifyDataSetChanged()

			if (dualPane) {
				showDetails(selectedIndex)
			}
		}
	}

	def showDetails(int index) {
		if (Accounts.getAccountsCount() <= index) {
			showDetails(Accounts.getAccountsCount() - 1)
			return
		}

		selectedIndex = index

		if (dualPane) {
			if (selectedIndex < 0) {
				getFragmentManager().beginTransaction()
					.replace(R.id.layout_account_page_details, new Fragment(), null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit()
				return
			}

			getListView().setItemChecked(index, true)

			AccountPreferenceFragment fragment = new AccountPreferenceFragment()
			Bundle arg = new Bundle()
			arg.putLong(AccountPreferenceFragment.ACCOUNT_ID, adapter.getAccountItem(index).getId())
			fragment.setArguments(arg)

			getFragmentManager().beginTransaction()
				.replace(R.id.layout_account_page_details, fragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit()
		} else {
			startActivity(new Intent(getActivity(), AccountPreferenceActivity.class)
				.putExtra(AccountPreferenceFragment.ACCOUNT_ID, adapter.getAccountItem(index).getId())
				.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true))
		}
	}

	private class AccountAdapter extends BaseAdapter {
		override getCount() {
			Accounts.getAccountsCount()
		}

		def getAccountItem(int index) {
			Accounts.getAllAccounts().elementAtOrDefault(index, null)
		}

		override getItem(int arg0) {
			getAccountItem(arg0)
		}

		override getItemId(int arg0) {
			arg0
		}

		override getView(int arg0, View arg1, ViewGroup arg2) {
			TextView re = arg1 != null
				? (TextView)arg1
				: (TextView)getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, null)
			Account a = getAccountItem(arg0)
			re.setText(a.getScreenName())
			re.setVisibility(arg0 == mDraggingPosition ? View.INVISIBLE : View.VISIBLE)
			re
		}

	}

	private class DragListener extends SimpleDragListener {
		override onStartDrag(int position) {
            mDraggingPosition = position
            getListView().invalidateViews()
            position
        }

        override onDuringDrag(int positionFrom, int positionTo) {
            if (positionFrom < 0 || positionTo < 0
                    || positionFrom == positionTo) {
                positionFrom
            }
            Accounts.move(positionFrom, positionTo)
            mDraggingPosition = positionTo
            if (dualPane && positionFrom == selectedIndex)
            	getListView().setItemChecked(selectedIndex = positionTo, true)
            getListView().invalidateViews()
            positionTo
        }

        override onStopDrag(int positionFrom, int positionTo) {
            mDraggingPosition = -1
            getListView().invalidateViews()
            super.onStopDrag(positionFrom, positionTo)
        }
	}
}
