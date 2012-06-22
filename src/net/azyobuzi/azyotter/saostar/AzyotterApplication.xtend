package net.azyobuzi.azyotter.saostar

import net.azyobuzi.azyotter.saostar.configuration.Setting
import net.azyobuzi.azyotter.saostar.services.TimelineReceiveService
import android.app.Application
import android.content.Intent
import android.preference.PreferenceManager

class AzyotterApplication extends Application {
	new() {
		super()
		ContextAccess.setContext(this)
	}

	override onCreate() {
		super.onCreate()

		System.setProperty("twitter4j.http.useSSL", "true")
		System.setProperty("twitter4j.oauth.consumerKey", "atiCJrEYcDTK06asy9riaA")
		System.setProperty("twitter4j.oauth.consumerSecret", "e1OSvum01Hgh3sg6xKIKJGRWIUOAqIei73rbuDOFbxY")
		System.setProperty("twitter4j.stream.user.repliesAll", "false")

		Setting.sp = PreferenceManager.getDefaultSharedPreferences(this)
		startService(new Intent(this, TimelineReceiveService.class))
	}
}
