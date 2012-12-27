package net.azyobuzi.saostar;

import net.azyobuzi.saostar.model.configuration.Accounts;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class AccountActivity extends Activity
{
    public static final String ACCOUNT_ID = "net.azyobuzi.saostar.AccountActivity.ACCOUNT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(Accounts.fromId(getIntent().getLongExtra(ACCOUNT_ID, -1)).getScreenName());

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, AccountFragment.newInstance(getIntent().getLongExtra(ACCOUNT_ID, -1)))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AccountFragment extends PreferenceFragment
    {
        private long id = -1;

        public static AccountFragment newInstance(long id)
        {
            AccountFragment instance = new AccountFragment();
            instance.id = id;
            return instance;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            if (id == -1) id = savedInstanceState.getLong(ACCOUNT_ID, -1);

            // TODO: 設定表示
        }

        @Override
        public void onSaveInstanceState(Bundle outState)
        {
            super.onSaveInstanceState(outState);
            outState.putLong(ACCOUNT_ID, id);
        }
    }
}
