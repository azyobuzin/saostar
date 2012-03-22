package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.HashMap;

import net.azyobuzi.azyotter.saostar.linq.Enumerable;

public class TimelineItemCollection {
	private static final HashMap<TimelineItemId, TimelineItem> dic = new HashMap<TimelineItemId, TimelineItem>();
	private static final Object lockObj = new Object();

	public static Enumerable<TimelineItem> getEnumerable() {
		synchronized (lockObj) {
			return Enumerable.from(dic.values().toArray()).cast();
		}
	}

	public static TimelineItem addOrMerge(twitter4j.Status source, boolean isHomeTweet) {
		synchronized (lockObj) {
			TimelineItemId key = new TimelineItemId(TimelineItemId.TYPE_TWEET, source.getId());
			if (dic.containsKey(key)) {
				TimelineItem re = dic.get(key);
				re.merge(source, isHomeTweet);
				return re;
			} else {
				return dic.put(key, TimelineItem.create(source, isHomeTweet));
			}
		}
	}

	public static TimelineItem get(TimelineItemId id) {
		synchronized (lockObj) {
			return dic.get(id);
		}
	}

	public static TimelineItem getTweet(long id) {
		synchronized (lockObj) {
			return dic.get(new TimelineItemId(TimelineItemId.TYPE_TWEET, id));
		}
	}

	public static void removeTweet(long id) {
		synchronized (lockObj) {
			dic.remove(new TimelineItemId(TimelineItemId.TYPE_TWEET, id));
		}
	}

	public static TimelineItem addOrMerge(twitter4j.DirectMessage source) {
		synchronized (lockObj) {
			TimelineItemId key = new TimelineItemId(TimelineItemId.TYPE_DIRECT_MESSAGE, source.getId());
			if (dic.containsKey(key)) {
				TimelineItem re = dic.get(key);
				re.merge(source);
				return re;
			} else {
				return dic.put(key, TimelineItem.create(source));
			}
		}
	}

	public static TimelineItem getDirectMessage(long id) {
		synchronized (lockObj) {
			return dic.get(new TimelineItemId(TimelineItemId.TYPE_DIRECT_MESSAGE, id));
		}
	}

	public static void removeDirectMessage(long id) {
		synchronized (lockObj) {
			dic.remove(new TimelineItemId(TimelineItemId.TYPE_DIRECT_MESSAGE, id));
		}
	}
}
