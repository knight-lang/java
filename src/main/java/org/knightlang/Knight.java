package org.knightlang;

import org.knightlang.value.Value;
import org.knightlang.value.Number;
import org.knightlang.value.Null;
import org.knightlang.value.Bool;
import org.knightlang.value.Text;
import org.knightlang.value.Func;
import org.knightlang.value.Func.FuncInner;
import org.knightlang.value.Variable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.knightlang.exception.ParseException;

public class Knight {
	private static Value parse(Stream stream) throws ParseException {
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
			case 'v': case 'w': case 'x': case 'y': case 'z': case '_':
				String ident = "" + c;

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

				Value[] args = new Value[func.getArity()];

				for (int i = 0; i < args.length; ++i) {
					args[i] = parse(stream);

					if (args[i] == null)
						throw new ParseException("Couldn't parse argument " + i + " for function " + func.getName());
				}

				return new Func(args, func);
		}
	}

	public static Value run(String input) throws RuntimeException, ParseException {
		Value value = parse(new Stream(input));

		if (value == null)
			throw new ParseException("Couldn't parse anything from the stream");

		return value.run();
	}

	public static void main(String[] args) {
		if (args.length != 2 || (!args[0].equals("-e") && !args[0].equals("-f"))) {
			System.err.println("usage: knight (-e 'program' | -f file)");
			System.exit(1);
		}

		try {
			if (args[0].equals("-e")) {
				run(args[1]);
			} else {
				// Stream _s = new Stream(Files.readAllBytes(Paths.get("file")));

				run(new String(Files.readAllBytes(Paths.get(args[1])), StandardCharsets.US_ASCII));
			}
		} catch (Exception err) {
			System.err.println("error occured: " + err);
			err.printStackTrace();
			System.exit(1);
		}
	}
}
