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
        boolean correct = parselModel.parse();
        System.out.println(correct);

        List<String> errors = parselModel.getErrors();
        StringBuilder st = new StringBuilder();
        for(String error: errors){
            st.append("\n").append(error);
        }
        if(correct){
            view.setSintacticoContent("TODO BIEN");
        }else{
            view.setSintacticoContent("");
            view.logToConsole(st.toString());
        }
    }

    public void clearParser(){
        view.clearConsoleArea();
        view.clearSintacticoContent();
    }
}
