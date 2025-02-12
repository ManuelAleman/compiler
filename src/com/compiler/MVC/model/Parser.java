package com.compiler.MVC.model;

import com.compiler.utils.Simbol;
import com.compiler.utils.Token;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Simbol> tokens;
    private int index;
    private Simbol currentToken;

    public Parser() {
        tokens = new ArrayList<>();
    }

    public void setTokens(List<Simbol> tokens) {
        this.tokens = tokens;
        index = 0;
        currentToken = tokens.get(index);
    }

    public void next(){
        index++;
        currentToken = index < tokens.size() ? tokens.get(index) : null;
    }

    private boolean match(Token expectedToken) {
        if (currentToken != null && currentToken.getTokenType() == expectedToken) {
            next();
            return true;
        }
        return false;
    }

}