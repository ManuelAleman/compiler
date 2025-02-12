package com.compiler;

import com.compiler.MVC.controller.CompilerController;
import com.compiler.MVC.view.Interface;

public class Main {
    public static void main(String[] args) {
        Interface view = new Interface();
        new CompilerController(view);

        view.setVisible(true);
    }
}