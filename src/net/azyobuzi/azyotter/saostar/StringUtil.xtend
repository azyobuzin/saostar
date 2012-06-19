package net.azyobuzi.azyotter.saostar;

import java.util.Collection;

import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Action2;

public class StringUtil {
	public static boolean isNullOrEmpty(CharSequence s) {
    	return s == null || s.length() <= 0;
    }

	public static String join(CharSequence separator, Collection<CharSequence> values) {
		StringBuffer sb = new StringBuffer();
		for (CharSequence value : values) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append(value);
		}
		return sb.toString();
	}

	public static String join(final CharSequence separator, Enumerable<CharSequence> values) {
		final StringBuffer sb = new StringBuffer();
		values.forEach(new Action2<CharSequence, Integer>() {
			public void invoke(CharSequence value, Integer index) {
				if (sb.length() > 0) {
					sb.append(separator);
				}
				sb.append(value);
			}
		});
		return sb.toString();
	}
}
