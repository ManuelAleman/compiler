package com.compiler.MVC.controller;

import com.compiler.MVC.model.Lexical;
import com.compiler.MVC.model.Parser;
import com.compiler.MVC.model.Semantic;
import com.compiler.MVC.view.Interface;

public class CompilerController {
    private LexicalController lexicalController;
    private ParserController parserController;
    private SemanticController semanticController;
    private Interface view;

    public CompilerController(Interface view) {
        this.view = view;
        initControllers();
        initViewListeners();
    }

    private void initControllers() {
        Lexical lexicalModel = new Lexical();
        this.lexicalController = new LexicalController(lexicalModel, view);
        Parser parserModel = new Parser();
        this.parserController = new ParserController(parserModel, view);
        Semantic semanticModel = new Semantic();
        this.semanticController = new SemanticController(semanticModel, view);
    }

    private void initViewListeners() {
        view.setAnalyzeButtonListener(_ -> {
            parserController.clearParser();
            lexicalController.analyzeCode();
        });

        view.setParserButtonListener(_ -> parserController.parseToken(lexicalController.getSimbols()));
        view.setParserButtonEnabled(false);

        view.setSemanticButtonListener(_ -> semanticController.analyzeSemantic(lexicalController.getSimbols()));
        //view.setSemanticButtonEnabled(false);
    }


}
