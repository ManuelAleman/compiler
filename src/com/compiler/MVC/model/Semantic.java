package com.compiler.MVC.model;

import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import com.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Semantic {
    private List<Simbol> tokens;
    private boolean hasError;

    private List<Variable> variable;
    private Set<String> variableNames;

    private Simbol currentToken;
    private int index;

    private String errorMessage;

    public Semantic() {
        variable = new ArrayList<>();
        variableNames = new HashSet<>();
        tokens = new ArrayList<>();
    }

    public void setTokens(List<Simbol> tokens) {
        this.tokens = tokens;
        variable.clear();
        variableNames.clear();
        index = 1;
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

    public boolean analyzeSemantic() {
        if (currentToken == null) {
            reportError("NO PROGRAM TO ANALYZE");
            return false;
        }
        program();
        return !hasError;
    }

    private boolean match(Token expect) {
        return currentToken != null && currentToken.getTokenType() == expect;
    }

    private void program() {
        while (!hasError && currentToken != null) {
            if (match(Token.RW) && isType(currentToken.getValue())) {
                System.out.println("entrada 4");
                declareVariable();
            } else if (match(Token.IDENTIFIER)) {
                System.out.println("entrada 3");
                assignVariable();
            }else if (match(Token.RW) && "if".equals(currentToken.getValue())){
                System.out.println("entrada 2");
                ifSentence();
            }else if(match(Token.RW) && "print".equals(currentToken.getValue())){
                inOutSentence();
            }else if(match(Token.RW) && "read".equals(currentToken.getValue())){
                inOutSentence();
            }
            next();
        }
    }

    private void inOutSentence(){
        next();next();
        if(!variableNames.contains(currentToken.getValue())){
            reportError("Identificador '" + currentToken.getValue() + "' no declarado");
        }
    }

    private void ifSentence(){
        next();next();
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION) || match(Token.STRING_VALUE)) {
            if (match(Token.IDENTIFIER) && !variableNames.contains(currentToken.getValue())) {
                reportError("Identificador no declarado: " + currentToken.getValue());
                return;
            }
            next();
        }
       next();
        if (match(Token.IDENTIFIER) || match(Token.NUMBER) || match(Token.FRACTION) || match(Token.STRING_VALUE)) {
            if (match(Token.IDENTIFIER) && !variableNames.contains(currentToken.getValue())) {
                reportError("Identificador no declarado: " + currentToken.getValue());
                return;
            }
            next();
        }
    }

    private void declareVariable() {
        String type = currentToken.getValue();
        next();
        String name = currentToken.getValue();

        if (variableNames.contains(name)) {
            reportError("Variable declarada anteriormente: " + name);
            return;
        }
        byte bits = switch (type) {
            case "int" -> 32;
            case "double" -> 16;
            case "string" -> 8;
            default -> 8;
        };
        variable.add(new Variable(type, name, "", bits));
        variableNames.add(name);
    }

    private void assignVariable() {
        String name = currentToken.getValue();
        if (!variableNames.contains(name)) {
            reportError("Variable no declarada: " + name);
            return;
        }

        Variable variableToAssign = getVariableByName(name);
        if (variableToAssign == null) return;

        next(); next();

        String assignedValue = extractAssignmentValue(variableToAssign.getType());
        if (assignedValue == null) return;
        variableToAssign.setValue(assignedValue);
    }

    private String extractAssignmentValue(String expectedType) {
        StringBuilder value = new StringBuilder();
        boolean expectOperand = true;

        while (!match(Token.SEMICOLON) && currentToken != null) {
            if (expectOperand) {
                if (!isValidTypeOpe(expectedType)) {
                    reportError("Asignación inválida: se esperaba un valor de tipo " + expectedType);
                    return null;
                }
                expectOperand = false;
            } else {
                if (!isArithOperator(currentToken)) {
                    reportError("Asignación inválida: se esperaba un operador.");
                    return null;
                }
                expectOperand = true;
            }

            value.append(currentToken.getValue());
            next();
        }

        if (expectOperand) {
            reportError("Asignación inválida: expresión incompleta.");
            return null;
        }
        return value.toString().trim();
    }


    private Variable getVariableByName(String name) {
        return variable.stream()
                .filter(var -> var.getName().equals(name))
                .findFirst()
                .orElseGet(() -> {
                    reportError("Error interno: No se encontró la variable '" + name + "'");
                    return null;
                });
    }

    private boolean isValidTypeOpe(String expectedType) {
        return switch (expectedType) {
            case "int" -> match(Token.NUMBER);
            case "double" -> match(Token.FRACTION);
            case "string" -> match(Token.STRING_VALUE);
            default -> false;
        };
    }

    private boolean isType(String value) {
        return "int".equals(value) || "double".equals(value) || "string".equals(value);
    }

    private boolean isArithOperator(Simbol token) {
        if (token == null) return false;
        return switch (token.getTokenType()) {
            case PLUS, MINUS, TIMES, DIVIDE -> true;
            default -> false;
        };
    }
    private void reportError(String error) {
        if (hasError) return;
        hasError = true;
        errorMessage = error;
        System.out.println(errorMessage);
    }

    public List<Variable> getVariables() {
        return new ArrayList<>(variable);
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
