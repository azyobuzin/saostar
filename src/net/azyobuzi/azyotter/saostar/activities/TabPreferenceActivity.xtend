package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Tabs
import android.app.ActionBar
import android.app.ActionBar.TabListener
import android.app.Activity
import android.app.Fragment
import android.app.FragmentTransaction
import android.app.ActionBar.Tab
import android.os.Bundle
import android.view.MenuItem

class TabPreferenceActivity extends Activity {
	static val TAB_INDEX = "net.azyobuzi.azyotter.saostar.activities.TabPreferenceActivity.TAB_INDEX"

	override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

        val index = getIntent().getIntExtra(TAB_INDEX, 0)

        val actionBar = getActionBar()
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setSubtitle(Tabs.get(index).getName())

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS)

        actionBar.addTab(
        	actionBar.newTab()
        		.setText(R.string.general_setting)
        		.setTabListener(new SettingTabListener(TabGeneralSettingFragment.createInstance(index)))
        )

        actionBar.addTab(
        	actionBar.newTab()
        		.setText(R.string.filter_setting)
        		.setTabListener(new SettingTabListener(TabFilterSettingFragment.createInstance(index)))
        )
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

	private class SettingTabListener implements TabListener {
		new(Fragment fragment) {
			mFragment = fragment
		}

		private Fragment mFragment

		override onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment)
		}

		override onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, mFragment)
		}

		override onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
