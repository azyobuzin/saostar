package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class AccountPreferenceActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTheme(Setting.getTheme());
        setContentView(R.layout.account_preference_page);

        long id = getIntent().getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(Accounts.get(id).getScreenName());

        ((AccountPreferenceFragment)getFragmentManager().findFragmentById(R.id.fragment_account_preference))
        	.setAccountId(id);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
