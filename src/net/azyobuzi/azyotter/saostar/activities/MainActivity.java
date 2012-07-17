package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Action2;
import net.azyobuzi.azyotter.saostar.system.Action3;
import net.azyobuzi.azyotter.saostar.widget.AccountSelector;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */

	public static final String CALLED_FROM_AZYOTTER = "net.azyobuzi.azyotter.saostar.activities.MainActivity.CALLED_FROM_AZYOTTER";
	public static final String TAB_INDEX = "net.azyobuzi.azyotter.saostar.activities.MainActivity.TAB_INDEX";

    private boolean tabChanged = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTheme(Setting.getTheme());
        setContentView(R.layout.main);

        if (Accounts.getAccountsCount() == 0) {
        	startActivity(new Intent(this, AccountsActivity.class)
        		.putExtra(AccountsActivity.FIRST_RUN, true)
        		.putExtra(CALLED_FROM_AZYOTTER, true));
        	finish();
        	return;
        }

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        createTabs();
        Tabs.addedHandler.add(tabChangedHandler);
        Tabs.removedHandler.add(tabChangedHandler);
        Tabs.movedHandler.add(movedTabHandler);
        
        if (savedInstanceState != null) {
        	int index = savedInstanceState.getInt(TAB_INDEX, -1);
        	if (index != -1)
        		actionBar.selectTab(actionBar.getTabAt(index));
        }

        findViewById(R.id.btn_main_update_status).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText tweetBox = (EditText)findViewById(R.id.txt_main_tweet);
				String text = tweetBox.getText().toString();
				if (StringUtil.isNullOrEmpty(text)) return;

				startService(new Intent(MainActivity.this, UpdateStatusService.class)
					.putExtra(UpdateStatusService.TEXT, text));
				tweetBox.setText("");
			}
        });
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		ActionBar.Tab selectedTab = getActionBar().getSelectedTab();
		if (selectedTab != null)
			outState.putInt(TAB_INDEX, Tabs.indexOf((Tab)selectedTab.getTag()));
	}

    @Override
    public void onDestroy() {
    	((AccountSelector)findViewById(R.id.as_main)).dispose();
    	Tabs.addedHandler.remove(tabChangedHandler);
        Tabs.removedHandler.remove(tabChangedHandler);
        Tabs.movedHandler.remove(movedTabHandler);
    	super.onDestroy();
    }

    @Override
    protected void onResume() {
    	super.onResume();

    	if (tabChanged) {
    		getActionBar().removeAllTabs();
    		createTabs();
    	}
    }

    private void createTabs() {
    	final ActionBar actionBar = getActionBar();
    	Tabs.getAllTabs().forEach(new Action2<Tab, Integer>() {
			@Override
			public void invoke(Tab arg0, Integer arg1) {
				actionBar.addTab(
					actionBar.newTab()
						.setText(arg0.getName())
						.setTabListener(new TimelineTabListener(arg0))
						.setTag(arg0)
				);
			}
        });
    }

	private final Action1<Tab> tabChangedHandler = new Action1<Tab>() {
		@Override
		public void invoke(Tab arg) {
			tabChanged = true;
		}
	};

	private final Action3<Tab, Integer, Integer> movedTabHandler = new Action3<Tab, Integer, Integer>() {
		@Override
		public void invoke(Tab arg0, Integer arg1, Integer arg2) {
			tabChanged = true;
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
			case R.id.menu_main_update_status:
				startActivity(new Intent(this, UpdateStatusActivity.class).putExtra(CALLED_FROM_AZYOTTER, true));
				return true;
			case R.id.menu_main_tabs:
				startActivity(new Intent(this, TabsActivity.class).putExtra(CALLED_FROM_AZYOTTER, true));
				return true;
			case R.id.menu_main_preference:
				startActivity(new Intent(this, SettingActivity.class).putExtra(CALLED_FROM_AZYOTTER, true));
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
		public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) { }

		@Override
		public void onTabSelected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			if (mFragment.isAdded())
				arg1.show(mFragment);
			else
				arg1.add(R.id.fragment_content, mFragment, null);
			mFragment.actionBarTab = arg0;
		}

		@Override
		public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			arg1.hide(mFragment);
		}
    }
}