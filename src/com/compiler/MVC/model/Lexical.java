package com.compiler.MVC.model;

import java.util.*;

import com.compiler.utils.Error;
import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import com.compiler.utils.Position;

public class Lexical {
    private static final Map<String, Token> tokenMap = Map.ofEntries(
            Map.entry("+", Token.PLUS), Map.entry("-", Token.MINUS),
            Map.entry("/", Token.DIVIDE), Map.entry("*", Token.TIMES),
            Map.entry(">", Token.GREATER), Map.entry("<", Token.LESS),
            Map.entry(">=", Token.GREATER_EQUAL), Map.entry("<=", Token.LESS_EQUAL),
            Map.entry("==", Token.EQUAL), Map.entry("<>", Token.NOT_EQUAL),
            Map.entry("(", Token.LEFT_PAREN), Map.entry(")", Token.RIGHT_PAREN),
            Map.entry("{", Token.LEFT_BRACE), Map.entry("}", Token.RIGHT_BRACE),
            Map.entry(";", Token.SEMICOLON), Map.entry("=", Token.ASSIGN)
    );

    private static final Map<String, Token> reservedWordMap = Map.ofEntries(
            Map.entry("int", Token.RW), Map.entry("double", Token.RW),
            Map.entry("string", Token.RW), Map.entry("print", Token.RW),
            Map.entry("read", Token.RW), Map.entry("START", Token.RW),
            Map.entry("ENDE", Token.RW), Map.entry("if", Token.RW),
            Map.entry("else", Token.RW)
    );

    private List<Token> tokens;
    private List<Error> errors;
    private List<Simbol> simbols;

    public Lexical() {
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        simbols = new ArrayList<>();
    }

    public void analyze(String code) {
        tokens.clear();
        errors.clear();
        simbols.clear();

        int row = 1, column = 1, length = code.length(), i = 0;

        while (i < length) {
            char currentChar = code.charAt(i);
            //verificamos si hay salto de linea para pasar a la siguiente fila
            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') { row++; column = 1; }
                else { column++; }
                i++;
                continue;
            }
            //obtenemos un token de dos caracteres si es posible y verificamos si lo tenemos en el mapa
            String doubleCharToken = (i + 1 < length) ? code.substring(i, i + 2) : "";
            if (tokenMap.containsKey(doubleCharToken)) {
                addToken(tokenMap.get(doubleCharToken), doubleCharToken, new Position(row, column));
                i += 2;
                column += 2;
                continue;
            }
            //obtenemos un token de un caracter y verificamos si lo tenemos en el mapa
            String singleCharToken = String.valueOf(currentChar);
            if (tokenMap.containsKey(singleCharToken)) {
                addToken(tokenMap.get(singleCharToken), singleCharToken, new Position(row, column));
                i++;
                column++;
                continue;
            }
            //verificamos si es un numero
            if (Character.isDigit(currentChar)) {
                int currentI = i;
                i = processNumber(code, i, row, column);
                column += (i - currentI);
                continue;
            }
            //verificamos si es un identificador o palabra reservada
            if (Character.isLetter(currentChar)) {
                int currentI = i;
                i = processIdentifier(code, i, row, column);
                column += (i - currentI);
                continue;
            }
            //verificamos si es un string
            if (currentChar == '"') {
                int currentI = i;
                i = processString(code, i, row, column);
                column += (i - currentI);
                continue;
            }

            errors.add(new Error(Token.ERROR, new Position(row, column), singleCharToken));
            addToken(Token.ERROR, singleCharToken, new Position(row, column));
            i++;
            column++;
        }
    }

    private int processNumber(String code, int i, int row, int column) {
        StringBuilder number = new StringBuilder();
        boolean isFraction = false;

        while (i < code.length() && (Character.isDigit(code.charAt(i)) || code.charAt(i) == '.')) {
            if (code.charAt(i) == '.') {
                if (isFraction) break;
                isFraction = true;
            }
            number.append(code.charAt(i));
            i++;
        }

        Token token = isFraction ? Token.FRACTION : Token.NUMBER;
        addToken(token, number.toString(), new Position(row, column));
        return i;
    }

    private int processIdentifier(String code, int i, int row, int column) {
        StringBuilder identifier = new StringBuilder();

        while (i < code.length() && (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
            identifier.append(code.charAt(i));
            i++;
        }

        String id = identifier.toString();
        Token token = reservedWordMap.getOrDefault(id, Token.IDENTIFIER);
        addToken(token, id, new Position(row, column));
        return i;
    }

    private int processString(String code, int i, int row, int column) {
        StringBuilder stringValue = new StringBuilder();
        i++;

        while (i < code.length() && code.charAt(i) != '"') {
            stringValue.append(code.charAt(i));
            i++;
        }

        if (i < code.length() && code.charAt(i) == '"') i++;
        addToken(Token.STRING_VALUE, stringValue.toString(), new Position(row, column));
        return i;
    }

    private void addToken(Token token, String value, Position position) {
        tokens.add(token);
        simbols.add(new Simbol(token, value, position));
    }

    public void printTokens() {
        tokens.forEach(System.out::println);
    }

    public void printSimbols() {
        simbols.forEach(simbol -> System.out.println(simbol.getTokenType() + " " + simbol.getValue()));
    }

    public List<Simbol> getSimbols() { return simbols; }
    public List<Error> getErrors() { return errors; }
    public void clearSimbols() { simbols.clear(); }
    public List<Token> getTokens() { return tokens; }
}
