package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.view.Interface;

public class CompilerController {
    private LexicalController lexicalController;
    private Interface view;

    public CompilerController(Interface view) {
        this.view = view;
        initControllers();
        initViewListeners();
    }

    private void initControllers() {
        Lexical lexicalModel = new Lexical();
        this.lexicalController = new LexicalController(lexicalModel, view);
    }

    private void initViewListeners() {
        view.setAnalyzeButtonListener(_ -> lexicalController.analyzeCode());
    }
}
