package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Tab
import net.azyobuzi.azyotter.saostar.configuration.Tabs
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class TabsActivity extends Activity {
	override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabs_page)
        getActionBar().setDisplayHomeAsUpEnabled(true)
	}

	override onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tabs_menu, menu)
		true
	}
	
	override onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish()
				true
			case R.id.menu_tabs_add:
				Tabs.add(new Tab())
				((TabsFragment)getFragmentManager().findFragmentById(R.id.fragment_tabs_list))
					.showDetails(Tabs.getTabsCount() - 1)
				true
			default:
				super.onOptionsItemSelected(item)
		}
	}
}
