package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import twitter4j.auth.AccessToken;

import net.azyobuzi.azyotter.saostar.system.Action;

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

		for (Action handler : useUserStreamChangedHandler) {
			handler.invoke();
		}
	}

	public final ArrayList<Action> useUserStreamChangedHandler = new ArrayList<Action>();
	
	public AccessToken toAccessToken() {
		return new AccessToken(oauthToken, oauthTokenSecret);
	}
}
