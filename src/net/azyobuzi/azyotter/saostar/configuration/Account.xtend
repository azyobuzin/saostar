package net.azyobuzi.azyotter.saostar.configuration

import java.util.ArrayList

import android.content.Context
import android.content.SharedPreferences

import twitter4j.auth.AccessToken

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.system.Action

class Account {
	new(long id) {
		this.id = id
		sp = ContextAccess.getSharedPreferences("twitter_" + getId(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS)
		screenName = sp.getString("screenName", "")
		oauthToken = sp.getString("oauthToken", "")
		oauthTokenSecret = sp.getString("oauthTokenSecret", "")
		useUserStream = sp.getBoolean("useUserStream", true)
	}

	private SharedPreferences sp

	private long id

	def getId() {
		id
	}

	private String screenName

	def getScreenName() {
		screenName
	}

	def setScreenName(String value) {
		screenName = value
		sp.edit().putString("screenName", value).apply()
	}

	private String oauthToken

	def getOAuthToken() {
		oauthToken
	}

	def setOAuthToken(String value) {
		oauthToken = value
		sp.edit().putString("oauthToken", value).apply()
	}

	private String oauthTokenSecret

	def getOAuthTokenSecret() {
		oauthTokenSecret
	}

	def setOAuthTokenSecret(String value) {
		oauthTokenSecret = value
		sp.edit().putString("oauthTokenSecret", value).apply()
	}

	private boolean useUserStream

	def getUseUserStream() {
		useUserStream
	}

	def setUseUserStream(boolean value) {
		useUserStream = value
		sp.edit().putBoolean("useUserStream", value).apply()

		for (Action handler : useUserStreamChangedHandler) {
			handler.invoke()
		}
	}

	val ArrayList<Action> useUserStreamChangedHandler = new ArrayList<Action>()

	def toAccessToken() {
		new AccessToken(getOAuthToken(), getOAuthTokenSecret())
	}
}
