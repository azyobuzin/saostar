package net.azyobuzi.azyotter.saostar.linq;

public interface Enumerator<T> {
	boolean moveNext();
	T getCurrent();
}
