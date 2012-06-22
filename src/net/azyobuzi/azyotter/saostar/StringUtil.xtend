package net.azyobuzi.azyotter.saostar

import java.util.Collection

import net.azyobuzi.azyotter.saostar.linq.Enumerable
import net.azyobuzi.azyotter.saostar.system.Action2

class StringUtil {
	def static isNullOrEmpty(CharSequence s) {
    	s == null || s.length() <= 0
    }

	def static join(CharSequence separator, Collection<CharSequence> values) {
		val sb = new StringBuilder()
		for (CharSequence value : values) {
			if (sb.length() > 0) {
				sb.append(separator)
			}
			sb.append(value)
		}
		sb.toString()
	}

	def static join(CharSequence separator, Enumerable<CharSequence> values) {
		val sb = new StringBuilder()
		values.forEach(new Action2<CharSequence, Integer>() {
			override invoke(CharSequence value, Integer index) {
				if (sb.length() > 0) {
					sb.append(separator)
				}
				sb.append(value)
			}
		})
		sb.toString()
	}
}
