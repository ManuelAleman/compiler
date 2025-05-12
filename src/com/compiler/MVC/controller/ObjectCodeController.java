package com.compiler.MVC.controller;

import com.compiler.MVC.model.ObjectCode;
import com.compiler.MVC.view.Interface;

public class ObjectCodeController {
    private ObjectCode objectCode;
    private Interface view;
    public ObjectCodeController(ObjectCode objectCode, Interface view){
        this.objectCode = objectCode;
        this.view = view;
    }

    public void generateObjectCode(){
        resetObjectCodeAnalysis();
        updateViewWithAnalysisResults();
    }

    private void resetObjectCodeAnalysis() {
        objectCode.cleanObjectCode();
        view.clearObjectCodeContent();
        view.clearConsoleArea();
    }

    private void updateViewWithAnalysisResults() {
        view.setObjectCodeContent(objectCode.getObjectCode());
    }

    public void clearObjectCode(){
        view.clearObjectCodeContent();
    }
}
