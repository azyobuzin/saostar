package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.system.Action;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AccountsActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accounts_page);

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
			getActionBar().setDisplayHomeAsUpEnabled(Accounts.getAccountsCount() != 0);
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra(AccountPreferenceFragment.ACCOUNT_ID)) {
			((AccountsFragment)getFragmentManager().findFragmentById(R.id.fragment_accounts_list))
				.showDetails(Accounts.indexOf(intent.getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1)));
		}
	}

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
						startActivity(new Intent(this, AzyotterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true));
						finish();
					}
				} else {
					finish();
				}
				return true;
			case R.id.menu_accounts_add:
				startActivity(new Intent(this, LoginActivity.class).putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}