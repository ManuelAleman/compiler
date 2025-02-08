package com.compiler.utils;

public class Simbol {
    private Token tokenType;
    private String value;
    private Position position;

    public Simbol(Token tokenType, String value, Position position) {
        this.tokenType = tokenType;
        this.value = value;
        this.position = position;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}