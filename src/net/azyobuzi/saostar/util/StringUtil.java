package net.azyobuzi.saostar.util;

import java.util.Collection;

public class StringUtil
{
    public static String join(final CharSequence separator, final Collection<CharSequence> values)
    {
        final StringBuffer sb = new StringBuffer();
        for (final CharSequence value : values)
        {
            if (sb.length() > 0)
            {
                sb.append(separator);
            }
            sb.append(value);
        }
        return sb.toString();
    }

    public static String join(final CharSequence separator, final Enumerable<CharSequence> values)
    {
        final StringBuffer sb = new StringBuffer();
        values.forEach(new Action2<CharSequence, Integer>()
        {
            @Override
            public void invoke(final CharSequence value, final Integer index)
            {
                if (sb.length() > 0)
                {
                    sb.append(separator);
                }
                sb.append(value);
            }
        });
        return sb.toString();
    }
}
