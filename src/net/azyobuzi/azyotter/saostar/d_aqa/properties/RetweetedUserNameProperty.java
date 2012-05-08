package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class RetweetedUserNameProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_STRING;
	}

	@Override
	public Object invoke(TimelineItem target) {
		if (target.retweeted != null)
			return target.retweeted.from.name;
		else
			return null;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "retweetedUser.name";
		}

		@Override
		public int getType() {
			return TYPE_STRING;
		}

		@Override
		public Property createProperty() {
			return new RetweetedUserNameProperty();
		}

	}

}
