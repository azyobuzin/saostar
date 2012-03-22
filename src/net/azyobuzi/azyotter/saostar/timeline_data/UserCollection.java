package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.HashMap;

import net.azyobuzi.azyotter.saostar.linq.Enumerable;

public class UserCollection {
	private static final HashMap<Long, UserInfo> dic = new HashMap<Long, UserInfo>();
	private static final Object lockObj = new Object();

	public static Enumerable<UserInfo> getEnumerable() {
		synchronized (lockObj) {
			return Enumerable.from(dic.values().toArray()).cast();
		}
	}

	public static UserInfo addOrMerge(twitter4j.User source) {
		synchronized (lockObj) {
			if (dic.containsKey(source.getId())) {
				UserInfo re = dic.get(source.getId());
				re.merge(source);
				return re;
			} else {
				return dic.put(source.getId(), UserInfo.create(source));
			}
		}
	}

	public static UserInfo get(long id) {
		synchronized (lockObj) {
			return dic.get(id);
		}
	}

	public static boolean containsId(long id) {
		synchronized (lockObj) {
			return dic.containsKey(id);
		}
	}
}
