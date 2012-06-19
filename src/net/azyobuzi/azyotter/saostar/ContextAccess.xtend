package net.azyobuzi.azyotter.saostar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ContextAccess {
	private static Context context;
	public static void setContext(Context ctx) {
		context = ctx;
	}

	public static CharSequence getText(int resId) {
		return context.getText(resId);
	}

	public static String getString(int resId) {
		return context.getString(resId);
	}

	public static FileInputStream openFileInput(String fileName) throws FileNotFoundException {
		return context.openFileInput(fileName);
	}

	public static FileOutputStream openFileOutput(String fileName, int mode) throws FileNotFoundException {
		return context.openFileOutput(fileName, mode);
	}

	public static SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static SharedPreferences getSharedPreferences(String name, int mode) {
		return context.getSharedPreferences(name, mode);
	}
}
