package org.knightlang.value;

import org.knightlang.exception.ParseException;
import org.knightlang.exception.RunException;
import org.knightlang.value.Func.FuncInner;
import org.knightlang.Stream;

public interface Value extends Comparable<Value> {
	public Value run() throws RunException;
	public void dump();

	public long toLong() throws RunException;
	public String toString() throws RunException;
	public boolean toBoolean() throws RunException;

	public default Value add(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot add");
	}

	public default Value subtract(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot subtract");
	}

	public default Value multiply(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot multiply");
	}

	public default Value divide(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot divide");
	}

	public default Value modulo(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot modulo");
	}

	public default Value exponentiate(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot exponentiate");
	}

	public default int compareTo(Value rhs) throws RunException {
		throw new UnsupportedOperationException("cannot compare");
	}

	public static Value parse(Stream stream) throws ParseException {
		char c;

		if (!stream.hasNext())
			return null;

		switch (c = stream.next()) {
			case '#':
				do {
					if (!stream.hasNext())
						return null;
				} while (stream.next() != '\n');
				// fallthrough.

			case ' ': case '\t': case '\f': case '\r': case '\n':
			case ':': case  '(': case  ')': case  '[': case  ']': case '{': case '}':
				return parse(stream); // simply ignore that character.

			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				long num = c - '0';
				
				while (stream.hasNext()) {
					if (Character.isDigit(c = stream.next())) {
						num = num *  10 + (c - '0');
					} else {
						stream.rewind();
						break;
					}
				}

				return new Number(num);
			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g':
			case 'h': case 'i': case 'j': case 'k': case 'l': case 'm': case 'n':
			case 'o': case 'p': case 'q': case 'r': case 's': case 't': case 'u':
			case 'w': case 'x': case 'y': case 'z': case '_':
				String ident = "";

				while (stream.hasNext()) {
					c = stream.next();

					if (Character.isLowerCase(c) || Character.isDigit(c) || c == '_') {
						ident += c; 
					} else {
						stream.rewind();
						break;
					}
				}

				return Variable.fetch(ident);

			case 'T':
			case 'F':
			case 'N':
				Value ret = c == 'N' ? new Null() : new Bool(c == 'T');
				while (stream.hasNext()) {
					if (!Character.isUpperCase(c = stream.next()) && c != '_') {
						stream.rewind();
						break;
					}
				}
				return ret;

			case '\"':
			case '\'':
				char quote = c;
				String string = "";

				while (true) {
					if (!stream.hasNext())
						throw new ParseException("Unterimanted quote found. Stream start: " + string);
					if (quote == (c = stream.next()))
						break;
					string += c;
				}

				return new Text(string);

			default:
				FuncInner func = Func.fetch(c);

				if (func == null)
					throw new ParseException("Unknown token start '" + c + "'");

				if (Character.isUpperCase(c)) {
					while (stream.hasNext()) {
						if (!Character.isUpperCase(c = stream.next()) && c != '_') {
							stream.rewind();
							break;
						}
					}
				}

				Value[] args = new Value[func.arity];
				Value arg;

				for (int i = 0; i < args.length; ++i) {
					if ((arg = parse(stream)) == null) {
						throw new ParseException("Couldn't parse argument " + i + " for function " + func.name);
					}

					args[i] = arg;
				}

				return new Func(args, func);
		}
	}
}
