package org.knightlang;

import java.util.Iterator;
import java.util.stream.Collectors;

public class Stream implements Iterator<Character> {
	private Iterator<Character> iter;
	private char prev;
	private boolean rewound;

	public Stream(String input) {
		this(input.chars().mapToObj(c -> (char) c).collect(Collectors.toList()).iterator());
	}

	public Stream(Iterator<Character> iter) {
		this.iter = iter;
		rewound = false;
	}

	@Override
	public boolean hasNext() {
		return rewound || iter.hasNext();
	}

	@Override
	public Character next() {
		if (rewound) {
			rewound = false;
		} else {
			prev = iter.next();
		}

		return prev;
	}

	public char peek() {
		char c = next();
		rewind();
		return c;
	}

	public void rewind() {
		assert !rewound;
		rewound = true;
	}

	public void stripKeyword() {
		char c;

		assert hasNext();
		assert Character.isUpperCase(peek()) || peek() == '_';

		do {
			c = next();
		} while (Character.isUpperCase(c) || c == '_');

		rewind();
	}

}
