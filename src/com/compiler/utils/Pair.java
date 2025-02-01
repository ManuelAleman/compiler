package com.compiler.utils;

public class Pair {
    private Token token;
    private Position position;
    public Pair(Token token, Position position) {
        this.token = token;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Token getToken() {
        return token;
    }
}
