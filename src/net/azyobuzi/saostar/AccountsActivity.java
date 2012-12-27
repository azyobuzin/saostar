package net.azyobuzi.saostar;

import net.azyobuzi.saostar.model.configuration.Account;
import net.azyobuzi.saostar.model.configuration.Accounts;
import net.azyobuzi.saostar.util.Action2;
import net.azyobuzi.saostar.util.ListChangedEventArgs;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class AccountsActivity extends ListActivity
{
    private final AccountAdapter adapter = new AccountAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startActivity(new Intent(AccountsActivity.this, AccountActivity.class)
                        .putExtra(AccountActivity.ACCOUNT_ID, adapter.getAccountItem(position).getId()));
            }
        });

        Accounts.getList().listChangedEvent.add(accountsChangedHandler);
    }

    @Override
    public void onDestroy()
    {
        Accounts.getList().listChangedEvent.remove(accountsChangedHandler);
        super.onDestroy();
    }

    private final Action2<Object, ListChangedEventArgs<Account>> accountsChangedHandler = new Action2<Object, ListChangedEventArgs<Account>>()
    {
        @Override
        public void invoke(Object sender, ListChangedEventArgs<Account> e)
        {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_accounts, menu);
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
            case R.id.menu_add_account:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AccountAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return Accounts.getList().size();
        }

        public Account getAccountItem(int index)
        {
            try
            {
                return Accounts.getList().get(index);
            }
            catch (IndexOutOfBoundsException ex)
            {
                return null;
            }
        }

        @Override
        public Object getItem(int position)
        {
            return getAccountItem(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            TextView re = (TextView)(convertView != null
                    ? convertView
                    : getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, null));
            Account a = getAccountItem(position);
            re.setText(a.getScreenName());
            return re;
        }
    }

}
