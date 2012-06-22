package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.StringUtil
import net.azyobuzi.azyotter.saostar.configuration.Accounts
import net.azyobuzi.azyotter.saostar.configuration.Tab
import net.azyobuzi.azyotter.saostar.configuration.Tabs
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService
import net.azyobuzi.azyotter.saostar.system.Action1
import net.azyobuzi.azyotter.saostar.system.Action2
import net.azyobuzi.azyotter.saostar.system.Action3
import net.azyobuzi.azyotter.saostar.widget.AccountSelector
import android.app.ActionBar
import android.app.ActionBar.TabListener
import android.app.Activity
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText

class AzyotterActivity extends Activity {
    /** Called when the activity is first created. */

	static val CALLED_FROM_AZYOTTER = "net.azyobuzi.azyotter.saostar.activities.AzyotterActivity.CALLED_FROM_AZYOTTER"

    private boolean tabChanged = false

	override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if (Accounts.getAccountsCount() == 0) {
        	startActivity(new Intent(this, AccountsActivity.class).putExtra("firstRun", true).putExtra(CALLED_FROM_AZYOTTER, true))
        	finish()
        	return
        }

        val actionBar = getActionBar()
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS)
        createTabs()
        Tabs.addedHandler.add(tabChangedHandler)
        Tabs.removedHandler.add(tabChangedHandler)
        Tabs.movedHandler.add(movedTabHandler)

        findViewById(R.id.btn_main_update_status).setOnClickListener(new OnClickListener() {
			override onClick(View arg0) {
				val tweetBox = (EditText)findViewById(R.id.txt_main_tweet)
				val text = tweetBox.getText().toString()
				if (StringUtil.isNullOrEmpty(text)) return

				startService(new Intent(AzyotterActivity.this, UpdateStatusService.class)
					.putExtra(UpdateStatusService.TEXT, text))
				tweetBox.setText("")
			}
        });
    }

    override onDestroy() {
    	((AccountSelector)findViewById(R.id.as_main)).dispose()
    	Tabs.addedHandler.remove(tabChangedHandler)
        Tabs.removedHandler.remove(tabChangedHandler)
        Tabs.movedHandler.remove(movedTabHandler)
    	super.onDestroy()
    }

    override onResume() {
    	super.onResume()

    	if (tabChanged) {
    		getActionBar().removeAllTabs()
    		createTabs()
    	}
    }

    def private createTabs() {
    	val actionBar = getActionBar()
    	Tabs.getAllTabs().forEach(new Action2<Tab, Integer>() {
			override invoke(Tab arg0, Integer arg1) {
				actionBar.addTab(
					actionBar.newTab()
						.setText(arg0.getName())
						.setTabListener(new TimelineTabListener(arg0))
						.setTag(arg0)
				)
			}
        })
    }

	private Action1<Tab> tabChangedHandler = new Action1<Tab>() {
		override invoke(Tab arg) {
			tabChanged = true
		}
	}

	private Action3<Tab, Integer, Integer> movedTabHandler = new Action3<Tab, Integer, Integer>() {
		override invoke(Tab arg0, Integer arg1, Integer arg2) {
			tabChanged = true
		}
	}

	override onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu)
		true
	}

	override onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_main_update_status:
				startActivity(new Intent(this, UpdateStatusActivity.class).putExtra(CALLED_FROM_AZYOTTER, true))
				true
			case R.id.menu_main_tabs:
				startActivity(new Intent(this, TabsActivity.class).putExtra(CALLED_FROM_AZYOTTER, true))
				true
			case R.id.menu_main_accounts:
				startActivity(new Intent(this, AccountsActivity.class).putExtra(CALLED_FROM_AZYOTTER, true))
				true
			default:
				super.onOptionsItemSelected(item)
		}
	}

    private class TimelineTabListener implements TabListener {
    	new(Tab tab) {
    		mFragment = new TimelineTabFragment(tab)
    	}

    	private TimelineTabFragment mFragment

		override onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
		}

		override onTabSelected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			arg1.add(R.id.fragment_content, mFragment, null)
			mFragment.setActionBarTab(arg0)
		}

		override onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
			arg1.remove(mFragment)
		}
    }
}