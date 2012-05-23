package net.azyobuzi.azyotter.saostar.activities;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.configuration.Tab;
import net.azyobuzi.azyotter.saostar.configuration.Tabs;
import net.azyobuzi.azyotter.saostar.d_aqa.Invokable;
import net.azyobuzi.azyotter.saostar.d_aqa.Reader;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TabFilterSettingFragment extends Fragment {
	public static TabFilterSettingFragment createInstance(int index) {
		TabFilterSettingFragment instance = new TabFilterSettingFragment();
		instance.tab = Tabs.get(index);
		return instance;
	}

	private Tab tab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_filter_setting_page, null);
	}

	private TextView tvQueryState;
	private Button btnAboutQuery;
	private EditText txtQuery;

	private static final int RED = 0xffff0000;
	private static final int GREEN = 0xff00ff00;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tvQueryState = (TextView)getView().findViewById(R.id.tv_tab_filter_setting_query_state);
		btnAboutQuery = (Button)getView().findViewById(R.id.btn_tab_filter_setting_about_filter);
		txtQuery = (EditText)getView().findViewById(R.id.txt_tab_filter_setting_query);

		txtQuery.setText(tab.getFilter());

		txtQuery.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String newText = txtQuery.getText().toString();

				if (StringUtil.isNullOrEmpty(newText)) {
					tvQueryState.setText(R.string.query_is_nothing);
					tvQueryState.setTextColor(RED);
					return;
				}

				try {
					Invokable expr = Reader.read(newText);
					if (expr.getResultType() != Invokable.TYPE_BOOLEAN)
						throw new IllegalArgumentException(getString(R.string.return_type_is_not_boolean));
					expr.invoke(TimelineItem.getDummyTweet());

					tab.setFilter(newText);
					tvQueryState.setText(R.string.query_successfully_parsed);
					tvQueryState.setTextColor(GREEN);
				} catch (Exception ex) {
					ex.printStackTrace();
					String message = ex.getMessage();
					if (StringUtil.isNullOrEmpty(message)) message = ex.getClass().getSimpleName();
					tvQueryState.setText(getText(R.string.parsing_query_failed) + message);
					tvQueryState.setTextColor(RED);
				}
			}
		});
	}
}
