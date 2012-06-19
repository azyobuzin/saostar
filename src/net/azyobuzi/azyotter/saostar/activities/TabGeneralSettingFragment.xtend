package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TabGeneralSettingFragment extends Fragment {
	public static TabGeneralSettingFragment createInstance(int index) {
		TabGeneralSettingFragment instance = new TabGeneralSettingFragment();
		instance.tab = Tabs.get(index);
		return instance;
	}

	private Tab tab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_general_setting_page, null);
	}

	private EditText txtTabName;
	private Button btnRemove;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		txtTabName = (EditText)getView().findViewById(R.id.txt_tab_general_setting_tab_name);
		btnRemove = (Button)getView().findViewById(R.id.btn_tab_general_setting_remove);

		txtTabName.setText(tab.getName());

		txtTabName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				tab.setName(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		btnRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tabs.remove(tab);
				getActivity().finish();
			}
		});
	}
}
