package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.MenuItem;

public class TabPreferenceActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        int index = getIntent().getIntExtra(TabPreferenceFragment.TAB_INDEX, 0);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(Tabs.get(index).getName());

        /*((TabPreferenceFragment)getFragmentManager().findFragmentById(R.id.fragment_tab_preference))
        	.setIndex(index);*/

        /*tabHost.addTab(tabHost.newTabSpec("general")
        	.setIndicator(getText(R.string.general_setting))
        	.setContent(new Intent(this, TabGeneralSettingActivity.class).putExtra(TabPreferenceFragment.TAB_INDEX, index))
        );

        tabHost.addTab(tabHost.newTabSpec("filter")
        	.setIndicator(getText(R.string.filter_setting))
        	.setContent(new Intent(this, TabFilterSettingActivity.class).putExtra(TabPreferenceFragment.TAB_INDEX, index))
        );

        tabHost.setCurrentTab(0);*/

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(
        	actionBar.newTab()
        		.setText(R.string.general_setting)
        		.setTabListener(new SettingTabListener(TabGeneralSettingFragment.createInstance(index)))
        );

        actionBar.addTab(
        	actionBar.newTab()
        		.setText(R.string.filter_setting)
        		.setTabListener(new SettingTabListener(TabFilterSettingFragment.createInstance(index)))
        );
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class SettingTabListener implements TabListener {
		public SettingTabListener(Fragment fragment) {
			mFragment = fragment;
		}

		private Fragment mFragment;

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
