package com.compiler.utils;

public class Simbol {
    private Token tokenType;
    private String value;

    public Simbol(Token tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public Token getTokenType() {
        return tokenType;
    }

    public void setTokenType(Token tokenType) {
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}