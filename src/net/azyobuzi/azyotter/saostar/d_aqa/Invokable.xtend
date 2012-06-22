package net.azyobuzi.azyotter.saostar.d_aqa

import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem

abstract class Invokable {
	/*
	 * 型
	 * TYPE_STRING - String
	 * TYPE_NUMBER - long
	 * TYPE_BOOLEAN - boolean
	 * TYPE_DATETIME - Date
	 * TYPE_STRING_ARRAY - String[]
	 */
	public static val TYPE_STRING = 0
	public static val TYPE_NUMBER = 1
	public static val TYPE_BOOLEAN = 2
	public static val TYPE_DATETIME = 3
	public static val TYPE_STRING_ARRAY = 4

	def int getResultType()
	def Object invoke(TimelineItem target)
}
