package org.knightlang.value;

import java.util.HashMap;
import java.util.Map;

import org.knightlang.exception.RunException;

public final class Variable extends NonIdempotent {
	private static final Map<String, Variable> VARIABLES = new HashMap<>();

	private final String name;
	private Value value;

	private Variable(String name) {
		this.name = name;
	}

	@Override
	public void dump() {
		System.out.printf("Identifier(%s)", name);
	}

	@Override
	public Value run() {
		if (value == null) {
			throw new RunException("undefined variable: " + name);
		}

		return value;
	}

	public static Variable fetch(String name) {
		return VARIABLES.computeIfAbsent(name, Variable::new);
	}
	
	public void assign(Value newValue) {
		value = newValue;
	}
}
