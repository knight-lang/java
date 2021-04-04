package org.knightlang.value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.knightlang.Knight;
import org.knightlang.exception.RunException;

public final class Func extends NonIdempotent {
	private static final Map<Character, FuncInner> FUNCTIONS = new HashMap<>();

	private final Value[] args;
	private final FuncInner function;

	public Func(Value[] args, FuncInner function) {
		assert args.length == function.arity;

		this.args = args;
		this.function = function;
	}

	@Override
	public Value run() throws RunException {
		return function.func.apply(args);
	}

	@Override
	public void dump() {
		System.out.printf("Function(%c", function.name);

		for (Value value : args) {
			System.out.print(", ");
			value.dump();
		}

		System.out.print(")");
	}

	public static FuncInner fetch(char name) {
		return FUNCTIONS.get(name);
	}

	public static void register(FuncInner func) {
		FUNCTIONS.put(func.name, func);
	}

	public static class FuncInner {
		char name;
		int arity;
		Function<Value[], Value> func;
		FuncInner(char name, int arity, Function<Value[], Value> func) {
			this.name = name; 
			this.arity = arity;
			this.func = func;
		}
	}

	static {
		// arity zero
		register(new FuncInner('P', 0, (Value[] args) -> {
			throw new RunException("todo");
		}));

		register(new FuncInner('R', 0, (Value[] args) -> {
			throw new RunException("todo");
		}));

		// arity one
		register(new FuncInner('E', 1, (Value[] args) -> Knight.run(args[0].toString())));
		register(new FuncInner('B', 1, (Value[] args) -> args[0]));
		register(new FuncInner('C', 1, (Value[] args) -> args[0].run().run()));

		register(new FuncInner('`', 1, (Value[] args) -> {
			throw new RunException("todo");
		}));

		register(new FuncInner('Q', 1, (Value[] args) -> {
			System.exit((int) args[0].toNumber());
			return args[0];
		}));

		register(new FuncInner('!', 1, (Value[] args) -> new Bool(!args[0].toBoolean())));

		register(new FuncInner('D', 1, (Value[] args) -> {
			Value arg = args[0].run();
			arg.dump();
			System.out.println();
			return arg;
		}));

		register(new FuncInner('O', 1, (Value[] args) -> {
			String str = args[0].toString();
			
			if (str.isEmpty()) {
				System.out.println();
			} else if (str.charAt(str.length() - 1) == '\\') {
				// todo
			} else {
				System.out.println(str);
			}

			return new Null();
		}));

		register(new FuncInner('+', 2, (Value[] args) -> args[0].add(args[1])));
		register(new FuncInner('-', 2, (Value[] args) -> args[0].subtract(args[1])));
		register(new FuncInner('*', 2, (Value[] args) -> args[0].multiply(args[1])));
		register(new FuncInner('/', 2, (Value[] args) -> args[0].divide(args[1])));
		register(new FuncInner('%', 2, (Value[] args) -> args[0].modulo(args[1])));
		register(new FuncInner('^', 2, (Value[] args) -> args[0].exponentiate(args[1])));
		register(new FuncInner('<', 2, (Value[] args) -> new Bool(args[0].compareTo(args[1]) < 0)));
		register(new FuncInner('>', 2, (Value[] args) -> new Bool(args[0].compareTo(args[1]) > 0)));
		register(new FuncInner('&', 2, (Value[] args) -> {
			Value lhs = args[0].run();
			
			if (lhs.toBoolean()) {
				return args[1].run();
			} else {
				return args[1].run();
			}
		}));

		register(new FuncInner('|', 2, (Value[] args) -> {
			Value lhs = args[0].run();
			
			if (!lhs.toBoolean()) {
				return args[1].run();
			} else {
				return args[1].run();
			}
		}));

		register(new FuncInner(';', 2, (Value[] args) -> {
			args[0].run();
			return args[1].run();
		}));

		register(new FuncInner('=', 2, (Value[] args) -> {
			Value ret = args[1].run();
			((Variable) args[0]).assign(ret);
			return ret;
		}));

		register(new FuncInner('W', 2, (Value[] args) -> {
			while (args[0].toBoolean())
				args[1].run();

			return new Null();
		}));

		// arity three
		register(new FuncInner('I', 3, (Value[] args) -> {
			return args[0].toBoolean() ? args[1].run() : args[2].run();
		}));

		register(new FuncInner('G', 3, (Value[] args) -> {
			throw new RunException("todo");
		}));

		// arity four
		register(new FuncInner('S', 4, (Value[] args) -> {
			throw new RunException("todo");
		}));
	}
}
