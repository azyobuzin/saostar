package net.azyobuzi.azyotter.saostar.d_aqa;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.d_aqa.operators.*;
import net.azyobuzi.azyotter.saostar.d_aqa.properties.*;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Func2;

public class Reader {
	//
	// Public Static Members
	//

	public static final PropertyFactory[] properties = new PropertyFactory[] {
		new IdProperty.Factory(),
		new CreatedAtProperty.Factory(),
		new TextProperty.Factory(),
		new SourceProperty.Factory(),
		new FromIdProperty.Factory(),
		new FromScreenNameProperty.Factory(),
		new FromNameProperty.Factory(),
		new FromCreatedAtProperty.Factory(),
		new FromProtectedProperty.Factory(),
		new FromVerifiedProperty.Factory(),
		new RetweetedProperty.Factory(),
		new RetweetedIdProperty.Factory(),
		new RetweetedCreatedAtProperty.Factory(),
		new OriginalTextProperty.Factory(),
		new RetweetedSourceProperty.Factory(),
		new RetweetedUserIdProperty.Factory(),
		new RetweetedUserScreenNameProperty.Factory(),
		new RetweetedUserNameProperty.Factory(),
		new RetweetedUserCreatedAtProperty.Factory(),
		new RetweetedUserVerifiedProperty.Factory(),
		new InReplyToProperty.Factory(),
		new IsHomeTweetProperty.Factory()
	};

	public static final OperatorFactory[] operators = new OperatorFactory[] {
		new EqualityOperator.Factory(),
		new InequalityOperator.Factory(),
		new GreaterThanOperator.Factory(),
		new GreaterThanOrEqualOperator.Factory(),
		new LessThanOperator.Factory(),
		new LessThanOrEqualOperator.Factory()
	};

	public static final FunctionFactory[] functions = new FunctionFactory[] {
		//ファクトリー
	};

	public static Invokable read(String query) throws EOFException, IllegalArgumentException {
		return read(new Reader(query));
	}

	//
	// Private Static Members
	//

	private static void endedInUnexpectedPosition() throws EOFException {
		throw new EOFException(ContextAccess.getText(R.string.query_ended_in_unexpected_position).toString());
	}

	private static final String space = " 　\r\n\t";

