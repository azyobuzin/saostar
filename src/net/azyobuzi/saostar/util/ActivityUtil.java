package net.azyobuzi.saostar.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ActivityUtil
{
    private ActivityUtil()
    {}

    public static final OnClickListener emptyDialogOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(final DialogInterface arg0, final int arg1)
        {}
    };

    public static OnClickListener getFinishDialogOnClickListener(final Activity activity)
    {
        return new OnClickListener()
        {
            @Override
            public void onClick(final DialogInterface arg0, final int arg1)
            {
                activity.finish();
            }
        };
    }

    public static void showAlertDialog(final Activity activity, final int icon, final int title, final int message, final boolean afterFinish)
    {
        new AlertDialog.Builder(activity)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, afterFinish ? getFinishDialogOnClickListener(activity) : emptyDialogOnClickListener)
                .show();
    }

    public static void showAlertDialog(final Activity activity, final int icon, final int title, final CharSequence message, final boolean afterFinish)
    {
        new AlertDialog.Builder(activity)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, afterFinish ? getFinishDialogOnClickListener(activity) : emptyDialogOnClickListener)
                .show();
    }
}
