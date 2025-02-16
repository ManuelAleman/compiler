package com.compiler.MVC.controller;

import com.compiler.MVC.model.Parser;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

import java.util.List;

public class ParserController {
    private Parser parserModel;
    private Interface view;

    public ParserController(Parser parserModel, Interface view){
        this.parserModel = parserModel;
        this.view = view;
    }

    public void parseToken(List<Simbol> simbols){
        parserModel.setTokens(simbols);
        boolean correct = parserModel.parse();

        view.setSintacticoColor(correct);
        view.logToConsole(parserModel.getErrorMessage());
    }

    public void clearParser(){
        view.clearConsoleArea();
        view.clearSintacticoContent();
    }
}
