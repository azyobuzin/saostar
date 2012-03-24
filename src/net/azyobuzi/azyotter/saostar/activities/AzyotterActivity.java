package net.azyobuzi.azyotter.saostar.activities;

import java.util.ArrayList;

import jp.sharakova.android.urlimageview.UrlImageView;
import net.azyobuzi.azyotter.saostar.ActivityUtil;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService;
import net.azyobuzi.azyotter.saostar.system.Action;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Action2;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class AzyotterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Accounts.getAccountsCount() == 0) {
        	startActivity(new Intent(this, AccountsActivity.class).putExtra("firstRun", true));
        	finish();
        	return;
        }

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tabs.getAllTabs().forEach(new Action2<Tab, Integer>() {
			@Override
			public void invoke(Tab arg0, Integer arg1) {
				addedTabHandler.invoke(arg0);
			}
        });
        Tabs.addedHandler.add(addedTabHandler);
        Tabs.removedHandler.add(removedTabHandler);

        Accounts.selectedAccountChangedHandler.add(selectedAccountChangedHandler);
        selectedAccountChangedHandler.invoke();

        findViewById(R.id.btn_main_update_status).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText tweetBox = (EditText)findViewById(R.id.txt_main_tweet);
				String text = tweetBox.getText().toString();
				if (StringUtil.isNullOrEmpty(text)) return;

				startService(new Intent(AzyotterActivity.this, UpdateStatusService.class).putExtra("text", text));
				tweetBox.setText("");
			}
        });

        findViewById(R.id.iv_main_selected_account).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final ArrayList<Account> accounts = Accounts.getAllAccounts().toArrayList();
				CharSequence[] screenNames = new CharSequence[accounts.size()];
				for (int i = 0; i < accounts.size(); i++) {
					screenNames[i] = accounts.get(i).screenName;
				}

				new AlertDialog.Builder(AzyotterActivity.this)
					.setTitle(R.string.select_account_to_use)
					.setItems(screenNames, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Accounts.setSelectedAccount(accounts.get(which));
						}
					})
					.setPositiveButton(android.R.string.cancel, ActivityUtil.emptyDialogOnClickListener)
					.show();
			}
        });
    }

    @Override
    public void onDestroy() {
    	Accounts.selectedAccountChangedHandler.remove(selectedAccountChangedHandler);
    	super.onDestroy();
    }

    private final Action selectedAccountChangedHandler = new Action() {
		@Override
		public void invoke() {
			((UrlImageView)findViewById(R.id.iv_main_selected_account)).setImageUrl(
				"https://api.twitter.com/1/users/profile_image/" + Accounts.getSelectedAccount().screenName + ".json"
			);
		}
	};

	private final Action1<Tab> addedTabHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			ActionBar actionBar = getActionBar();
			actionBar.addTab(
				actionBar.newTab()
					.setText(arg.getName())
					.setTabListener(new TimelineTabListener(arg))
					.setTag(arg)
			);
		}
	};

	private final Action1<Tab> removedTabHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			ActionBar actionBar = getActionBar();
			for (int i = 0; i < actionBar.getTabCount(); i++) {
				if (actionBar.getTabAt(i).getTag() == arg) {
					actionBar.removeTabAt(i);
					return;
				}
			}
		}
	};

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_main_accounts:
				startActivity(new Intent(this, AccountsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    private class TimelineTabListener implements TabListener {
    	public TimelineTabListener(Tab tab) {
    		mFragment = new TimelineTabFragment(tab);
    	}

    	private TimelineTabFragment mFragment;

		@Override
		public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {

		}

		@Override
		public void onTabSelected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			arg1.add(R.id.fragment_content, mFragment, null);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			arg1.remove(mFragment);
		}
    }
}