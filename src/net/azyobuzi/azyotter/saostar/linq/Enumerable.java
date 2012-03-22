package net.azyobuzi.azyotter.saostar.linq;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

import net.azyobuzi.azyotter.saostar.JexlHelper;
import net.azyobuzi.azyotter.saostar.system.Action2;
import net.azyobuzi.azyotter.saostar.system.Func;
import net.azyobuzi.azyotter.saostar.system.Func2;

public abstract class Enumerable<T> {
	public static <T> Enumerable<T> from(final Enumerator<T> enumerator) {
		return new Enumerable<T>() {
			@Override
			public Enumerator<T> getEnumerator() {
				return enumerator;
			}
		};
	}

	public static <T> Enumerable<T> from(final Iterable<T> source) {
		return new Enumerable<T>() {
			@Override
			public Enumerator<T> getEnumerator() {
				return new Enumerator<T>() {
					private Iterator<T> itr = source.iterator();
					private T current;

					@Override
					public boolean moveNext() {
						if (itr.hasNext()) {
							current = itr.next();
							return true;
						} else {
							return false;
						}
					}

					@Override
					public T getCurrent() {
						return current;
					}
				};
			}
		};
	}

	public static <T> Enumerable<T> from(final T[] source) {
		return new Enumerable<T>() {
			@Override
			public Enumerator<T> getEnumerator() {
				return new Enumerator<T>() {
					private int index = -1;
					private T current;

					@Override
					public boolean moveNext() {
						index++;

						if (source.length > index) {
							current = source[index];
							return true;
						} else {
							return false;
						}
					}

					@Override
					public T getCurrent() {
						return current;
					}
				};
			}
		};
	}

	public abstract Enumerator<T> getEnumerator();

	public void forEach(Action2<T, Integer> action) {
		Enumerator<T> enumerator = getEnumerator();
		int index = 0;
		while (enumerator.moveNext()) {
			action.invoke(enumerator.getCurrent(), index++);
		}
	}

	public void forEach(String expression, Func<JexlContext> contextGenerator) {
		Expression expr = JexlHelper.createExpression(expression);

		Enumerator<T> enumerator = getEnumerator();
		int index = 0;
		while (enumerator.moveNext()) {
			JexlContext context = null;
			if (contextGenerator != null) context = contextGenerator.invoke();
			if (context == null) context = new MapContext();
			context.set("item", enumerator.getCurrent());
			context.set("index", index++);
			expr.evaluate(context);
		}
	}

	public ArrayList<T> toArrayList() {
		final ArrayList<T> re = new ArrayList<T>();
		forEach(new Action2<T, Integer>() {
			@Override
			public void invoke(T arg0, Integer arg1) {
				re.add(arg0);
			}
		});
		return re;
	}

	public <TResult> Enumerable<TResult> select(final Func2<T, Integer, TResult> selector) {
		return from(new Enumerator<TResult>() {
			private Enumerator<T> source = getEnumerator();
			private TResult current;
			private int index = 0;

			@Override
			public boolean moveNext() {
				if (source.moveNext()) {
					current = selector.invoke(source.getCurrent(), index++);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public TResult getCurrent() {
				return current;
			}
		});
	}

	public Enumerable<Object> select(final String expression, final Func<JexlContext> contextGenerator) {
		return from(new Enumerator<Object>() {
			private Enumerator<T> source = getEnumerator();
			private Object current;
			private int index = 0;
			private Expression expr = JexlHelper.createExpression(expression);

			@Override
			public boolean moveNext() {
				if (source.moveNext()) {
					JexlContext context = null;
					if (contextGenerator != null) context = contextGenerator.invoke();
					if (context == null) context = new MapContext();
					context.set("item", source.getCurrent());
					context.set("index", index++);
					current = expr.evaluate(context);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public Object getCurrent() {
				return current;
			}
		});
	}

	public Enumerable<T> where(final Func2<T, Integer, Boolean> predicate) {
		return from(new Enumerator<T>() {
			private Enumerator<T> source = getEnumerator();
			private T current;
			private int index = 0;

			@Override
			public boolean moveNext() {
				if (source.moveNext()) {
					boolean result = predicate.invoke(source.getCurrent(), index++);
					if (result) {
						current = source.getCurrent();
						return true;
					} else {
						return moveNext();
					}
				} else {
					return false;
				}
			}

			@Override
			public T getCurrent() {
				return current;
			}
		});
	}

	public Enumerable<T> where(final String expression, final Func<JexlContext> contextGenerator) {
		return from(new Enumerator<T>() {
			private Enumerator<T> source = getEnumerator();
			private T current;
			private int index = 0;
			private Expression expr = JexlHelper.createExpression(expression);

			@Override
			public boolean moveNext() {
				if (source.moveNext()) {
					JexlContext context = null;
					if (contextGenerator != null) context = contextGenerator.invoke();
					if (context == null) context = new MapContext();
					context.set("item", source.getCurrent());
					context.set("index", index++);
					boolean result = (Boolean)expr.evaluate(context);
					if (result) {
						current = source.getCurrent();
						return true;
					} else {
						return moveNext();
					}
				} else {
					return false;
				}
			}

			@Override
			public T getCurrent() {
				return current;
			}
		});
	}

	public <TResult> Enumerable<TResult> cast() {
		return select(new Func2<T, Integer, TResult>() {
			@SuppressWarnings("unchecked")
			@Override
			public TResult invoke(T arg0, Integer arg1) {
				return (TResult)arg0;
			}
		});
	}

	public T firstOrDefault(T defaultValue) {
		T result = defaultValue;

		Enumerator<T> source = getEnumerator();
		if (source.moveNext())
			result = source.getCurrent();

		return result;
	}

	public T lastOrDefault(T defaultValue) {
		T result = defaultValue;

		Enumerator<T> source = getEnumerator();
		while (source.moveNext()) {
			result = source.getCurrent();
		}

		return result;
	}

	public Enumerable<T> concat(final Enumerable<? extends T> second) {
		return from(new Enumerator<T>() {
			private T current;

			private Enumerator<T> firstEnumerator = getEnumerator();
			private Enumerator<? extends T> secondEnumerator = second.getEnumerator();

			@Override
			public boolean moveNext() {
				if (firstEnumerator.moveNext()) {
					current = firstEnumerator.getCurrent();
					return true;
				} else if (secondEnumerator.moveNext()) {
					current = secondEnumerator.getCurrent();
					return true;
				}

				return false;
			}

			@Override
			public T getCurrent() {
				return current;
			}
		});
	}
	
	public T elementAtOrDefault(int index, T defaultValue) {
		int i = 0;
		Enumerator<T> source = getEnumerator();
		while (source.moveNext()) {
			if (i++ == index) return source.getCurrent();
		}
		
		return defaultValue;
	}
}
