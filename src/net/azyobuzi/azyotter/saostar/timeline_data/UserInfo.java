package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.ArrayList;
import java.util.Date;

import net.azyobuzi.azyotter.saostar.configuration.Account;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Action2;
import net.azyobuzi.azyotter.saostar.system.Func2;

import android.net.Uri;

public class UserInfo {
	public long id;
	public String screenName;
	public String name;
	public Date createdAt;
	public String description;
	public String location;
	public Uri url;
	public int friendsCount;
	public int followersCount;
	public int statusesCount;
	public int favouritesCount;
	public int listedCount;
	public boolean isProtected;
	public boolean isVerified;
	public String profileImageUrl;

	public final ArrayList<Action1<UserInfo>> mergedHandler = new ArrayList<Action1<UserInfo>>();

	public static UserInfo create(twitter4j.User source) {
		UserInfo re = new UserInfo();
		re.id = source.getId();
		re.screenName = source.getScreenName();
		re.name = source.getName();
		re.createdAt = source.getCreatedAt();
		re.description = source.getDescription();
		re.location = source.getLocation();
		re.url = Uri.parse(source.getURL().toString());
		re.friendsCount = source.getFriendsCount();
		re.followersCount = source.getFollowersCount();
		re.statusesCount = source.getStatusesCount();
		re.favouritesCount = source.getFavouritesCount();
		re.listedCount = source.getListedCount();
		re.isProtected = source.isProtected();
		re.isVerified = source.isVerified();
		re.profileImageUrl = source.getProfileImageUrlHttps().toString();

		re.refreshAccount();

		return re;
	}

	public static UserInfo create(twitter4j.Tweet source) {
		UserInfo re = new UserInfo();
		re.id = source.getFromUserId();
		re.screenName = source.getFromUser();
		re.profileImageUrl = source.getProfileImageUrl();

		re.refreshAccount();

		return re;
	}

	public void merge(twitter4j.User source) {
		id = source.getId();
		screenName = source.getScreenName();
		name = source.getName();
		createdAt = source.getCreatedAt();
		description = source.getDescription();
		location = source.getLocation();
		url = Uri.parse(source.getURL().toString());
		friendsCount = source.getFriendsCount();
		followersCount = source.getFollowersCount();
		statusesCount = source.getStatusesCount();
		favouritesCount = source.getFavouritesCount();
		listedCount = source.getListedCount();
		isProtected = source.isProtected();
		isVerified = source.isVerified();
		profileImageUrl = source.getProfileImageUrlHttps().toString();

		refreshAccount();

		for (Action1<UserInfo> handler : mergedHandler) {
			handler.invoke(this);
		}
	}

	private void refreshAccount() {
		Accounts.getAllAccounts()
			.where(new Func2<Account, Integer, Boolean>() {
				@Override
				public Boolean invoke(Account arg0, Integer arg1) {
					return arg0.getId() == id;
				}
			})
			.forEach(new Action2<Account, Integer>() {
				@Override
				public void invoke(Account arg0, Integer arg1) {
					arg0.setScreenName(screenName);
				}
			});
	}

	public static UserInfo getDummyUser() {
		UserInfo re = new UserInfo();
		re.id = 0;
		re.screenName = "dummydummydummydummy";
		re.name = "dummydummydummydummy";
		re.createdAt = new Date();
		re.profileImageUrl = "https://si0.twimg.com/sticky/default_profile_images/default_profile_0_normal.png";
		return re;
	}
}
