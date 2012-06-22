package net.azyobuzi.azyotter.saostar.activities

import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.configuration.Tab
import net.azyobuzi.azyotter.saostar.configuration.Tabs
import android.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class TabGeneralSettingFragment extends Fragment {
	def static createInstance(int index) {
		val instance = new TabGeneralSettingFragment()
		instance.tab = Tabs.get(index)
		return instance
	}

	private Tab tab

	override onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_general_setting_page, null)
	}

	private EditText txtTabName
	private Button btnRemove

	override onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState)

		txtTabName = (EditText)getView().findViewById(R.id.txt_tab_general_setting_tab_name)
		btnRemove = (Button)getView().findViewById(R.id.btn_tab_general_setting_remove)

		txtTabName.setText(tab.getName())

		txtTabName.addTextChangedListener(new TextWatcher() {
			override onTextChanged(CharSequence s, int start, int before, int count) {
				tab.setName(s.toString())
			}

			override beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			override afterTextChanged(Editable s) {
			}
		})
		btnRemove.setOnClickListener(new OnClickListener() {
			override onClick(View v) {
				Tabs.remove(tab)
				getActivity().finish()
			}
		})
	}
}
