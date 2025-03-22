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
                "\tINT 21h\n" + "\n";
    }

    public static String movTemplate(String destination, String source) {
        return "\tMOV " + destination + ", " + source + "\n";
    }

    public static String addTemplate(String destination, String source) {
        return "\tADD " + destination + ", " + source + "\n";
    }

    public static String subTemplate(String destination, String source) {
        return "\tSUB " + destination + ", " + source + "\n";
    }

    public static String mulTemplate(String operand) {
        return "\tMOV AX, " + operand + "\n" +
                "\tMUL BX\n";
    }

    public static String divTemplate(String operand) {
        return "\tMOV AX, " + operand + "\n" +
                "\tMOV DX, 0\n" +
                "\tDIV BX\n";
    }


    public static String stringAssigmentTemplate(String varName, String stringValue) {
        StringBuilder stringCode = new StringBuilder();
        char[] characters = stringValue.toCharArray();

        for (int j = 0; j < characters.length; j++) {
            stringCode.append("	MOV	 [").append(varName).append(" + ").append(j).append("], '").append(characters[j]).append("'\n");
        }

        return stringCode.toString();
    }

    public static String printStringTemplate(String varName) {
        return "	MOV	 DX, OFFSET " + varName + "\n" +
                "	MOV	 AH, 09h\n" +
                "	INT	 21h\n";
    }

    public static String prinIdTemplate(String varName) {
        return "\tMOV AH, 09h\n" +
                "\tLEA DX, " + varName + "\n" +
                "\tINT 21h\n";
    }

    public static String endOfProgram() {
        return  "ENDE0" + ":" + "\n" +
                "    MOV AH, 4Ch" + "\n" +
                "    INT 21h\n";
    }
}
