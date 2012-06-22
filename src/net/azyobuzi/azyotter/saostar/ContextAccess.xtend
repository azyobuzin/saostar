package net.azyobuzi.azyotter.saostar

import android.content.Context

class ContextAccess {
	private static Context context = null
	def static setContext(Context ctx) {
		context = ctx
	}

	def static getText(int resId) {
		context.getText(resId)
	}

	def static getString(int resId) {
		context.getString(resId)
	}

	def static openFileInput(String fileName) {
		context.openFileInput(fileName)
	}

	def static openFileOutput(String fileName, int mode) {
		context.openFileOutput(fileName, mode)
	}

	def static getDefaultSharedPreferences() {
		PreferenceManager.getDefaultSharedPreferences(context)
	}

	def static getSharedPreferences(String name, int mode) {
		context.getSharedPreferences(name, mode)
	}
}
