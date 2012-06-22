package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Account
import net.azyobuzi.azyotter.saostar.configuration.Accounts
import android.app.Activity
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment

class AccountPreferenceFragment extends PreferenceFragment {
	static val ACCOUNT_ID = "net.azyobuzi.azyotter.saostar.activities.AccountPreferenceFragment.ACCOUNT_ID"

    override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)

        val arg = getArguments()
        if (arg != null) {
        	val id = getArguments().getLong(ACCOUNT_ID, -1)
        	if (id != -1) setAccountId(id)
        }
    }

	def setAccountId(long id) {
		getPreferenceManager().setSharedPreferencesName("twitter_" + String.valueOf(id))
        addPreferencesFromResource(R.xml.account_preference)
        account = Accounts.get(id)
        findPreference("removeAccount").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			override onPreferenceClick(Preference arg0) {
				Accounts.remove(account)

				val activity = getActivity();
				if (activity instanceof AccountPreferenceActivity) {
					activity.finish()
				}

				true
			}
        })
	}

	private Account account
}
