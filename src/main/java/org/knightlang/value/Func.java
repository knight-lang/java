package org.knightlang.value;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
		private char name;
		private int arity;
		private Function<Value[], Value> func;

		public FuncInner(char name, int arity, Function<Value[], Value> func) {
			this.name = name; 
			this.arity = arity;
			this.func = func;
		}

		public int getArity() {
			return arity;
		}

		public Function<Value[], Value> getFunc() {
			return func;
		}

		public char getName() {
			return name;
		}
	}

	private static final Random RANDOM = new Random();
	private static final Scanner PROMPT = new Scanner(System.in);

	static {
		// arity zero
		register(new FuncInner('P', 0, (Value[] args) -> new Text(PROMPT.nextLine())));
		register(new FuncInner('R', 0, (Value[] args) -> new Number(RANDOM.nextLong())));

		// arity one
		register(new FuncInner('E', 1, (Value[] args) -> Knight.run(args[0].toString())));
		register(new FuncInner('B', 1, (Value[] args) -> args[0]));
		register(new FuncInner('C', 1, (Value[] args) -> args[0].run().run()));

		register(new FuncInner('`', 1, (Value[] args) -> {
			Runtime rt = Runtime.getRuntime();
			String[] commands = {"/bin/sh", "-c", args[0].toString()};
			Process proc;

			try {
				proc = rt.exec(commands);
			} catch (IOException err) {
				throw new RunException("Cannot run command: " + err);
			}
			
			InputStream stdout = proc.getInputStream();
			
			String result = "";
			byte[] buf = new byte[2048];
			int read;

			try {
				while ((read = stdout.read(buf)) != -1)
					result += new String(buf, 0, read);
			} catch (IOException err) {
				throw new RunException("Cannot read stdout: " + err);
			}
			
			return new Text(result);
		}));

		register(new FuncInner('Q', 1, (Value[] args) -> {
			System.exit((int) args[0].toLong());
			return null; // just to appease the type checker
		}));

		register(new FuncInner('!', 1, (Value[] args) -> new Bool(!args[0].toBoolean())));
		register(new FuncInner('L', 1, (Value[] args) -> new Number(args[0].toString().length())));

		register(new FuncInner('D', 1, (Value[] args) -> {
			Value arg = args[0].run();
			arg.dump();
			System.out.println(); // for trailing newline.
			return arg;
		}));

		register(new FuncInner('O', 1, (Value[] args) -> {
			String str = args[0].toString();
			
			if (str.isEmpty()) {
				System.out.println();
			} else if (str.charAt(str.length() - 1) == '\\') {
				System.out.print(str.substring(0, str.length() - 1));
				System.out.flush();
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
		register(new FuncInner('?', 2, (Value[] args) -> new Bool(args[0].run().equals(args[1].run()))));

		register(new FuncInner('<', 2, (Value[] args) -> new Bool(args[0].compareTo(args[1]) < 0)));
		register(new FuncInner('>', 2, (Value[] args) -> new Bool(args[0].compareTo(args[1]) > 0)));
		register(new FuncInner('&', 2, (Value[] args) -> {
			Value lhs = args[0].run();
			
			return lhs.toBoolean() ? args[1].run() : lhs;
		}));

		register(new FuncInner('|', 2, (Value[] args) -> {
			Value lhs = args[0].run();

			return lhs.toBoolean() ? lhs : args[1].run();
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
			String str = args[0].toString();
			int start = (int) args[1].toLong();
			int length = (int) args[2].toLong();

			if (str.isEmpty()) return new Text("");

			return new Text(str.substring(start, start + length));
		}));

		// arity four
		register(new FuncInner('S', 4, (Value[] args) -> {
			String str = args[0].toString();
			int start = (int) args[1].toLong();
			int length = (int) args[2].toLong();
			String repl = args[3].toString();

			return new Text(str.substring(0, start) + repl + str.substring(start + length));

		}));
	}
}
