package net.azyobuzi.azyotter.saostar.configuration;

import java.util.HashSet;
import java.util.Set;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;

import android.content.SharedPreferences;

public class Setting {
	public static SharedPreferences sp = ContextAccess.getDefaultSharedPreferences();

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

	public static final Set<String> defaultShownUploadServices =
		new HashSet<String>(Enumerable.from(ContextAccess.getResources().getStringArray(R.array.upload_services_value)).toArrayList());

	public static Set<String> getShownUploadServices() {
		return sp.getStringSet("shownUploadServices", defaultShownUploadServices);
	}

	public static int getTheme() {
		return Integer.valueOf(sp.getString("theme", String.valueOf(android.R.style.Theme_DeviceDefault)));
	}
}
