package net.azyobuzi.saostar;

import net.azyobuzi.saostar.model.configuration.Accounts;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        if (Accounts.getList().isEmpty())
        {
            startActivity(new Intent(this, AccountsActivity.class));
            // 必要なし？
            // finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

}
