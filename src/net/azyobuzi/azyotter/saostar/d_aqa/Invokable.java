package net.azyobuzi.azyotter.saostar.d_aqa;

import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public interface Invokable {
	/*
	 * 型
	 * TYPE_STRING - String
	 * TYPE_NUMBER - long
	 * TYPE_BOOLEAN - boolean
	 * TYPE_DATETIME - Date
	 * TYPE_STRING_ARRAY - String[]
	 */
	public static final int TYPE_STRING = 0;
	public static final int TYPE_NUMBER = 1;
	public static final int TYPE_BOOLEAN = 2;
	public static final int TYPE_DATETIME = 3;
	public static final int TYPE_STRING_ARRAY = 4;

	int getResultType();
	Object invoke(TimelineItem target);
}
