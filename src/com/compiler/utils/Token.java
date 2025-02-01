package com.compiler.utils;

public enum Token {
   START,
    INT, DOUBLE, STRING,
    PLUS, MINUS, DIVIDE, TIMES,
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, NOT_EQUAL,
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, SEMICOLON, ASSIGN, DOT, COMMA,
    IDENTIFIER, NUMBER, FRACTION, STRING_VALUE,
    IF, ELSE,
    READ, PRINT,
    END,
    ERROR
}
