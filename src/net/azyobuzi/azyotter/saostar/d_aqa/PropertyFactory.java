package net.azyobuzi.azyotter.saostar.d_aqa;

public interface PropertyFactory {
	String getPropertyName();
	int getType();
	Property createProperty();
}
