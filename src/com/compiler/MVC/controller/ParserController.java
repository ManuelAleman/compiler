package com.compiler.MVC.controller;

import com.compiler.MVC.model.Parser;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

import java.util.List;

public class ParserController {
    private Parser parserModel;
    private Interface view;
    private boolean parserCorrect;

    public ParserController(Parser parserModel, Interface view){
        this.parserModel = parserModel;
        this.view = view;
        this.parserCorrect = false;
    }

    public void parseToken(List<Simbol> simbols){
        parserModel.setTokens(simbols);
        parserCorrect = parserModel.parse();

        view.setSintacticoColor(parserCorrect);
        view.logToConsole(parserModel.getErrorMessage());
    }

    public void clearParser(){
        view.clearConsoleArea();
    }

    public boolean isParserCorrect() {
        return parserCorrect;
    }

    public void setParserCorrect(boolean status){
        this.parserCorrect = status;
    }
}