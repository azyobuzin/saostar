package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class AccountPreferenceActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_preference_page);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ((AccountPreferenceFragment)getFragmentManager().findFragmentById(R.id.fragment_account_preference))
        	.setAccountId(getIntent().getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1));
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
