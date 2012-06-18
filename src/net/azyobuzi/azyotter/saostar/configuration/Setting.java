package net.azyobuzi.azyotter.saostar.configuration;

import android.content.SharedPreferences;

public class Setting {
	public static SharedPreferences sp;

	public static final int COMMAND_FAVORITE = 0;
	public static final int COMMAND_RETWEET = 1;

	public static int getFlickToRightCommand() {
		return sp.getInt("flickToRightCommand", COMMAND_FAVORITE);
	}

	public static void setFlickToRightCommand(int value) {
		sp.edit().putInt("flickToRightCommand", value).apply();
	}

	public static int getFlickToLeftCommand() {
		return sp.getInt("flickToLeftCommand", COMMAND_RETWEET);
	}

	public static void setFlickToLeftCommand(int value) {
		sp.edit().putInt("flickToLeftCommand", value).apply();
	}
}
