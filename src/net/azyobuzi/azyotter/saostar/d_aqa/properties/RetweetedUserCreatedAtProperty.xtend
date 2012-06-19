package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import java.util.Date;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class RetweetedUserCreatedAtProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_DATETIME;
	}

	@Override
	public Object invoke(TimelineItem target) {
		if (target.retweeted != null)
			return target.retweeted.from.createdAt;
		else
			return new Date(1900, 1, 1);
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "retweetedUser.createdAt";
		}

		@Override
		public int getType() {
			return TYPE_DATETIME;
		}

		@Override
		public Property createProperty() {
			return new RetweetedUserCreatedAtProperty();
		}

	}

}
