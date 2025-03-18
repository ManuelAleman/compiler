package com.compiler.MVC.controller;

import com.compiler.MVC.model.Semantic;
import com.compiler.MVC.view.Interface;
import com.compiler.utils.Simbol;
import com.compiler.utils.Variable;

import java.util.List;

public class SemanticController {
    private final Semantic semanticModel;
    private final Interface view;
    private boolean semanticCorrect;

    public SemanticController(Semantic semanticModel, Interface view) {
        this.semanticModel = semanticModel;
        this.view = view;
        this.semanticCorrect = false;
    }

    public void analyzeSemantic(List<Simbol> tokens){
        semanticModel.setTokens(tokens);

        semanticCorrect = semanticModel.analyzeSemantic();
        view.setSemanticColor(semanticCorrect);
        view.logToConsole(semanticModel.getErrorMessage());
    }

    public boolean isSemanticCorrect(){
        return semanticCorrect;
    }

    public List<Variable> getVariables(){
        return semanticModel.getVariables();
    }

    public void setSemanticCorrect(boolean semanticCorrect) {
        this.semanticCorrect = semanticCorrect;
    }
}
