package net.azyobuzi.azyotter.saostar.d_aqa

import java.io.EOFException
import java.util.ArrayList
import java.util.GregorianCalendar

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.d_aqa.operators.*
import net.azyobuzi.azyotter.saostar.d_aqa.properties.*
import net.azyobuzi.azyotter.saostar.linq.Enumerable
import net.azyobuzi.azyotter.saostar.system.Func2

class Reader {
	//
	// Public Static Members
	//

	static val properties = new PropertyFactory[] {
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
	}

	static val operators = new OperatorFactory[] {
		new EqualityOperator.Factory(),
		new InequalityOperator.Factory(),
		new GreaterThanOperator.Factory(),
		new GreaterThanOrEqualOperator.Factory(),
		new LessThanOperator.Factory(),
		new LessThanOrEqualOperator.Factory()
	}

	static val functions = new FunctionFactory[] {
		//ファクトリー
	}

	def static read(String query) {
		read(new Reader(query))
	}

	//
	// Private Static Members
	//

	def private static endedInUnexpectedPosition() {
		throw new EOFException(ContextAccess.getText(R.string.query_ended_in_unexpected_position).toString())
	}

	private static val space = " 　\r\n\t"

	def private static Invokable read(Reader reader) {
		val char firstChar

		while (true) {
			val i = reader.read()
			if (i == -1) endedInUnexpectedPosition()

			val c = (char)i
			if (!space.contains(String.valueOf(c))) {
				firstChar = c
				break
			}
		}

		val sb = new StringBuilder()
		switch (firstChar) {
			case '"':
				while (true) {
					int c = reader.read()
					if (c == -1) endedInUnexpectedPosition()
					if (c == '"') break
					if (c == '\\') {
						int c2 = reader.read()
						if (c2 == -1) endedInUnexpectedPosition()
						c = switch (c2) {
							case 'r': '\r'
							case 'n': '\n'
							case 't': '\t'
							default: c2
						}
					}
					sb.append((char)c)
				}
				new Constant(Invokable.TYPE_STRING, sb.toString())

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
				sb.append(firstChar)
				while (true) {
					val i = reader.peek()
					if (i == -1) break

					val c = (char)i;
					if (!"0123456789".contains(String.valueOf(c))) break

					sb.append(c)
					reader.read()
				}
				new Constant(Invokable.TYPE_NUMBER, Long.valueOf(sb.toString()))

			case 't':
			case 'f':
				sb.append(firstChar)
				while (true) {
					val i = reader.peek()
					if (i == -1) break

					val c = (char)i
					if ((space + ")").contains(String.valueOf(c))) break

					sb.append(c)
					reader.read()
				}
				new Constant(Invokable.TYPE_BOOLEAN, Boolean.valueOf(sb.toString()))

			case 'd':
				val timeFlag = false
				while (true) {
					int i = reader.peek()
					if (i == -1) break

					val c = (char)i
					if (!"/-0123456789".contains(String.valueOf(c))) break
					if (c == '-') {
						timeFlag = true
						reader.read()
						break
					}

					sb.append(c)
					reader.read()
				}

				val splittedDate = sb.toString().split("/")
				val year = Integer.valueOf(splittedDate[0])
				val month = Integer.valueOf(splittedDate[1])
				val day = Integer.valueOf(splittedDate[2])

				if (!timeFlag) {
					new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day).getTime());
				} else {
					sb = new StringBuilder();
					while (true) {
						val i = reader.peek()
						if (i == -1) break

						val c = (char)i
						if (!":0123456789".contains(String.valueOf(c))) break

						sb.append(c)
						reader.read()
					}

					val splittedTime = sb.toString().split(":")
					val hour = Integer.valueOf(splittedTime[0])
					val minute = Integer.valueOf(splittedTime[1])

					if (splittedTime.length == 3) {
						val second = Integer.valueOf(splittedTime[2])
						new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day, hour, minute, second).getTime())
					} else {
						new Constant(Invokable.TYPE_DATETIME, new GregorianCalendar(year, month, day, hour, minute).getTime())
					}
				}

			case '(':
				val left = read(reader)

				if (left instanceof Function) {
					val args = new ArrayList<Invokable>()
					while (true) {
						val arg = read(reader)
						if (arg != null) args.add(arg)
						else break
					}
					(left as Function).setArguments(args)
					left
				} else {
					while (true) {
						val i = reader.read()
						if (i == -1) endedInUnexpectedPosition()

						val c = (char)i
						if (!space.contains(String.valueOf(c))) {
							sb.append(c)

							while (true) {
								i = reader.read()
								if (i == -1) endedInUnexpectedPosition()

								c = (char)i
								if (space.contains(String.valueOf(c))) break
								sb.append(c)
							}

							val right = read(reader)
							read(reader) // )まで読み取る

							val opId = sb.toString()

							try {
								Enumerable.from(operators).where(new Func2<OperatorFactory, Integer, Boolean>() {
									override invoke(OperatorFactory arg0, Integer arg1) {
										arg0.getOperatorIdentifier().equals(opId)
											&& arg0.getParameterTypes().get(left.getResultType()) == right.getResultType();
									}
								}).firstOrDefault(null).createOperator(left, right)
							} catch (NullPointerException ex) {
								ex.printStackTrace()
								throw new IllegalArgumentException(
									ContextAccess.getText(R.string.not_found_operator_or_type_has_not_matched)
										.toString().replace("$opId$", opId),
									ex
								)
							}
						}
					}
				}

			case ')':
				null

			default:
				sb.append(firstChar)

				while (true) {
					val i = reader.read()
					if (i == -1) endedInUnexpectedPosition()
					if (i == ':') break
					sb.append((char)i)
				}

				val type = sb.toString()

				sb = new StringBuilder()
				while (true) {
					int i = reader.read()
					if (i == -1) break

					val c = (char)i
					if (space.contains(String.valueOf(c))) break
					sb.append(c)
				}

				val id = sb.toString()

				if (type.equals("prop")) {
					Enumerable.from(properties).where(new Func2<PropertyFactory, Integer, Boolean>() {
						override invoke(PropertyFactory arg0, Integer arg1) {
							arg0.getPropertyName().equals(id)
						}
					}).firstOrDefault(null).createProperty()
				} else if (type.equals("func")) {
					Enumerable.from(functions).where(new Func2<FunctionFactory, Integer, Boolean>() {
						override invoke(FunctionFactory arg0, Integer arg1) {
							arg0.getFunctionName().equals(id)
						}
					}).firstOrDefault(null).createFunction()
				} else {
					throw new IllegalArgumentException(ContextAccess.getText(R.string.cannot_use_other_than_prop_and_func).toString())
				}
		}
	}

	//
	// Private Instance Members
	//

	private new(String s) {
		this.s = s;
	}

	private String s;
	private int position = 0;

	def private peek() {
		if (s.length() > position)
			s.charAt(position);
		else
			-1;
	}

	def private read() {
		if (s.length() > position)
		{
			s.charAt(position++)
		} else {
			-1
		}
	}
}
