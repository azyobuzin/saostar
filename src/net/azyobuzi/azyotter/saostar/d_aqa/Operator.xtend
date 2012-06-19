package net.azyobuzi.azyotter.saostar.d_aqa;

public abstract class Operator implements Invokable {
	public Operator(Invokable left, Invokable right) {
		this.left = left;
		this.right = right;
	}

	protected Invokable left;
	protected Invokable right;

	@Override
	public int getResultType() {
		return Invokable.TYPE_BOOLEAN;
	}
}
