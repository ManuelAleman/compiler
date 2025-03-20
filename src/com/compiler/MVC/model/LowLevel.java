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
    private int labelCounter = 0;

    private void next() {
        index++;
        currentToken = index < tokens.size() ? tokens.get(index) : null;
    }
    private boolean match(Token expect) {
        return currentToken != null && currentToken.getTokenType() == expect;
    }
    public LowLevel(){
        variables = new ArrayList<>();
        tokens = new ArrayList<>();
        lowLevelCode = new StringBuilder();
    }
    public void prepareLowLevel(List<Variable> variables, List<Simbol> tokens) {
        this.variables = variables;
        this.tokens = tokens;
        index = 0;
        currentToken = tokens.isEmpty() ? null : tokens.get(index);
    }

    public void analizeLowLevel(){
        generateDataSegment();
        generateCodeSegment();
    }

    private void generateDataSegment(){
        lowLevelCode.append(".DATA\n");
        for (Variable variable : variables) {
            lowLevelCode.append(LowLevelTemplate.dataTemplate(variable));
        }
    }

    private void generateCodeSegment(){
        lowLevelCode.append(".CODE\n");
        while(currentToken != null){
            if(match(Token.LEFT_PAREN) || match(Token.RIGHT_PAREN)){
                next();
                continue;
            }
            if(match(Token.RW) && isType(currentToken.getValue())){
                consumeLine();
            }
            if(match(Token.IDENTIFIER)){
                String varName = currentToken.getValue();
                next();
                if(match(Token.ASSIGN)){
                    next();
                    if(match(Token.FRACTION)){
                        lowLevelCode.append(LowLevelTemplate.movTemplate(varName, currentToken.getValue()));
                    }
                    if(match(Token.NUMBER)){
                        lowLevelCode.append(LowLevelTemplate.movTemplate(varName, currentToken.getValue()));
                    }
                    if(match(Token.STRING_VALUE)){
                        lowLevelCode.append(LowLevelTemplate.stringAssigmentTemplate(varName, currentToken.getValue()));
                    }
                    next();
                }
            }
            if(match(Token.RW) && "if".equals(currentToken.getValue())){
                generateIfTree();
            }
            next();
        }
    }

    private void generateIfTree(){
        next();
        next();
        if(match(Token.IDENTIFIER)){
            String varName = currentToken.getValue();
            next();
            String operator = currentToken.getValue();
            next();
            if(match(Token.NUMBER) || match(Token.FRACTION)){
                String value = currentToken.getValue();
                lowLevelCode.append(LowLevelTemplate.movTemplate("AX", varName)).append("\n");
                lowLevelCode.append(LowLevelTemplate.movTemplate("BX", value)).append("\n");
                lowLevelCode.append("\tCMP AX, BX\n");
                if(operator.equals("==")){
                    lowLevelCode.append("\tJE ");
                }
                if(operator.equals("!=")){
                    lowLevelCode.append("\tJNE ");
                }
                if(operator.equals(">")){
                    lowLevelCode.append("\tJG ");
                }
                if(operator.equals("<")){
                    lowLevelCode.append("\tJL ");
                }
                if(operator.equals(">=")){
                    lowLevelCode.append("\tJGE ");
                }
                if(operator.equals("<=")){
                    lowLevelCode.append("\tJLE ");
                }
                lowLevelCode.append("ELSE").append(labelCounter).append("\n");
                next();
                next();
                if(match(Token.LEFT_BRACE)){
                    consumeLine();
                }
                lowLevelCode.append("\tJMP END").append(labelCounter).append("\n");
                lowLevelCode.append("ELSE").append(labelCounter).append(":\n");
                if(match(Token.RW) && "else".equals(currentToken.getValue())){
                    next();
                    next();
                    if(match(Token.LEFT_BRACE)){
                        consumeLine();
                    }
                }
                lowLevelCode.append("END").append(labelCounter).append(":\n");
                labelCounter++;
            }

        }
    }

    private void consumeLine(){
        while (currentToken.getTokenType() != Token.SEMICOLON){
            next();
        }
        next();
    }
    private boolean isType(String value) {
        return "int".equals(value) || "double".equals(value) || "string".equals(value);
    }

    public String getLowLevelCode() {
        return lowLevelCode.toString();
    }

    public void clearLowLevel(){
        lowLevelCode = new StringBuilder();
    }
}
