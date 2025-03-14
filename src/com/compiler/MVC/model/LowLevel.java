package com.compiler.MVC.model;

import com.compiler.utils.LowLevelTemplate;
import com.compiler.utils.Simbol;
import com.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.List;

public class LowLevel {
    private List<Variable> variables;
    private List<Simbol> tokens;
    private StringBuilder lowLevelCode;

    public LowLevel(){
        variables = new ArrayList<>();
        tokens = new ArrayList<>();
        lowLevelCode = new StringBuilder();
    }
    public void prepareLowLevel(List<Variable> variables, List<Simbol> tokens) {
        this.variables = variables;
        this.tokens = tokens;
    }

    public void analizeLowLevel(){
        generateDataSegment();
    }

    private void generateDataSegment(){
        lowLevelCode.append(".DATA\n");
        for (Variable variable : variables) {
            lowLevelCode.append(LowLevelTemplate.dataTemplate(variable));
        }
    }

    public String getLowLevelCode() {
        return lowLevelCode.toString();
    }

    public void clearLowLevel(){
        lowLevelCode = new StringBuilder();
    }
}
