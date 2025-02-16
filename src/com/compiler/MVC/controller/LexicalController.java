package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

import java.util.List;

public class LexicalController {
    private final Lexical lexicalModel;
    private final Interface view;

    public LexicalController(Lexical lexicalModel, Interface view) {
        this.lexicalModel = lexicalModel;
        this.view = view;
    }

    public void analyzeCode() {
        resetLexicalAnalysis();

        String code = getCodeFromView();
        if (code.isEmpty()) {
            handleEmptyCode();
            return;
        }

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

    private void handleEmptyCode() {
        view.logToConsole("No hay código para analizar");
        view.setParserButtonEnabled(false);
    }

    private void updateViewWithAnalysisResults() {
        boolean hasErrors = !lexicalModel.getErrors().isEmpty();
        view.setLexicoContent(lexicalModel.getLexicalAsString(), hasErrors);
        view.logToConsole(lexicalModel.getErrorAsString());
        view.setParserButtonEnabled(!hasErrors);
        view.clearParserSemanticLabels();
    }

    public List<Simbol> getSimbols() {
        return lexicalModel.getSimbols();
    }
}
