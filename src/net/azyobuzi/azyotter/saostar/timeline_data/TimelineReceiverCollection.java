package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.HashMap;

import net.azyobuzi.azyotter.saostar.configuration.Account;

public class TimelineReceiverCollection {
	private static final HashMap<Account, TimelineReceiver> receivers = new HashMap<Account, TimelineReceiver>();

	public static void addAccount(Account a) {
		TimelineReceiver receiver = new TimelineReceiver(a);
		receivers.put(a, receiver);
		receiver.start();
	}

	public static void removeAccount(Account a) {
		receivers.remove(a).dispose();
	}
}
