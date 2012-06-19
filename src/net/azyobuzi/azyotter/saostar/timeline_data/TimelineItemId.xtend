package net.azyobuzi.azyotter.saostar.timeline_data;

public class TimelineItemId {
	public static final int TYPE_TWEET = 0;
	public static final int TYPE_DIRECT_MESSAGE = 1;
	public static final int TYPE_USER_STREAM_EVENT = 2;
	
	public TimelineItemId(int type, long id) {
		this.type = type;
		this.id = id;
	}
	
	public int type;
	public long id;
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof TimelineItemId)) return false;
		TimelineItemId x = (TimelineItemId)other;
		return type == x.type && id == x.id;
	}
	
	@Override
	public int hashCode() {
		return String.valueOf(id).hashCode();
	}
}
