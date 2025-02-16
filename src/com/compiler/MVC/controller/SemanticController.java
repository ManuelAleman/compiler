package com.compiler.MVC.controller;

import com.compiler.MVC.model.Semantic;
import com.compiler.MVC.view.Interface;

public class SemanticController {
    private final Semantic semanticModel;
    private final Interface view;

    public SemanticController(Semantic semanticModel, Interface view) {
        this.semanticModel = semanticModel;
        this.view = view;
    }
}
