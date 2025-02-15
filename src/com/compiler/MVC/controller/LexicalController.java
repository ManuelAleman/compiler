package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;
import com.compiler.utils.Error;

import java.util.List;

public class LexicalController {
    private Lexical lexicalModel;
    private Interface view;

    public LexicalController(Lexical lexicalModel, Interface view) {
        this.lexicalModel = lexicalModel;
        this.view = view;
    }

    public void analyzeCode() {
        clearLexical();
        String code = view.getCode();
        if (code.isEmpty()) {
            view.logToConsole("No hay código para analizar");
            view.setParserButtonEnabled(false);
            return;
        }
        lexicalModel.analyze(code);
        view.setLexicoContent(lexicalModel.getLexicalAsString(), !lexicalModel.getErrors().isEmpty());
        view.logToConsole(lexicalModel.getErrorAsString());
        view.setParserButtonEnabled(lexicalModel.getErrors().isEmpty());
    }

    private void clearLexical(){
        view.clearLexicoContent();
        view.clearConsoleArea();
        lexicalModel.clearSimbols();
    }

    public List<Simbol> getSimbols() {
        return lexicalModel.getSimbols();
    }
}
