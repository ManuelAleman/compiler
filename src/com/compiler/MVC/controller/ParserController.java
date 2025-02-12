package com.compiler.MVC.controller;

import com.compiler.MVC.model.Parser;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;

import java.util.List;

public class ParserController {
    private Parser parselModel;
    private Interface view;

    public ParserController(Parser parserModel, Interface view){
        this.parselModel = parserModel;
        this.view = view;
    }

    public void parseToken(List<Simbol> simbols){
        parselModel.setTokens(simbols);
    }
}
