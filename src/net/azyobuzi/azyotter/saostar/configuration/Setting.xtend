package net.azyobuzi.azyotter.saostar.configuration

import android.content.SharedPreferences

class Setting {
	public static SharedPreferences sp

	public static val COMMAND_FAVORITE = 0
	public static val COMMAND_RETWEET = 1

	def static getFlickToRightCommand() {
		sp.getInt("flickToRightCommand", COMMAND_FAVORITE)
	}

	def static setFlickToRightCommand(int value) {
		sp.edit().putInt("flickToRightCommand", value).apply()
	}

	def static getFlickToLeftCommand() {
		sp.getInt("flickToLeftCommand", COMMAND_RETWEET)
	}

	def static setFlickToLeftCommand(int value) {
		sp.edit().putInt("flickToLeftCommand", value).apply()
	}
}
