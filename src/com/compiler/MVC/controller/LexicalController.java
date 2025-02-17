package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

import java.util.List;

public class LexicalController {
    private final Lexical lexicalModel;
    private final Interface view;
    private boolean lexicalCorrect;

    public LexicalController(Lexical lexicalModel, Interface view) {
        this.lexicalModel = lexicalModel;
        this.view = view;
        this.lexicalCorrect = false;
    }

    public void analyzeCode() {
        resetLexicalAnalysis();

        String code = getCodeFromView();

        lexicalModel.analyze(code);
        updateViewWithAnalysisResults();
    }

    private void resetLexicalAnalysis() {
        view.clearLexicoContent();
        view.clearConsoleArea();
        lexicalModel.clearSimbols();
    }

    private String getCodeFromView() {
        return view.getCode();
    }

    private void updateViewWithAnalysisResults() {
        lexicalCorrect = lexicalModel.getErrors().isEmpty();
        view.setLexicoContent(lexicalModel.getLexicalAsString(), lexicalCorrect);
        view.logToConsole(lexicalModel.getErrorAsString());
        view.clearParserSemanticLabels();
    }

    public List<Simbol> getSimbols() {
        return lexicalModel.getSimbols();
    }

    public boolean isLexicalCorrect() {
        return lexicalCorrect;
    }
    public void setLexicalCorrect(boolean status){
        this.lexicalCorrect = status;
    }
}