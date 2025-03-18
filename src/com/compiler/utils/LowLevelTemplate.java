package com.compiler.utils;

public class LowLevelTemplate {
    public static String dataTemplate(Variable variable) {
        return switch (variable.getType()) {
            case "int" -> "\t" + variable.getName() + " dw ?\n";
            case "double" -> "\t" + variable.getName() + " dd ?\n";
            default -> "\t" + variable.getName() + " db 256 DUP(\"$\")\n";
        };
    }
    public static String printTemplate(String variable) {
        return "\tMOV AH, 09h\n" +
                "\tLEA DX, " + variable + "\n" +
                "\tINT 21h\n";
    }
    public static String movTemplate(String destination, String source) {
        return "\tMOV " + destination + ", " + source + "\n";
    }
    public static String addTemplate(String destination, String source) {
        return "\tADD " + destination + ", " + source + "\n";
    }
    public static String mulTemplate(String destination, String source) {
        return "\tMUL " + destination + ", " + source + "\n";
    }
    public static String subTemplate(String destination, String source) {
        return "\tSUB " + destination + ", " + source + "\n";
    }
    public static String divTemplate(String destination, String source) {
        return "\tDIV " + destination + ", " + source + "\n";
    }
}