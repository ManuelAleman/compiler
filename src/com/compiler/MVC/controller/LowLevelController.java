package com.compiler.MVC.controller;

import com.compiler.MVC.model.LowLevel;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;
import com.compiler.utils.Variable;

import java.util.List;

public class LowLevelController {
    private LowLevel lowLevel;
    private Interface view;

    public LowLevelController(LowLevel lowLevel, Interface view){
        this.lowLevel = lowLevel;
        this.view = view;
    }

    public void generateLowLevelCode(List<Variable> variables, List<Simbol> tokens){
        resetLowLevelAnalysis();
        lowLevel.prepareLowLevel(variables, tokens);
        lowLevel.analizeLowLevel();
        updateViewWithAnalysisResults();
    }

    private void resetLowLevelAnalysis() {
        view.clearLowLevelContent();
        view.clearConsoleArea();
        lowLevel.clearLowLevel();
    }

    private void updateViewWithAnalysisResults() {
        view.setLowLevelContent(lowLevel.getLowLevelCode());
    }


}
