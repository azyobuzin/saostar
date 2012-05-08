package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class TabPreferenceFragment extends Fragment {
	public static final String TAB_INDEX = "net.azyobuzi.azyotter.saostar.activities.TabPreferenceFragment.TAB_INDEX";

	private TabHost tabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return tabHost = (TabHost)inflater.inflate(R.layout.edit_tab_page, null);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arg = getArguments();
        if (arg != null) {
        	setIndex(arg.getInt(TAB_INDEX, 0));
        }
	}

	public void setIndex(int i) {
		tabHost.addTab(tabHost.newTabSpec("general")
        	.setIndicator(getText(R.string.general_setting))
        	.setContent(new Intent(getActivity(), TabGeneralSettingActivity.class).putExtra(TAB_INDEX, i))
        );

        tabHost.addTab(tabHost.newTabSpec("filter")
        	.setIndicator(getText(R.string.filter_setting))
        	.setContent(new Intent(getActivity(), TabFilterSettingActivity.class).putExtra(TAB_INDEX, i))
        );

        tabHost.setCurrentTab(0);
	}
}
