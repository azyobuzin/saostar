package net.azyobuzi.azyotter.saostar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ActivityUtil {
	private ActivityUtil() { }
	
	public static final OnClickListener emptyDialogOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) { }
	};
	
	public static OnClickListener getFinishDialogOnClickListener(final Activity activity) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.finish();
			}
		};
	}
	
	public static void showAlertDialog(Activity activity, int icon, int title, int message, boolean afterFinish) {
		new AlertDialog.Builder(activity)
			.setIcon(icon)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, afterFinish ? getFinishDialogOnClickListener(activity) : emptyDialogOnClickListener)
			.show();
	}
	
	public static void showAlertDialog(Activity activity, int icon, int title, CharSequence message, boolean afterFinish) {
		new AlertDialog.Builder(activity)
			.setIcon(icon)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, afterFinish ? getFinishDialogOnClickListener(activity) : emptyDialogOnClickListener)
			.show();
	}
}
