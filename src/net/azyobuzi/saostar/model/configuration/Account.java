package net.azyobuzi.saostar.model.configuration;

import net.azyobuzi.saostar.App;
import net.azyobuzi.saostar.util.EventArgs;
import net.azyobuzi.saostar.util.Notificator;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;

public class Account // TODO:アカウント削除時に SharedPreference をクリア
{
    public Account(final long id)
    {
        this.id = id;
        sp = App.instance.getSharedPreferences("twitter_" + getId(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        screenName = sp.getString("screenName", "");
        oauthToken = sp.getString("oauthToken", "");
        oauthTokenSecret = sp.getString("oauthTokenSecret", "");
        useUserStreams = sp.getBoolean("useUserStreams", true);

        useUserStreamsChangedEvent.scheduler = App.instance.h;
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

    private boolean useUserStreams;
    public final Notificator<EventArgs> useUserStreamsChangedEvent = new Notificator<EventArgs>();

    public boolean getUseUserStreams()
    {
        return useUserStreams;
    }

    public void setUseUserStreams(final boolean value)
    {
        useUserStreams = value;
        sp.edit().putBoolean("useUserStreams", value).apply();
        useUserStreamsChangedEvent.raise(this, EventArgs.empty);
    }

    public AccessToken toAccessToken()
    {
        return new AccessToken(getOAuthToken(), getOAuthTokenSecret());
    }
}
