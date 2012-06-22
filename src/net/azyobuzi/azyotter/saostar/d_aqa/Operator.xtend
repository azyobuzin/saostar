package net.azyobuzi.azyotter.saostar.d_aqa

public abstract class Operator extends Invokable {
	new(Invokable left, Invokable right) {
		this.left = left
		this.right = right
	}

	protected Invokable left
	protected Invokable right

	override getResultType() {
		TYPE_BOOLEAN
	}
}
