package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class AccountPreferenceFragment extends PreferenceFragment {
	public static final String ACCOUNT_ID = "net.azyobuzi.azyotter.saostar.activities.AccountPreferenceFragment.ACCOUNT_ID";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if (arg != null) {
        	long id = getArguments().getLong(ACCOUNT_ID, -1);
        	if (id != -1) setAccountId(id);
        }
    }

	public void setAccountId(long id) {
		getPreferenceManager().setSharedPreferencesName("twitter_" + String.valueOf(id));
        addPreferencesFromResource(R.xml.account_preference);
        account = Accounts.get(id);
        findPreference("removeAccount").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Accounts.remove(account);

				Activity activity = getActivity();
				if (activity instanceof AccountPreferenceActivity) {
					activity.finish();
				}

				return true;
			}
        });
        findPreference("useUserStream").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				account.setUseUserStream((Boolean)newValue);
				return true;
			}
        });
	}

	private Account account;
}
