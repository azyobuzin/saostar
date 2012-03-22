package net.azyobuzi.azyotter.saostar;

import android.app.Application;

public class AzyotterApplication extends Application {
	public AzyotterApplication() {
		super();
		ContextAccess.setContext(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		System.setProperty("twitter4j.http.useSSL", "true");
		System.setProperty("twitter4j.oauth.consumerKey", "atiCJrEYcDTK06asy9riaA");
		System.setProperty("twitter4j.oauth.consumerSecret", "e1OSvum01Hgh3sg6xKIKJGRWIUOAqIei73rbuDOFbxY");
	}
}
