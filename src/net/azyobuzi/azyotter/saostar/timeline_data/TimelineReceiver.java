package net.azyobuzi.azyotter.saostar.timeline_data;

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

public class TimelineReceiver {
	public TimelineReceiver(Account a) {
		account = a;

		//twitter = Twitter4JFactories.twitterFactory.getInstance(a.toAccessToken());
		stream = Twitter4JFactories.twitterStreamFactory.getInstance(a.toAccessToken());

		stream.addListener(new UserStreamListener() {
			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				TimelineItemCollection.removeTweet(statusDeletionNotice.getStatusId());
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

	public void dispose() {
		account.useUserStreamChangedHandler.remove(useUserStreamChangedHandler);
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