	private static Invokable read(Reader reader) throws EOFException, IllegalArgumentException {
		char firstChar;

		while (true) {
			int i = reader.read();
			if (i == -1) endedInUnexpectedPosition();

			char c = (char)i;
			if (!space.contains(String.valueOf(c))) {
				firstChar = c;
				break;
			}
		}

		StringBuilder sb = new StringBuilder();
		switch (firstChar) {
			case '"':
				while (true) {
					int c = reader.read();
					if (c == -1) endedInUnexpectedPosition();
					if (c == '"') break;
					if (c == '\\') {
						int c2 = reader.read();
						if (c2 == -1) endedInUnexpectedPosition();
						switch (c2) {
							case 'r':
								c = '\r';
								break;
							case 'n':
								c = '\n';
								break;
							case 't':
								c = '\t';
								break;
							default:
								c = c2;
								break;
						}
					}
					sb.append((char)c);
				}
				return new Constant(Invokable.TYPE_STRING, sb.toString());

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				sb.append(firstChar);
				while (true) {
					int i = reader.peek();
					if (i == -1) break;

					char c = (char)i;
					if (!"0123456789".contains(String.valueOf(c))) break;

					sb.append(c);
					reader.read();
				}
				return new Constant(Invokable.TYPE_NUMBER, Long.valueOf(sb.toString()));

			case 't':
			case 'f':
				sb.append(firstChar);
				while (true) {
					int i = reader.peek();
					if (i == -1) break;

					char c = (char)i;
					if ((space + ")").contains(String.valueOf(c))) break;

					sb.append(c);
					reader.read();
				}
				return new Constant(Invokable.TYPE_BOOLEAN, Boolean.valueOf(sb.toString()));

			case 'd':
				boolean timeFlag = false;
				while (true) {
					int i = reader.peek();
					if (i == -1) break;

					char c = (char)i;
					if (!"/-0123456789".contains(String.valueOf(c))) break;
					if (c == '-') {
						timeFlag = true;
						reader.read();
						break;
					}

					sb.append(c);
					reader.read();
				}

				String[] splittedDate = sb.toString().split("/");
				int year = Integer.valueOf(splittedDate[0]);
				int month = Integer.valueOf(splittedDate[1]);
				int day = Integer.valueOf(splittedDate[2]);

				if (!timeFlag) {
					return new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day).getTime());
				} else {
					sb = new StringBuilder();
					while (true) {
						int i = reader.peek();
						if (i == -1) break;

						char c = (char)i;
						if (!":0123456789".contains(String.valueOf(c))) break;

						sb.append(c);
						reader.read();
					}

					String[] splittedTime = sb.toString().split(":");
					int hour = Integer.valueOf(splittedTime[0]);
					int minute = Integer.valueOf(splittedTime[1]);

					if (splittedTime.length == 3) {
						int second = Integer.valueOf(splittedTime[2]);
						return new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day, hour, minute, second).getTime());
					} else {
						return new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day, hour, minute).getTime());
					}
				}

			case '(':
				final Invokable left = read(reader);

				if (left instanceof Function) {
					ArrayList<Invokable> args = new ArrayList<Invokable>();
					while (true) {
						Invokable arg = read(reader);
						if (arg != null) args.add(arg);
						else break;
					}
					((Function)left).setArguments(args);
					return left;
				} else {
					while (true) {
						int i = reader.read();
						if (i == -1) endedInUnexpectedPosition();

						char c = (char)i;
						if (!space.contains(String.valueOf(c))) {
							sb.append(c);

							while (true) {
								i = reader.read();
								if (i == -1) endedInUnexpectedPosition();

								c = (char)i;
								if (space.contains(String.valueOf(c))) break;
								sb.append(c);
							}

							final Invokable right = read(reader);
							read(reader); // )まで読み取る

							final String opId = sb.toString();

							try {
								return Enumerable.from(operators).where(new Func2<OperatorFactory, Integer, Boolean>() {
									@Override
									public Boolean invoke(OperatorFactory arg0, Integer arg1) {
										return arg0.getOperatorIdentifier().equals(opId)
											&& arg0.getParameterTypes().get(left.getResultType()) == right.getResultType();
									}
								}).firstOrDefault(null).createOperator(left, right);
							} catch (NullPointerException ex) {
								ex.printStackTrace();
								throw new IllegalArgumentException(
									ContextAccess.getText(R.string.not_found_operator_or_type_has_not_matched)
										.toString().replace("$opId$", opId),
									ex
								);
							}
						}
					}
				}

			case ')':
				return null;

			default:
				sb.append(firstChar);

				while (true) {
					int i = reader.read();
					if (i == -1) endedInUnexpectedPosition();
					if (i == ':') break;
					sb.append((char)i);
				}

				String type = sb.toString();

				sb = new StringBuilder();
				while (true) {
					int i = reader.read();
					if (i == -1) break;

					char c = (char)i;
					if (space.contains(String.valueOf(c))) break;
					sb.append(c);
				}

				final String id = sb.toString();

				if (type.equals("prop")) {
					return Enumerable.from(properties).where(new Func2<PropertyFactory, Integer, Boolean>() {
						@Override
						public Boolean invoke(PropertyFactory arg0, Integer arg1) {
							return arg0.getPropertyName().equals(id);
						}
					}).firstOrDefault(null).createProperty();
				} else if (type.equals("func")) {
					return Enumerable.from(functions).where(new Func2<FunctionFactory, Integer, Boolean>() {
						@Override
						public Boolean invoke(FunctionFactory arg0, Integer arg1) {
							return arg0.getFunctionName().equals(id);
						}
					}).firstOrDefault(null).createFunction();
				} else {
					throw new IllegalArgumentException(ContextAccess.getText(R.string.cannot_use_other_than_prop_and_func).toString());
				}
		}
	}

	//
	// Private Instance Members
	//

	private Reader(String s) {
		this.s = s;
	}

	private String s;
	private int position = 0;

	private int peek() {
		if (s.length() > position)
			return s.charAt(position);
		else
			return -1;
	}

	private int read() {
		if (s.length() > position)
		{
			return s.charAt(position++);
		} else {
			return -1;
		}
	}
}
