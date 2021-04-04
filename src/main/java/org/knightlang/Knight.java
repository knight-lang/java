package org.knightlang;

import org.knightlang.value.Value;
import org.knightlang.exception.ParseException;

public class Knight {
	public static Value run(String input) throws RuntimeException, ParseException {
		Value value = Value.parse(new Stream(input));

		if (value == null)
			throw new ParseException("Couldn't parse anything from the stream");

		return value.run();
	}

	public static void main(String[] args) {
		// run(args[1]);
		run("D 1 2 3 ");
	}
}
