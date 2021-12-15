package fr.umlv.json;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IncompleteJSONParser {
	private enum Kind {
		STRING("\"([^\\\"]*)\""),
		INTEGER("([0-9]+)"),
		LEFT_CURLY("(\\{)"),
		RIGHT_CURLY("(\\})"),
		COLON("(\\:)"),
		COMMA("(\\,)"),
		BLANK("([ \t]+)")
		;

		private final String regex;

		Kind(String regex) {
			this.regex = regex;
		}

		private final static Kind[] VALUES = values();
	}

	private record Token(Kind kind, String text, int location) {
		private boolean is(Kind kind) {
			return this.kind == kind;
		}

		private String expect(Kind kind) {
			if (this.kind != kind) {
				throw new IllegalStateException("expect " + kind + " but recognized " + this.kind + " at " + location);
			}
			return text;
		}
	}

	private record Lexer(Matcher matcher) {
		private Token next() {
			for(;;) {
				if (!matcher.find()) {
					throw new IllegalStateException("no token recognized");
				}
				var index = rangeClosed(1, matcher.groupCount()).filter(i -> matcher.group(i) != null).findFirst().orElseThrow();
				var kind = Kind.VALUES[index - 1];
				if (kind != Kind.BLANK) {
					return new Token(kind, matcher.group(index), matcher.start(index));
				}
			}
		}
	}

	private static final Pattern PATTERN = compile(Arrays.stream(Kind.VALUES).map(k -> k.regex).collect(joining("|")));

	public static Map<String, Object> parse(String input) {
		try {
		  return parseText(input);
		} catch(IllegalStateException e) {
			throw new IllegalStateException(e.getMessage() + "\n while parsing " + input, e);
		}
	}

	private static HashMap<String, Object> parseText(String input) {
		var map = new HashMap<String, Object>();
		var lexer = new Lexer(PATTERN.matcher(input));
		lexer.next().expect(Kind.LEFT_CURLY);
		var token = lexer.next();
		if (token.is(Kind.RIGHT_CURLY)) {
			return map;
		}
		for(;;) {
			var key = token.expect(Kind.STRING);
			lexer.next().expect(Kind.COLON);
			token = lexer.next();
			var value = token.is(Kind.INTEGER)
				? parseInt(token.text)
				: token.expect(Kind.STRING);
			map.put(key, value);
			token = lexer.next();
			if (token.is(Kind.RIGHT_CURLY)) {
				return map;
			}
			token.expect(Kind.COMMA);
			token = lexer.next();
		}
	}

	public static void main(String[] args) {
		System.out.println(parse("{}"));
		System.out.println(parse("{ }"));
		System.out.println(parse("{ \"foo\": \"bar\" }"));
		System.out.println(parse("{ \"foo\": \"bar\", \"bob-one\": 42 }"));
	}
}