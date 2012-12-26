package net.azyobuzi.saostar.model.configuration;

import java.util.ArrayList;

import net.azyobuzi.saostar.App;
import net.azyobuzi.saostar.util.Action;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;

public class Account
{
    public Account(final long id)
    {
        this.id = id;
        sp = App.instance.getSharedPreferences("twitter_" + getId(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        screenName = sp.getString("screenName", "");
        oauthToken = sp.getString("oauthToken", "");
        oauthTokenSecret = sp.getString("oauthTokenSecret", "");
        useUserStream = sp.getBoolean("useUserStream", true);
    }

    private final SharedPreferences sp;

    private final long id;

    public long getId()
    {
        return id;
    }

    private String screenName;

    public String getScreenName()
    {
        return screenName;
    }

    public void setScreenName(final String value)
    {
        screenName = value;
        sp.edit().putString("screenName", value).apply();
    }

    private String oauthToken;

    public String getOAuthToken()
    {
        return oauthToken;
    }

    public void setOAuthToken(final String value)
    {
        oauthToken = value;
        sp.edit().putString("oauthToken", value).apply();
    }

    private String oauthTokenSecret;

    public String getOAuthTokenSecret()
    {
        return oauthTokenSecret;
    }

    public void setOAuthTokenSecret(final String value)
    {
        oauthTokenSecret = value;
        sp.edit().putString("oauthTokenSecret", value).apply();
    }

    private boolean useUserStream;

    public boolean getUseUserStream()
    {
        return useUserStream;
    }

    public void setUseUserStream(final boolean value)
    {
        useUserStream = value;
        sp.edit().putBoolean("useUserStream", value).apply();

        for (final Action handler : useUserStreamChangedHandler)
        {
            handler.invoke();
        }
    }

    public final ArrayList<Action> useUserStreamChangedHandler = new ArrayList<Action>();

    public AccessToken toAccessToken()
    {
        return new AccessToken(getOAuthToken(), getOAuthTokenSecret());
    }
}
