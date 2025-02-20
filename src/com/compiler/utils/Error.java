package com.compiler.utils;

public class Error {
    private Token token;
    private Position position;
    private String invalidValue;
    public Error(Token token, Position position, String invalidValue) {
        this.token = token;
        this.position = position;
        this.invalidValue = invalidValue;
    }

    public Position getPosition() {
        return position;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Error: " + invalidValue + " en " + position;
    }
}
