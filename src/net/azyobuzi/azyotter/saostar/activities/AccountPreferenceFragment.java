package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import android.os.Bundle;
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
	}
}
