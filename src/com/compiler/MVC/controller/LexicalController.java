package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

public class LexicalController {
    private Lexical lexicalModel;
    private Interface view;

    public LexicalController(Lexical lexicalModel, Interface view) {
        this.lexicalModel = lexicalModel;
        this.view = view;
    }

    public void analyzeCode() {
        String code = view.getCode();
        lexicalModel.analyze(code);

        StringBuilder lexicalContent = new StringBuilder();
        for (Simbol simbol : lexicalModel.getSimbols()) {
            lexicalContent.append(simbol.getTokenType().name())
                    .append(" -> ")
                    .append(simbol.getValue())
                    .append("\n");
        }
        view.setLexicoContent(lexicalContent.toString());
    }
}
