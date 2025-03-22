package com.compiler.MVC.model;

import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Simbol> tokens;
    private int index;
    private Simbol currentToken;
    private boolean hasError;
    private String errorMessage;

    public Parser() {
        tokens = new ArrayList<>();
        errorMessage = "";
    }

    public void setTokens(List<Simbol> tokens) {
        this.tokens = tokens;
        index = 0;
        currentToken = tokens.isEmpty() ? null : tokens.get(index);
        hasError = false;
        errorMessage = "";
    }

    private void next() {
        if (!hasError) {
            index++;
            currentToken = index < tokens.size() ? tokens.get(index) : null;
        }
    }

    private boolean match(Token expected) {
        return currentToken != null && currentToken.getTokenType() == expected;
    }

    private void expect(Token expected, String message) {
        if (hasError) return;
        if (!match(expected)) {
            reportError(message);
        }
        next();
    }

    private void consumeDeclaration() {
        int currentIndex = index;
        declaration();
        if (currentIndex == index) {
            next();
        }
    }

    public boolean parse() {
        if (currentToken == null) {
            reportError("inicio del programa");
            return false;
        }
        program();
        return !hasError;
    }

    private void program() {
        if (match(Token.RW) && "START".equals(currentToken.getValue())) {
            next();
            declarationList();
            if (match(Token.RW) && "ENDE".equals(currentToken.getValue())) {
                next();
                if(currentToken!= null){
                    reportError("No debe haber código después de 'ENDE'.");
                }
            } else {
                reportError("'ENDE'");
            }
        } else {
            reportError("'START'");
        }
    }

    private void declarationList() {
        while (!hasError && currentToken != null && !(match(Token.RW) && "ENDE".equals(currentToken.getValue()))) {
            consumeDeclaration();
        }
    }

    private void declaration() {
        if (currentToken == null) return;
        if (match(Token.IDENTIFIER)) {
            next();
            expect(Token.ASSIGN, "'='");
            expression();
            expect(Token.SEMICOLON, "';'");
        } else if (match(Token.RW)) {
            switch (currentToken.getValue()) {
                case "int":
                case "double":
                case "string":
                    next();
                    expect(Token.IDENTIFIER, "identifier");
                    expect(Token.SEMICOLON, "';'");
                    break;
                case "if":
                    next();
                    ifDeclaration();
                    break;
                case "print":
                    next();
                    printBlock();
                    expect(Token.SEMICOLON, "';'");
                    break;
                case "read":
                    next();
                    readBlock();
                    expect(Token.SEMICOLON, "';'");
                    break;
                default:
                    reportError("declaration");
            }
        } else {
            reportError("declaration");
        }
    }

    private void expression() {
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) ||
                match(Token.FRACTION) || match(Token.STRING_VALUE)) {
            next();
            if (isArithOperator(currentToken)) {
                next();
                expression();
            }
        } else if(match(Token.LEFT_PAREN)){
            next();
            expression();
            expect(Token.RIGHT_PAREN, "')'");
        }
        else {
            reportError("expression");
        }
    }

    private void ifDeclaration() {
        expect(Token.LEFT_PAREN, "'('");
        comparation();
        expect(Token.RIGHT_PAREN, "')'");
        parseBlock();
        if (match(Token.RW) && "else".equals(currentToken.getValue())) {
            next();
            parseBlock();
        }
    }

    private void comparation() {
        ifExpression();
        if (isCompOperator(currentToken)) {
            next();
            ifExpression();
        } else {
            reportError("comparison operator");
        }
    }

    private void ifExpression() {
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION)) {
            next();
        } else {
            reportError("expression");
        }
    }

    private void parseBlock() {
        expect(Token.LEFT_BRACE, "'{'");
        while (!hasError && currentToken != null && !match(Token.RIGHT_BRACE)) {
            consumeDeclaration();
            if (currentToken == null) {
                reportError("'}'");
                return;
            }
        }
        expect(Token.RIGHT_BRACE, "'}'");
    }

    private void printBlock() {
        expect(Token.LEFT_PAREN, "'('");
        if (match(Token.IDENTIFIER)) {
            next();
        } else {
            reportError("identifier");
        }
        expect(Token.RIGHT_PAREN, "')'");
    }

    private void readBlock() {
        expect(Token.LEFT_PAREN, "'('");
        expect(Token.IDENTIFIER, "identifier");
        expect(Token.RIGHT_PAREN, "')'");
    }

    private boolean isArithOperator(Simbol token) {
        if (token == null) return false;
        return switch (token.getTokenType()) {
            case PLUS, MINUS, TIMES, DIVIDE -> true;
            default -> false;
        };
    }

    private boolean isCompOperator(Simbol token) {
        if (token == null) return false;
        return switch (token.getTokenType()) {
            case EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL -> true;
            default -> false;
        };
    }

    private void reportError(String expected) {
        if (hasError) return;
        hasError = true;
        if (currentToken == null) {
            errorMessage = "Error: Fin de entrada inesperado, se esperaba " + expected + ".";
        } else {
            errorMessage = "Error en línea " + currentToken.getPosition().getRow() +
                    ": se esperaba " + expected + " pero se encontró '" + currentToken.getValue() + "'.";
        }
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
