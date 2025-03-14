package com.compiler.utils;

public class LowLevelTemplate {
    public static String dataTemplate(Variable variable) {
        return switch (variable.getType()) {
            case "int" -> "\t" + variable.getName() + " dw ?\n";
            case "double" -> "\t" + variable.getName() + " dd ?\n";
            default -> "\t" + variable.getName() + " db 256 DUP(\"$\")\n";
        };
    }
}