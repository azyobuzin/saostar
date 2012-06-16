package net.azyobuzi.azyotter.saostar.services;

import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.system.Action;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimelineReceiveService extends Service {
	public TimelineReceiveService() {
		instance = this;
	}

	private static TimelineReceiveService instance;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for (TimelineReceiver receiver : receivers.values()) {
			receiver.start();
		}
		for (Account a : addQueue) {
			internalAdd(a);
		}
		addQueue.clear();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		for (TimelineReceiver receiver : receivers.values()) {
			receiver.stop();
		}
	}

	public final HashMap<Account, TimelineReceiver> receivers = new HashMap<Account, TimelineReceiver>();

	private static ArrayList<Account> addQueue = new ArrayList<Account>();

	public static void addAccount(Account a) {
		if (instance != null) {
			instance.internalAdd(a);
		} else {
			addQueue.add(a);
		}
	}

	private void internalAdd(Account a) {
		TimelineReceiver receiver = new TimelineReceiver(a);
		receivers.put(a, receiver);
		receiver.start();
	}

	public static void removeAccount(Account a) {
		if (instance != null) {
			instance.internalRemove(a);
		} else {
			addQueue.remove(a);
		}
	}

	private void internalRemove(Account a) {
		receivers.remove(a).dispose();
	}

	private class TimelineReceiver {
		public TimelineReceiver(Account a) {
			account = a;

			//twitter = Twitter4JFactories.twitterFactory.getInstance(a.toAccessToken());
			stream = Twitter4JFactories.twitterStreamFactory.getInstance(a.toAccessToken());

			stream.addListener(new UserStreamListener() {
				@Override
				public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
					//TimelineItemCollection.removeTweet(statusDeletionNotice.getStatusId());
				}

				@Override
				public void onScrubGeo(long userId, long upToStatusId) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onStatus(Status status) {
					TimelineItemCollection.addOrMerge(status, true);
				}

				@Override
				public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onException(Exception ex) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onBlock(User source, User blockedUser) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onDeletionNotice(long directMessageId, long userId) {
					TimelineItemCollection.removeDirectMessage(directMessageId);
				}

				@Override
				public void onDirectMessage(DirectMessage directMessage) {
					TimelineItemCollection.addOrMerge(directMessage);
				}

				@Override
				public void onFavorite(User source, User target, Status favoritedStatus) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onFollow(User source, User followedUser) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onFriendList(long[] friendIds) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onRetweet(User source, User target, Status retweetedStatus) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUnblock(User source, User unblockedUser) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListCreation(User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListDeletion(User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserListUpdate(User listOwner, UserList list) {
					// TODO 自動生成されたメソッド・スタブ

				}

				@Override
				public void onUserProfileUpdate(User updatedUser) {
					// TODO 自動生成されたメソッド・スタブ

				}
			});
		}

		private Account account;

		//private Twitter twitter;
		private TwitterStream stream;

		public void start() {
			account.useUserStreamChangedHandler.add(useUserStreamChangedHandler);
			useUserStreamChangedHandler.invoke();
		}

		public void stop() {
			account.useUserStreamChangedHandler.remove(useUserStreamChangedHandler);
			stream.cleanUp();
		}
		
		public void dispose() {
			stop();
			stream.shutdown();
		}

		private final Action useUserStreamChangedHandler = new Action() {
			@Override
			public void invoke() {
				stream.cleanUp();

				if (account.getUseUserStream()) {
					stream.user();
				}
			}
		};
	}
}
