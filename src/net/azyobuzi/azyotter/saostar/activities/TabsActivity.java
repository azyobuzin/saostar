package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TabsActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTheme(Setting.getTheme());
        setContentView(R.layout.tabs_page);
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tabs_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_tabs_add:
				Tabs.add(new Tab());
				((TabsFragment)getFragmentManager().findFragmentById(R.id.fragment_tabs_list))
					.showDetails(Tabs.getTabsCount() - 1);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
