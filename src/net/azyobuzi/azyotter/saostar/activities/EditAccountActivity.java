package net.azyobuzi.azyotter.saostar.activities;

import jp.sharakova.android.urlimageview.UrlImageView;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class EditAccountActivity extends Activity {
	private Account account;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_page);

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(R.string.edit_account);
        actionBar.setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra("id", 0);
        account = Accounts.get(id);

        ((UrlImageView)findViewById(R.id.iv_edit_account_profile_image)).setImageUrl(
        	"https://api.twitter.com/1/users/profile_image/" + account.getScreenName() + ".json"
        );

        ((TextView)findViewById(R.id.tv_edit_account_screen_name)).setText(account.getScreenName());
        ((TextView)findViewById(R.id.tv_edit_account_id)).setText(String.valueOf(account.getId()));

        CheckBox useUserStream = (CheckBox)findViewById(R.id.check_edit_account_use_user_stream);
        useUserStream.setChecked(account.getUseUserStream());
        useUserStream.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				account.setUseUserStream(arg1);
				Accounts.save();
			}
        });
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_account_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_edit_account_edit_profile:
				Toast.makeText(this, "無茶言うな", Toast.LENGTH_SHORT).show(); //TODO
				return true;
			case R.id.menu_edit_account_remove_account:
				Accounts.remove(account);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
