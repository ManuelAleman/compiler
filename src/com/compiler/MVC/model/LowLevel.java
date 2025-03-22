package com.compiler.MVC.model;

import com.compiler.utils.LowLevelTemplate;
import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import com.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.List;

public class LowLevel {
    private StringBuilder lowLevelCode;
    private StringBuilder auxProcedures;
    private List<Variable> variables;
    private List<Simbol> tokens;
    private Simbol currentToken;
    private int index;

    public LowLevel() {
        variables = new ArrayList<>();
        tokens = new ArrayList<>();
        lowLevelCode = new StringBuilder();
        auxProcedures = new StringBuilder();
    }

    public void prepareLowLevel(List<Variable> variables, List<Simbol> tokens) {
        this.variables = variables;
        this.tokens = tokens;
        index = 0;
        currentToken = tokens.isEmpty() ? null : tokens.get(index);
        lowLevelCode = new StringBuilder();
        auxProcedures = new StringBuilder();
    }

    public void analizeLowLevel() {
        lowLevelCode.append(".model small\n")
                .append(".stack 100h\n");

        generateDataSegment();
        generateCodeSegment();
    }

    private void generateDataSegment() {
        lowLevelCode.append(".DATA\n");
        variables.forEach(variable ->
                lowLevelCode.append(LowLevelTemplate.dataTemplate(variable, 1))
        );
    }

    private void generateCodeSegment() {
        lowLevelCode.append(".CODE\n")
                .append("main:\n")
                .append(LowLevelTemplate.movTemplate("AX", "@DATA", 1))
                .append(LowLevelTemplate.movTemplate("DS", "AX", 1)).append("\n");

        while (currentToken != null) {
            processToken();
            next();
        }

        lowLevelCode.append(auxProcedures)
                .append(LowLevelTemplate.endOfProgram(1))
                .append("\nEND main");
    }

    private void processToken() {
        if (match(Token.LEFT_PAREN) || match(Token.RIGHT_PAREN)) {
            next();
            return;
        }
        if (match(Token.RW)) {
            String value = currentToken.getValue();
            if (isType(value)) {
                consumeLine();
            } else if ("print".equals(value)) {
                next();
                next();
                if(match(Token.STRING_VALUE)){
                    lowLevelCode.append(LowLevelTemplate.printStringTemplate(currentToken.getValue(), 1));
                }else{
                    lowLevelCode.append(LowLevelTemplate.prinIdTemplate(currentToken.getValue(), 1));
                }
            }
        } else if (match(Token.IDENTIFIER)) {
            processAssignment();
        }
    }

    private void processAssignment() {
        Variable variable = getVariable(currentToken.getValue());
        next();
        next();
        if(match(Token.STRING_VALUE)){
            lowLevelCode.append(LowLevelTemplate.stringAssigmentTemplate(variable.getName(), currentToken.getValue(), 1));
        }else {
            String value = variable.getValue();
            lowLevelCode.append(LowLevelTemplate.expressionAssigmentTemplate(variable.getName(), value, 1)).append("\n");
        }
        consumeLine();
    }


    private void next() {
        index++;
        currentToken = index < tokens.size() ? tokens.get(index) : null;
    }

    private boolean match(Token expect) {
        return currentToken != null && currentToken.getTokenType() == expect;
    }

    private void consumeLine() {
        while (currentToken != null && currentToken.getTokenType() != Token.SEMICOLON) {
            next();
        }

    }

    private boolean isType(String value) {
        return "int".equals(value) || "double".equals(value) || "string".equals(value);
    }

    public Variable getVariable(String varName){
        for (Variable variable : variables) {
            if (variable.getName().equals(varName)) {
                return variable;
            }
        }
        return null;
    }
    public String getLowLevelCode() {
        return lowLevelCode.toString();
    }

    public void clearLowLevel() {
        lowLevelCode = new StringBuilder();
    }
}
