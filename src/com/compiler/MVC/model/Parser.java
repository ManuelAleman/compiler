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
        currentToken = (++index < tokens.size()) ? tokens.get(index) : null;
    }

    private boolean match(Token expected) {
        return currentToken != null && currentToken.getTokenType() == expected;
    }

    public boolean parse() {
        if (currentToken == null) {
            reportError("No tokens to parse.");
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
            } else {
                reportError("'ENDE' was expected.");
            }
        } else {
            reportError("'START' was expected.");
        }
    }


    private void declarationList() {
        while (currentToken != null && !(match(Token.RW) && "ENDE".equals(currentToken.getValue()))) {
            int currentIndex = index;
            declaration();
            if (currentIndex == index) {
                next();
            }
        }
    }

    private void declaration() {
        if (currentToken == null) return;

        if (match(Token.IDENTIFIER)) {
            next();
            if (match(Token.ASSIGN)) {
                next();
                expression();
                if (match(Token.SEMICOLON)) {
                    next();
                } else {
                    reportError("';' expected after assignment.");
                }
            } else {
                reportError("'=' expected after identifier.");
            }
        } else if (match(Token.RW)) {
            switch (currentToken.getValue()) {
                case "int":
                case "double":
                case "string":
                    next();
                    if (!match(Token.IDENTIFIER)) {
                        reportError("Identifier expected after type");
                        return;
                    }
                    next();
                    if (match(Token.SEMICOLON)) {
                        next();
                    } else {
                        reportError("';' expected after type declaration");
                    }
                    break;
                case "if":
                    next();
                    ifDeclaration();
                    break;
                case "print":
                    next();
                    printBlock();
                    if (match(Token.SEMICOLON)) {
                        next();
                    } else {
                        reportError("';' expected after print statement");
                    }
                    break;
                default:
                    reportError("Invalid declaration.");

            }
        }

    }

    private void expression() {
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION)) {
            next();
            if (isArithOperator(currentToken)) {
                next();
                expression();
            }
        } else {
            reportError("Invalid expression.");
        }
    }

    private void ifDeclaration() {
        if (!match(Token.LEFT_PAREN)) {
            reportError("Missing '(' after if");
            return;
        }
        next();
        comparation();
        if (!match(Token.RIGHT_PAREN)) {
            reportError("Missing ')' after if condition");
            return;
        }
        next();
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
            reportError("Missing comparison operator");
        }
    }

    private void ifExpression() {
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION)) {
            next();
        } else {
            reportError("Invalid expression in if");
        }
    }
    private void parseBlock() {
        if (!match(Token.LEFT_BRACE)) {
            reportError("Missing '{' after condition");
            return;
        }
        next();
        while (currentToken != null && !match(Token.RIGHT_BRACE)) {
            int currentIndex = index;
            declaration();
            if (currentIndex == index) {
                next();
            }
            if (currentToken == null) {
                reportError("Unexpected end of input, '}' expected.");
                return;
            }
        }
        if (!match(Token.RIGHT_BRACE)) {
            reportError("Missing '}' after block");
        } else {
            next();
        }
    }


    private void printBlock() {
        if (!match(Token.LEFT_PAREN)) {
            reportError("Missing '(' after print");
            return;
        }
        next();

        if (match(Token.IDENTIFIER) || match(Token.STRING_VALUE)) {
            next();
        }

        if (!match(Token.RIGHT_PAREN)) {
            reportError("Missing ')' after print argument");
            return;
        }
        next();
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

    private void reportError(String message) {
        hasError = true;
        if (currentToken != null) {
            errorMessages.add("Error at line " + currentToken.getPosition() + ": " + message);
        } else {
            errorMessages.add("Error: " + message);
        }
    }

    public List<String> getErrors() {
        return errorMessages;
    }
}
