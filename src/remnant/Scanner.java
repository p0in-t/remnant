package remnant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static remnant.TokenType.*;


public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private static final Map<String, TokenType> keywords;
	private int start = 0;
	private int current = 0;
	private int line = 1;
	
	static {
		keywords = new HashMap<>();
		
		keywords.put("var", VAR);
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("true", TRUE);
		keywords.put("while", WHILE);
		keywords.put("for", FOR);
		keywords.put("return", RETURN);
		keywords.put("output", OUTPUT);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("null", NULL);
		keywords.put("class", CLASS);
		keywords.put("fn", FUNCTION);
	}
	
	Scanner(String source) {
		this.source = source;
	}
	
	List<Token> getTokens() {
		while (!isAtEnd()) {
			start = current;
			getToken();
		}
		
		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}
	
	private void getToken() {
		char ch = advance();
		
		switch (ch) {
			case '(': addToken(LEFT_PARENTHESIS); break;
			case ')': addToken(RIGHT_PARENTHESIS); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
			case '&': addToken(matchForward('&') ? LOGICAL_AND : BITWISE_AND); break;
			case '|': addToken(matchForward('|') ? LOGICAL_OR : BITWISE_OR); break;
			case '!': addToken(matchForward('=') ? NOT_EQUAL : NOT); break;
			case '=': addToken(matchForward('=') ? EQUAL_EQUAL : EQUAL); break;
			case '<': addToken(matchForward('=') ? LESS_EQUAL : LESS); break;
			case '>': addToken(matchForward('=') ? GREATER_EQUAL : GREATER); break;
			case '/':
				if (matchForward('/')) {
					while (peek() != '\n' && !isAtEnd())
						advance();
				}
				else if (matchForward('*')) {
					while ((peek() != '*' && peekNext() != '/' ) && !isAtEnd())
						advance();
					
					advance();
					advance();
				}
				else {
					addToken(SLASH);
				} break;
			case ' ':
			case '\r':
			case '\t': break;
			case '\n': line++; break;
			case '"': string(); break;
			default: {
				if (Character.isDigit(ch))
					number();
				else if (isAlpha(ch))
					identifier();
				else
					Remnant.error(line, "Invalid character.");
			} break;
		}
	}
	
	private boolean isAlpha(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}
	
	private boolean isAlphaNumeric(char ch) {
		return isAlpha(ch) || Character.isDigit(ch);
	}
	
	private void identifier() {
		while (isAlphaNumeric(peek()))
			advance();
		
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		
		if (type == null)
			type = IDENTIFIER;
		
		addToken(type);
	}
	
	private void number() {
		while (Character.isDigit(peek())) 
			advance();
		
		if (peek() == '.' && Character.isDigit(peekNext())) {
			advance();
			
			while (Character.isDigit(peek()))
				advance();
		}
		
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}
	
	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if (peek() == '\n')
				line++;
			
			advance();
		}
		
		if (isAtEnd()) {
			Remnant.error(line, "Undetermined string.");
			return;
		}
		
		advance();
		
		String literal = source.substring(start + 1, current - 1);
		addToken(STRING, literal);
	}
	
	private boolean matchForward(char ch) {
		if (isAtEnd())
			return false;
		
		if (source.charAt(current) != ch)
			return false;
		
		current++;
		
		return true;
	}
	
	private char peek() {
		if (isAtEnd())
			return '\0';
			
		return source.charAt(current);
	}
	
	private char peekNext() {
		if (current + 1 >= source.length())
			return '\0';
		
		return source.charAt(current + 1);
	}
	
	private boolean isAtEnd() {
		return current >= source.length();
	}
	
	private char advance() {
		current++;
		return source.charAt(current-1);
	}
	
	private void addToken(TokenType type) {
		addToken(type, null);
	}
	
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		start = current;
		tokens.add(new Token(type, text, literal, line));
	}
}
