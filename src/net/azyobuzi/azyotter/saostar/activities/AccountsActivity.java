package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.system.Action;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AccountsActivity extends ListActivity {
	private AccountAdapter adapter = new AccountAdapter();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(R.string.registered_accounts);

        getListView().setPadding(15, 0, 15, 0);

        setListAdapter(adapter);

        Accounts.accountsChangedHandler.add(accountsChangedHandler);
        accountsChangedHandler.invoke();
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
			getActionBar().setDisplayHomeAsUpEnabled(Accounts.getAccountsCount() != 0);
		}
	};

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.accounts_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (getIntent().getBooleanExtra("firstRun", false)) {
					if (Accounts.getAccountsCount() > 0) {
						startActivity(new Intent(this, AzyotterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						finish();
					}
				} else {
					finish();
				}
				return true;
			case R.id.menu_accounts_add:
				startActivity(new Intent(this, LoginActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);

		Account item = (Account)listView.getItemAtPosition(position);
		startActivity(new Intent(this, EditAccountActivity.class).putExtra("id", item.id));
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
			TextView re = (TextView)getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
			re.setText(getAccountItem(arg0).screenName);
			return re;
		}

	}
}
