package com.compiler;

import java.util.ArrayList;
import java.util.HashMap;

import com.compiler.utils.Pair;
import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import com.compiler.utils.Position;

public class Lexical {
    private final HashMap<String, Token> tokenMap;
    private ArrayList<Token> tokens;
    private ArrayList<Pair> pairs, errors;
    private ArrayList<Simbol> simbols;

    public Lexical() {
        tokenMap = new HashMap<>();
        simbols = new ArrayList<>();
        tokenMap.put("START", Token.START);

        tokenMap.put("int", Token.INT);
        tokenMap.put("double", Token.DOUBLE);
        tokenMap.put("string", Token.STRING);

        tokenMap.put("+", Token.PLUS);
        tokenMap.put("-", Token.MINUS);
        tokenMap.put("/", Token.DIVIDE);
        tokenMap.put("*", Token.TIMES);

        tokenMap.put(">", Token.GREATER);
        tokenMap.put("<", Token.LESS);
        tokenMap.put(">=", Token.GREATER_EQUAL);
        tokenMap.put("<=", Token.LESS_EQUAL);
        tokenMap.put("==", Token.EQUAL);
        tokenMap.put("<>", Token.NOT_EQUAL);

        tokenMap.put("(", Token.LEFT_PAREN);
        tokenMap.put(")", Token.RIGHT_PAREN);
        tokenMap.put("{", Token.LEFT_BRACE);
        tokenMap.put("}", Token.RIGHT_BRACE);
        tokenMap.put(";", Token.SEMICOLON);
        tokenMap.put("=", Token.ASSIGN);
        tokenMap.put(".", Token.DOT);
        tokenMap.put(",", Token.COMMA);

        tokenMap.put("if", Token.IF);
        tokenMap.put("else", Token.ELSE);

        tokenMap.put("read", Token.READ);
        tokenMap.put("print", Token.PRINT);

        tokenMap.put("END", Token.END);
        tokenMap.put("ERROR", Token.ERROR);
    }

    public void analyze(String code) {
        tokens = new ArrayList<>();
        pairs = new ArrayList<>();
        errors = new ArrayList<>();

        int row = 1;
        int column = 1;
        int length = code.length();
        int i = 0;

        while (i < length) {
            char currentChar = code.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') {
                    row++;
                    column = 1;
                } else {
                    column++;
                }
                i++;
                continue;
            }

            if (tokenMap.containsKey(String.valueOf(currentChar))) {
                Token token = tokenMap.get(String.valueOf(currentChar));
                tokens.add(token);
                pairs.add(new Pair(token, new Position(row, column)));
                simbols.add(new Simbol(token, String.valueOf(currentChar)));
                i++;
                column++;
                continue;
            }

            if (i + 1 < length) {
                String doubleCharToken = code.substring(i, i + 2);
                if (tokenMap.containsKey(doubleCharToken)) {
                    Token token = tokenMap.get(doubleCharToken);
                    tokens.add(token);
                    pairs.add(new Pair(token, new Position(row, column)));
                    simbols.add(new Simbol(token, doubleCharToken));
                    i += 2;
                    column += 2;
                    continue;
                }
            }

            if (Character.isDigit(currentChar)) {
                StringBuilder number = new StringBuilder();
                boolean isFraction = false;

                while (i < length && (Character.isDigit(code.charAt(i)) || code.charAt(i) == '.')) {
                    if (code.charAt(i) == '.') {
                        if (isFraction) {
                            errors.add(new Pair(Token.ERROR, new Position(row, column)));
                            break;
                        }
                        isFraction = true;
                    }
                    number.append(code.charAt(i));
                    i++;
                    column++;
                }

                if (isFraction) {
                    tokens.add(Token.FRACTION);
                    pairs.add(new Pair(Token.FRACTION, new Position(row, column - number.length())));
                    simbols.add(new Simbol(Token.FRACTION, number.toString()));
                } else {
                    tokens.add(Token.NUMBER);
                    pairs.add(new Pair(Token.NUMBER, new Position(row, column - number.length())));
                    simbols.add(new Simbol(Token.NUMBER, number.toString()));
                }
                continue;
            }

            if (Character.isLetter(currentChar)) {
                StringBuilder identifier = new StringBuilder();
                while (i < length && (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
                    identifier.append(code.charAt(i));
                    i++;
                    column++;
                }
                String id = identifier.toString();
                if (isReservedWord(id)) {
                    Token token = tokenMap.get(id);
                    tokens.add(token);
                    pairs.add(new Pair(token, new Position(row, column - id.length())));
                    simbols.add(new Simbol(token, id));
                } else {
                    tokens.add(Token.IDENTIFIER);
                    pairs.add(new Pair(Token.IDENTIFIER, new Position(row, column - id.length())));
                    simbols.add(new Simbol(Token.IDENTIFIER, id));
                }
                continue;
            }

            if (currentChar == '"') {
                StringBuilder stringValue = new StringBuilder();
                i++;
                column++;
                while (i < length && code.charAt(i) != '"') {
                    stringValue.append(code.charAt(i));
                    i++;
                    column++;
                }
                if (i < length && code.charAt(i) == '"') {
                    i++;
                    column++;
                } else {
                    errors.add(new Pair(Token.ERROR, new Position(row, column)));
                }
                tokens.add(Token.STRING_VALUE);
                pairs.add(new Pair(Token.STRING_VALUE, new Position(row, column - stringValue.length() - 2)));
                simbols.add(new Simbol(Token.STRING_VALUE, stringValue.toString()));
                continue;
            }

            errors.add(new Pair(Token.ERROR, new Position(row, column)));

            i++;
            column++;
        }
    }

    private boolean isReservedWord(String word) {
        return tokenMap.containsKey(word);
    }

    public void printTokens() {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public void printPairs() {
        for (Pair pair : pairs) {
            System.out.println(pair.getToken() + " " + pair.getPosition().getRow() + " " + pair.getPosition().getColumn());
        }
    }

    public void printErrors() {
        for (Pair error : errors) {
            System.out.println(error.getToken() + " " + error.getPosition().getRow() + " " + error.getPosition().getColumn());
        }
    }

    public void printSimbols() {
        for (Simbol simbol : simbols) {
            System.out.println(simbol.getTokenType() + " " + simbol.getValue());
        }
    }
}
