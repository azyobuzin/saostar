package net.azyobuzi.saostar;

import com.google.common.base.Strings;

import net.azyobuzi.saostar.model.Authorization;
import net.azyobuzi.saostar.model.configuration.Account;
import net.azyobuzi.saostar.util.ActivityUtil;
import twitter4j.TwitterException;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class LoginActivity extends Activity
{
    private boolean canceled = false;
    private Authorization auth = new Authorization();

    private void showError()
    {
        App.instance.h.post(new Runnable()
        {
            @Override
            public void run()
            {
                ActivityUtil.showAlertDialog(
                        LoginActivity.this,
                        android.R.drawable.ic_dialog_alert,
                        android.R.string.dialog_alert_title,
                        R.string.couldnt_get_token,
                        true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final Uri uri;

                try
                {
                    uri = auth.beginAuthorization();
                }
                catch (TwitterException e)
                {
                    e.printStackTrace();
                    showError();
                    return;
                }

                if (canceled) return;

                App.instance.h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                canceled = true;
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if (canceled) return;

        if (intent.getData() != null)
        {
            final String verifier = intent.getData().getQueryParameter("oauth_verifier");

            if (!Strings.isNullOrEmpty(verifier))
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final Account a;

                        try
                        {
                            a = auth.endAuthorization(verifier);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            showError();
                            return;
                        }

                        App.instance.h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                startActivity(new Intent(LoginActivity.this, AccountActivity.class)
                                        .putExtra(AccountActivity.ACCOUNT_ID, a.getId()));
                                finish();
                            }
                        });
                    }
                }).start();
            }
        }
    }
}
