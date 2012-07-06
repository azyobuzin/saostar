package net.azyobuzi.azyotter.saostar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ActivityUtil {
	private ActivityUtil() { }
	
	public static final OnClickListener emptyDialogOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) { }
	};
	
	public static void showAlertDialog(Context ctx, int icon, int title, int message) {
		new AlertDialog.Builder(ctx)
			.setIcon(icon)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, emptyDialogOnClickListener)
			.show();
	}
	
	public static void showAlertDialog(Context ctx, int icon, int title, CharSequence message) {
		new AlertDialog.Builder(ctx)
			.setIcon(icon)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, emptyDialogOnClickListener)
			.show();
	}
}
