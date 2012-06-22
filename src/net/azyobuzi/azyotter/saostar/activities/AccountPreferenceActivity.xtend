package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Accounts
import android.app.ActionBar
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem

class AccountPreferenceActivity extends Activity {
    override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_preference_page)

        val id = getIntent().getLongExtra(AccountPreferenceFragment.ACCOUNT_ID, -1)

        val actionBar = getActionBar()
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setSubtitle(Accounts.get(id).getScreenName())

        ((AccountPreferenceFragment)getFragmentManager().findFragmentById(R.id.fragment_account_preference))
        	.setAccountId(id)
    }

	override onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish()
				true
			default:
				super.onOptionsItemSelected(item)
		}
	}
}
