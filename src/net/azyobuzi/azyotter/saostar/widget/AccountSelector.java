package net.azyobuzi.azyotter.saostar.widget;

import java.util.ArrayList;

import net.azyobuzi.azyotter.saostar.ActivityUtil;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.system.Action;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;

public class AccountSelector extends CustomizedUrlImageView {
	public AccountSelector(Context context) {
		super(context);
		init(context);
	}

	public AccountSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AccountSelector(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(final Context context) {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final ArrayList<Account> accounts = Accounts.getAllAccounts().toArrayList();
				CharSequence[] screenNames = new CharSequence[accounts.size()];
				for (int i = 0; i < accounts.size(); i++) {
					screenNames[i] = accounts.get(i).getScreenName();
				}

				new AlertDialog.Builder(context)
					.setTitle(R.string.select_account_to_use)
					.setItems(screenNames, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Accounts.setSelectedAccount(accounts.get(which));
						}
					})
					.setPositiveButton(android.R.string.cancel, ActivityUtil.emptyDialogOnClickListener)
					.show();
			}
		});

		Accounts.selectedAccountChangedHandler.add(selectedAccountChangedHandler);
		selectedAccountChangedHandler.invoke();
	}

	private final Action selectedAccountChangedHandler = new Action() {
		@Override
		public void invoke() {
			Account a = Accounts.getSelectedAccount();
			if (a != null)
				setImageUrl("https://api.twitter.com/1/users/profile_image/" + Accounts.getSelectedAccount().getScreenName() + ".json");
		}
	};

	public void dispose() {
		Accounts.selectedAccountChangedHandler.remove(selectedAccountChangedHandler);
	}
}
