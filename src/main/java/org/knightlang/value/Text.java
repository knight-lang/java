package org.knightlang.value;

import org.knightlang.exception.RunException;

public final class Text extends Idempotent {
	private final String text;

	public Text(String text) {
		this.text = text;
	}

	@Override
	public void dump() {
		System.out.printf("String(%s)", toString());
	}

	@Override
	public long toLong() {
		String txt = text;

		while (!txt.isEmpty() && Character.isWhitespace(txt.charAt(0)))
			txt = txt.substring(1);
		
		if (txt.isEmpty())
			return 0;

		boolean isNegative = txt.charAt(0) == '-';

		if (isNegative || txt.charAt(0) == '+')
			txt = txt.substring(1);
		
		long result = 0;

		while (!txt.isEmpty() && Character.isDigit(txt.charAt(0))) {
			result = result * 10 + txt.charAt(0) - '0';
			txt = txt.substring(1);
		}

		return result * (isNegative ? -1 : 1);
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public boolean toBoolean() {
		return !text.isEmpty();
	}

	@Override
	public boolean equals(Object rhs) {
		return rhs instanceof Text && text.equals(((Text) rhs).text);
	}

	@Override
	public int compareTo(Value rhs) {
		return text.compareTo(rhs.toString());
	}

	@Override
	public Value add(Value rhs) {
		return new Text(text + rhs.toString());
	}

	@Override
	public Value multiply(Value rhs) {
		String ret = "";
		long amnt = rhs.toLong();

		if (amnt < 0) {
			throw new RunException("cannot multiply a string by a negative amount.");
		}

		while (amnt-- != 0) {
			ret += text;
		}

		return new Text(ret);
	}
}
