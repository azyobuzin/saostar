package net.azyobuzi.azyotter.saostar.configuration;

import java.util.HashSet;
import java.util.Set;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;

import android.content.SharedPreferences;

public class Setting {
	public static SharedPreferences sp = ContextAccess.getDefaultSharedPreferences();
	
	public static Command getTapCommand() {
		return Command.valueOf(sp.getString("tapCommand", Command.SHOW_DETAIL.toString()));
	}
	
	public static Command getLongPressCommand() {
		return Command.valueOf(sp.getString("longPressCommand", Command.SELECT.toString()));
	}

	public static Command getFlickToRightCommand() {
		return Command.valueOf(sp.getString("flickToRightCommand", Command.FAVORITE_SHOW_DIALOG.toString()));
	}

	public static Command getFlickToLeftCommand() {
		return Command.valueOf(sp.getString("flickToLeftCommand", Command.RETWEET_SHOW_DIALOG.toString()));
	}
	
	public static Command getDoubleTapCommand() {
		return Command.valueOf(sp.getString("doubleTapCommand", Command.REPLY.toString()));
	}

	public static final Set<String> defaultShownUploadServices =
		new HashSet<String>(Enumerable.from(ContextAccess.getResources().getStringArray(R.array.upload_services_value)).toArrayList());

	public static Set<String> getShownUploadServices() {
		return sp.getStringSet("shownUploadServices", defaultShownUploadServices);
	}

	public static int getTheme() {
		return Integer.valueOf(sp.getString("theme", String.valueOf(android.R.style.Theme_DeviceDefault)));
	}
	
	public static boolean getCloseTweetDetailViewAfterOperation() {
		return sp.getBoolean("closeTweetDetailViewAfterOperation", true);
	}
}
