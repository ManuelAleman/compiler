package com.compiler.MVC.model;

import com.compiler.utils.LowLevelTemplate;
import com.compiler.utils.Simbol;
import com.compiler.utils.Token;
import com.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.List;

public class LowLevel {
    private StringBuilder lowLevelCode;
    private List<Variable> variables;
    private List<Simbol> tokens;
    private Simbol currentToken;
    private int index;
    private int ifLabelCount;
    private int showLabelCount;
    private int readLabelCount;

    public LowLevel() {
        variables = new ArrayList<>();
        tokens = new ArrayList<>();
        lowLevelCode = new StringBuilder();
    }

    public void prepareLowLevel(List<Variable> variables, List<Simbol> tokens) {
        this.variables = variables;
        this.tokens = tokens;
        index = 0;
        ifLabelCount = 0;
        showLabelCount = 0;
        readLabelCount = 0;

        currentToken = tokens.isEmpty() ? null : tokens.get(index);
        lowLevelCode = new StringBuilder();
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
        lowLevelCode.append(String.format("%-20s %-3s %s", "new_line", "db", "0Dh, 0Ah, '$'")).append("\n");

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
        lowLevelCode.append(LowLevelTemplate.endOfProgram());
        lowLevelCode.append("\nEND main");

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
                Variable variable = getVariable(currentToken.getValue());
                if(variable.getType().equals("string")){
                    lowLevelCode.append(LowLevelTemplate.printStringTemplate(variable.getName(), 1));
                }else {
                    lowLevelCode.append(LowLevelTemplate.printNumberIdTemplate(variable.getName(), 1));
                    lowLevelCode.append(LowLevelTemplate.cicloTemplate(1, ++showLabelCount));
                    lowLevelCode.append(LowLevelTemplate.printNumber(1, showLabelCount));
                }
            } else if ("read".equals(value)) {
                next();
                next();
                Variable variable = getVariable(currentToken.getValue());
                if(variable.getType().equals("string")){
                    lowLevelCode.append(LowLevelTemplate.readStringTemplate(variable.getName(), 1, readLabelCount));
                }else{
                    lowLevelCode.append(LowLevelTemplate.readNumberTemplate(variable.getName(), 1, readLabelCount));
                }
            } else if( "if".equals(value)) {
                processIf();
            }
        } else if (match(Token.IDENTIFIER)) {
            processAssignment();
        }
    }

    private void processIf() {
        next();
        next();
        String val1 = currentToken.getValue();
        next();
        String op = currentToken.getValue();
        next();
        String val2 = currentToken.getValue();
        next();
        next();
        lowLevelCode.append(LowLevelTemplate.CompTemplate(val1, op, val2, 1, ifLabelCount));
        while (!match(Token.RIGHT_BRACE)) {
            next();
            processToken();
        }
        lowLevelCode.append("\t").append("JMP END_IF").append(ifLabelCount).append("\n");
        lowLevelCode.append("ELSE").append(ifLabelCount).append(":\n");
        next();
        if (match(Token.RW) && "else".equals(currentToken.getValue())) {
            next();
            next();
            while (!match(Token.RIGHT_BRACE)) {
                processToken();
                next();
            }
        }
        lowLevelCode.append("END_IF").append(ifLabelCount).append(":\n");
        ifLabelCount++;
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
