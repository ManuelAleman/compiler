package com.compiler.MVC.controller;

import com.compiler.MVC.model.Semantic;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;
import com.compiler.utils.Variable;

import java.util.List;

public class SemanticController {
    private final Semantic semanticModel;
    private final Interface view;

    public SemanticController(Semantic semanticModel, Interface view) {
        this.semanticModel = semanticModel;
        this.view = view;
    }

    public void analyzeSemantic(List<Simbol> tokens){
        semanticModel.setTokens(tokens);

        boolean status = semanticModel.analyzeSemantic();
        if(status){
            for(Variable v : semanticModel.getVariables()){
                System.out.println(v);
            }
        }
    }
}
