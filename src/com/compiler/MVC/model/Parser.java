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
    private List<String> errorMessages;

    public Parser() {
        tokens = new ArrayList<>();
        errorMessages = new ArrayList<>();
    }

    public void setTokens(List<Simbol> tokens) {
        this.tokens = tokens;
        index = 0;
        currentToken = tokens.isEmpty() ? null : tokens.get(index);
        hasError = false;
        errorMessages.clear();
    }

    private void next() {
        if (!hasError) {
            currentToken = (++index < tokens.size()) ? tokens.get(index) : null;
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

    public boolean parse() {
        if (currentToken == null) {
            reportError("inicio del programa");
            return false;
        }
        program();
        return !hasError;
    }

    private void program() {
        if (hasError) return;
        if (match(Token.RW) && "START".equals(currentToken.getValue())) {
            next();
            declarationList();
            if (hasError) return;
            if (match(Token.RW) && "ENDE".equals(currentToken.getValue())) {
                next();
            } else {
                reportError("'ENDE'");
            }
        } else {
            reportError("'START'");
        }
    }

    private void declarationList() {
        while (!hasError && currentToken != null && !(match(Token.RW) && "ENDE".equals(currentToken.getValue()))) {
            int currentIndex = index;
            declaration();
            if (currentIndex == index) {
                next();
            }
        }
    }

    private void declaration() {
        if (hasError || currentToken == null) return;

        if (match(Token.IDENTIFIER)) {
            next();
            expect(Token.ASSIGN, "'='");
            if (!hasError) expression();
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
                    reportError("declaration ");
            }
        } else {
            reportError("declaration");
        }
    }

    private void expression() {
        if (hasError) return;
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION) || match(Token.STRING_VALUE)) {
            next();
            if (!hasError && isArithOperator(currentToken)) {
                next();
                expression();
            }
        } else {
            reportError("expression");
        }
    }

    private void ifDeclaration() {
        if (hasError) return;
        expect(Token.LEFT_PAREN, "'('");
        if (!hasError) comparation();
        expect(Token.RIGHT_PAREN, "')'");
        if (!hasError) parseBlock();

        if (!hasError && match(Token.RW) && "else".equals(currentToken.getValue())) {
            next();
            parseBlock();
        }
    }

    private void comparation() {
        if (hasError) return;
        ifExpression();
        if (!hasError && isCompOperator(currentToken)) {
            next();
            ifExpression();
        } else {
            reportError("comparison operator");
        }
    }

    private void ifExpression() {
        if (hasError) return;
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION)) {
            next();
        } else {
            reportError("expression");
        }
    }

    private void parseBlock() {
        if (hasError) return;
        expect(Token.LEFT_BRACE, "'{'");
        while (!hasError && currentToken != null && !match(Token.RIGHT_BRACE)) {
            int currentIndex = index;
            declaration();
            if (currentIndex == index) next();
            if (currentToken == null) {
                reportError("'}'");
                return;
            }
        }
        expect(Token.RIGHT_BRACE, "'}'");
    }

    private void printBlock() {
        if (hasError) return;
        expect(Token.LEFT_PAREN, "'('");
        if (!hasError && (match(Token.IDENTIFIER) || match(Token.STRING_VALUE))) next();
        expect(Token.RIGHT_PAREN, "')'");
    }

    private void readBlock() {
        if (hasError) return;
        expect(Token.LEFT_PAREN, "'('");
        expect(Token.IDENTIFIER, "identifier");
        expect(Token.RIGHT_PAREN, "')'");
    }

    private boolean isArithOperator(Simbol token) {
        return token != null && switch (token.getTokenType()) {
            case PLUS, MINUS, TIMES, DIVIDE -> true;
            default -> false;
        };
    }

    private boolean isCompOperator(Simbol token) {
        return token != null && switch (token.getTokenType()) {
            case EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL -> true;
            default -> false;
        };
    }

    private void reportError(String expected) {
        if (hasError) return;
        hasError = true;
        if (currentToken == null) {
            errorMessages.add("Error: Fin de entrada inesperado, se esperaba " + expected + ".");
        } else {
            errorMessages.add("Error en línea " + currentToken.getPosition().getRow() + ", columna " + currentToken.getPosition().getColumn() +
                    ": se esperaba " + expected + " pero se encontró '" + currentToken.getValue() + "'.");
        }
    }

    public List<String> getErrors() {
        return errorMessages;
    }
}
