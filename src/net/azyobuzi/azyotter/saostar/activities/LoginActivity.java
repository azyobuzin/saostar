package net.azyobuzi.azyotter.saostar.activities;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private Handler h = new Handler();
	private RequestToken reqToken;

	private TextView status;

	private boolean canceled = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login_waiting_page);

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(R.string.login);
        actionBar.setDisplayHomeAsUpEnabled(true);

        status = (TextView)findViewById(R.id.tv_login_waiting_status);
        
        setProgressBarIndeterminateVisibility(true);

        new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Twitter tw = Twitter4JFactories.twitterFactory.getInstance();
					reqToken = tw.getOAuthRequestToken();
				} catch (Exception ex) {
					h.post(new Runnable() {
						@Override
						public void run() {
							status.setText(R.string.get_token_failed);
							setProgressBarIndeterminateVisibility(false);
						}
					});
					return;
				}

				if (canceled) return;

				h.post(new Runnable() {
					@Override
					public void run() {
						startActivity(new Intent(Intent.ACTION_VIEW)
							.setData(Uri.parse(reqToken.getAuthorizationURL())));
						status.setText(R.string.waiting_for_you_to_authorize);
						setProgressBarIndeterminateVisibility(false);
					}
				});
			}
        }).start();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_waiting_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			case R.id.menu_login_waiting_cancel:
				canceled = true;
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (canceled) return;

		if (intent.getData() != null) {
			String verifier = intent.getData().getQueryParameter("oauth_verifier");

			if (!StringUtil.isNullOrEmpty(verifier)) {
				status.setText(R.string.getting_token);
				setProgressBarIndeterminateVisibility(true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Twitter tw = Twitter4JFactories.twitterFactory.getInstance();
							AccessToken token = tw.getOAuthAccessToken(reqToken);

							if (canceled) return;

							if (Accounts.get(token.getUserId()) == null) {
								final Account a = new Account();
								a.id = token.getUserId();
								a.screenName = token.getScreenName();
								a.oauthToken = token.getToken();
								a.oauthTokenSecret = token.getTokenSecret();
								Accounts.add(a);
								h.post(new Runnable() {
									@Override
									public void run() {
										startActivity(new Intent(LoginActivity.this, EditAccountActivity.class).putExtra("id", a.id));
										finish();
									}
								});
							}
						} catch (Exception ex) {
							h.post(new Runnable() {
								@Override
								public void run() {
									status.setText(R.string.get_token_failed);
									setProgressBarIndeterminateVisibility(false);
								}
							});
						}
					}
				}).start();
			}
		}
	}
}
