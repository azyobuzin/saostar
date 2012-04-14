package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import twitter4j.auth.AccessToken;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.system.Action;

public class Account {
	public Account(long id) {
		this.id = id;
		sp = ContextAccess.getSharedPreferences("twitter_" + getId(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
		screenName = sp.getString("screenName", "");
		oauthToken = sp.getString("oauthToken", "");
		oauthTokenSecret = sp.getString("oauthTokenSecret", "");
		useUserStream = sp.getBoolean("useUserStream", true);
	}

	private SharedPreferences sp;

	private long id;

	public long getId() {
		return id;
	}

	private String screenName;

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String value) {
		screenName = value;
		sp.edit().putString("screenName", value).apply();
	}

	private String oauthToken;

	public String getOAuthToken() {
		return oauthToken;
	}

	public void setOAuthToken(String value) {
		oauthToken = value;
		sp.edit().putString("oauthToken", value).apply();
	}

	private String oauthTokenSecret;

	public String getOAuthTokenSecret() {
		return oauthTokenSecret;
	}

	public void setOAuthTokenSecret(String value) {
		oauthTokenSecret = value;
		sp.edit().putString("oauthTokenSecret", value).apply();
	}

	private boolean useUserStream;

	public boolean getUseUserStream() {
		return useUserStream;
	}

	public void setUseUserStream(boolean value) {
		useUserStream = value;
		sp.edit().putBoolean("useUserStream", value).apply();

		for (Action handler : useUserStreamChangedHandler) {
			handler.invoke();
		}
	}

	public final ArrayList<Action> useUserStreamChangedHandler = new ArrayList<Action>();

	public AccessToken toAccessToken() {
		return new AccessToken(getOAuthToken(), getOAuthTokenSecret());
	}
}
