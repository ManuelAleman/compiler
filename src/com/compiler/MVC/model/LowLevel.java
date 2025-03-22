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
    private int labelCounter = 0;

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
        labelCounter = 0;
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
                lowLevelCode.append(LowLevelTemplate.dataTemplate(variable))
        );
    }

    private void generateCodeSegment() {
        lowLevelCode.append(".CODE\n")
                .append("main:\n")
                .append(LowLevelTemplate.movTemplate("AX", "@DATA"))
                .append(LowLevelTemplate.movTemplate("DS", "AX")).append("\n");

        while (currentToken != null) {
            processToken();
            next();
        }

        lowLevelCode.append(auxProcedures)
                .append(LowLevelTemplate.endOfProgram())
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
            } else if ("if".equals(value)) {
                generateIfTree();
            } else if ("print".equals(value)) {
                next();
                next();
                lowLevelCode.append(LowLevelTemplate.printTemplate(currentToken.getValue()));
            } else if ("ENDE".equals(value)) {
                lowLevelCode.append("   JMP ENDE0\n");
            }
        } else if (match(Token.IDENTIFIER)) {
            processAssignment();
        }
    }

    private void processAssignment() {
        String varName = currentToken.getValue();
        next();
        if (match(Token.ASSIGN)) {
            next();
            if (match(Token.FRACTION) || match(Token.NUMBER)) {
                lowLevelCode.append(LowLevelTemplate.movTemplate(varName, currentToken.getValue())).append("\n");
            } else if (match(Token.STRING_VALUE)) {
                lowLevelCode.append(LowLevelTemplate.stringAssigmentTemplate(varName, currentToken.getValue())).append("\n");
            }
            next();
        }
    }

    private void generateIfTree() {
        next();
        next();

        if (match(Token.IDENTIFIER)) {
            String varName = currentToken.getValue();
            next();
            String operator = currentToken.getValue();
            next();

            if (match(Token.NUMBER) || match(Token.FRACTION)) {
                String value = currentToken.getValue();
                lowLevelCode.append(LowLevelTemplate.movTemplate("AX", varName))
                        .append(LowLevelTemplate.movTemplate("BX", value))
                        .append("\tCMP AX, BX\n");

                appendConditionalJump(operator);
                lowLevelCode.append("ELSE").append(labelCounter).append("\n");
                lowLevelCode.append("\n");
                next();
                next();
                generateBlock("ELSE");
                lowLevelCode.append("\n");
                labelCounter++;
            }
        }
    }

    private void generateBlock(String procedureName) {
        auxProcedures.append(procedureName).append(labelCounter).append(":\n");
        while (currentToken != null && !match(Token.RIGHT_BRACE)) {
            if (match(Token.IDENTIFIER)) {
                processAssignment();
            } else if (match(Token.RW) && "if".equals(currentToken.getValue())) {
                generateIfTree();
            } else if (match(Token.RW) && "print".equals(currentToken.getValue())) {
                next();
                next();
                auxProcedures.append(LowLevelTemplate.printTemplate(currentToken.getValue()));
            }
            next();
        }
        next();
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

    private void appendConditionalJump(String operator) {
        switch (operator) {
            case "==":
                lowLevelCode.append("\tJE ");
                break;
            case "<>":
                lowLevelCode.append("\tJNE ");
                break;
            case ">":
                lowLevelCode.append("\tJG ");
                break;
            case "<":
                lowLevelCode.append("\tJL ");
                break;
            case ">=":
                lowLevelCode.append("\tJGE ");
                break;
            case "<=":
                lowLevelCode.append("\tJLE ");
                break;
            default:
                throw new IllegalArgumentException("Operador no soportado: " + operator);
        }

    }

    public String getLowLevelCode() {
        return lowLevelCode.toString();
    }

    public void clearLowLevel() {
        lowLevelCode = new StringBuilder();
    }
}
