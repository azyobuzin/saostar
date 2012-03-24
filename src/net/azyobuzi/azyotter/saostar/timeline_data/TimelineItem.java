package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Func2;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.URLEntity;

import android.net.Uri;

public class TimelineItem {
	public TimelineItemId id;
	public Date createdAt;
	public String originalText;
	public String displayText; //Entities適用済み
	public String sourceName;
	public Uri sourceUrl;
	public UserInfo from;
	public UserInfo to;
	public long inReplyToStatusId;
	public TimelineItem retweeted;
	public TweetEntities entities;
	public boolean isHomeTweet; //ホームに表示されるべきツイートかどうか

	public final ArrayList<Action1<TimelineItem>> mergedHandler = new ArrayList<Action1<TimelineItem>>();
	private void raiseMerged() {
		for (Action1<TimelineItem> handler : mergedHandler) {
			handler.invoke(this);
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof TimelineItem)) return false;
		return id.equals(((TimelineItem)other).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public static TimelineItem create(Status source, boolean isHomeTweet) {
		TimelineItem re = new TimelineItem();
		re.id = new TimelineItemId(TimelineItemId.TYPE_TWEET, source.getId());
		re.createdAt = source.getCreatedAt();
		re.originalText = source.getText();
		re.displayText = createDisplayText(source);
		Matcher sourceMatcher = sourcePattern.matcher(source.getSource());
		if (sourceMatcher.find()) {
			re.sourceName = sourceMatcher.group(2);
			re.sourceUrl = Uri.parse(sourceMatcher.group(1));
		} else {
			re.sourceName = source.getSource();
			re.sourceUrl = Uri.parse("http://twitter.com/");
		}
		re.from = UserCollection.addOrMerge(source.getUser());
		re.inReplyToStatusId = source.getInReplyToStatusId();
		if (source.isRetweet())
			re.retweeted = TimelineItemCollection.addOrMerge(source.getRetweetedStatus(), false);
		re.entities = new TweetEntities(source);
		re.isHomeTweet = isHomeTweet;
		return re;
	}

	public void merge(Status source, boolean isHomeTweet) {
		id = new TimelineItemId(TimelineItemId.TYPE_TWEET, source.getId());
		createdAt = source.getCreatedAt();
		originalText = source.getText();
		displayText = createDisplayText(source);
		Matcher sourceMatcher = sourcePattern.matcher(source.getSource());
		if (sourceMatcher.find()) {
			sourceName = sourceMatcher.group(2);
			sourceUrl = Uri.parse(sourceMatcher.group(1));
		} else {
			sourceName = source.getSource();
			sourceUrl = Uri.parse("http://twitter.com/");
		}
		from = UserCollection.addOrMerge(source.getUser());
		inReplyToStatusId = source.getInReplyToStatusId();
		retweeted = TimelineItemCollection.addOrMerge(source.getRetweetedStatus(), false);
		entities = new TweetEntities(source);
		this.isHomeTweet = isHomeTweet ? true : this.isHomeTweet;
		raiseMerged();
	}

	private static String createDisplayText(final Status source) {
		final StringBuilder sb = new StringBuilder();
		ArrayList<URLEntity> entities = Enumerable.from(source.getURLEntities())
			.concat(Enumerable.from(source.getMediaEntities()))
			.toArrayList();
		Collections.sort(entities, new Comparator<URLEntity>() {
			@Override
			public int compare(URLEntity lhs, URLEntity rhs) {
				return ((Integer)lhs.getStart()).compareTo(rhs.getStart());
			}
		});
		int lastIndex = Enumerable.from(entities)
			.select(new Func2<URLEntity, Integer, Integer>() {
				private int index = 0;

				@Override
				public Integer invoke(URLEntity arg0, Integer arg1) {
					sb.append(source.getText().substring(index, arg0.getStart() - 1));
					sb.append(arg0.getDisplayURL());
					index = arg0.getEnd();
					return index;
				}
			})
			.lastOrDefault(0);
		sb.append(source.getText().substring(lastIndex));
		return sb.toString();
	}

	private static Pattern sourcePattern = Pattern.compile("^<a href=\"(https?://[\\w\\d/%#$&?!()~_.=+-]+)\" rel=\"nofollow\">(.+)</a>$");
	
	public static TimelineItem create(DirectMessage source) {
		TimelineItem re = new TimelineItem();
		re.id = new TimelineItemId(TimelineItemId.TYPE_DIRECT_MESSAGE, source.getId());
		re.createdAt = source.getCreatedAt();
		re.originalText = source.getText();
		re.displayText = source.getText();
		re.from = UserCollection.addOrMerge(source.getSender());
		re.to = UserCollection.addOrMerge(source.getRecipient());
		return re;
	}
	
	public void merge(DirectMessage source) {
		id = new TimelineItemId(TimelineItemId.TYPE_DIRECT_MESSAGE, source.getId());
		createdAt = source.getCreatedAt();
		originalText = source.getText();
		displayText = source.getText();
		from = UserCollection.addOrMerge(source.getSender());
		to = UserCollection.addOrMerge(source.getRecipient());
		raiseMerged();
	}
}
