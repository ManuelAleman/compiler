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
            resetControlelrs();
            parserController.clearParser();
            if (isCodeEmpty()) {
                codeEmpty();
                return;
            }
            lexicalController.analyzeCode();
            updateButtonStatus();
        });

        view.setParserButtonListener(_ -> {
            parserController.parseToken(lexicalController.getSimbols());
            updateButtonStatus();
        });
        view.setParserButtonEnabled(false);

        view.setSemanticButtonListener(_ -> semanticController.analyzeSemantic(lexicalController.getSimbols()));
        view.setSemanticButtonEnabled(false);
    }

    private void updateButtonStatus(){
        boolean isLexerCorrect = lexicalController.isLexicalCorrect();
        boolean isParserCorrect = parserController.isParserCorrect();
        boolean isSemanticCorrect = semanticController.isSemanticCorrect();

        view.setParserButtonEnabled(isLexerCorrect);
        view.setSemanticButtonEnabled(isLexerCorrect && isParserCorrect);
    }

    private void codeEmpty(){
        view.logToConsole("No hay código para analizar");
       resetControlelrs();
    }

    private boolean isCodeEmpty(){
        String code = view.getCode();
        return code == null || code.trim().isEmpty();
    }

    private void resetControlelrs(){
        lexicalController.setLexicalCorrect(false);
        parserController.setParserCorrect(false);
    }


}