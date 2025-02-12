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
        lexicalModel.analyze(code);

        StringBuilder lexicalContent = new StringBuilder();

        for (Simbol simbol : lexicalModel.getSimbols()) {
            lexicalContent.append("< ")
                    .append(simbol.getTokenType().name())
                    .append(" -> ")
                    .append(simbol.getValue())
                    .append(" >")
                    .append("\n");
        }

        /*
        for( Token t: lexicalModel.getTokens()){
            lexicalContent.append("< ")
                    .append(t.name())
                    .append(" >")
                    .append("\n");
        }
         */

        view.setLexicoContent(lexicalContent.toString());
        setErrors();
    }

    private void setErrors(){
        StringBuilder errors = new StringBuilder();
        for (Error error : lexicalModel.getErrors()) {
            errors.append(error).append("\n");
        }
        view.logToConsole(errors.toString());
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
