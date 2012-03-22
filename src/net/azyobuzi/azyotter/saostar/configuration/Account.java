package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import twitter4j.auth.AccessToken;

import net.azyobuzi.azyotter.saostar.system.Action1;

public class Account {
	public long id;
	public String screenName;
	public String oauthToken;
	public String oauthTokenSecret;

	private boolean useUserStream;

	public boolean getUseUserStream() {
		return useUserStream;
	}

	public void setUseUserStream(boolean value) {
		useUserStream = value;

		for (Action1<Account> handler : useUserStreamChangedHandler) {
			handler.invoke(this);
		}
	}

	public final ArrayList<Action1<Account>> useUserStreamChangedHandler = new ArrayList<Action1<Account>>();
	
	public AccessToken toAccessToken() {
		return new AccessToken(oauthToken, oauthTokenSecret);
	}
}
