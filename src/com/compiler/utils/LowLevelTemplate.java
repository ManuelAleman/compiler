package com.compiler.utils;

public class LowLevelTemplate {

    private static String generateTabs(int tabs) {
        return "\t".repeat(tabs);
    }

    public static String dataTemplate(Variable variable, int tabs) {
        String formattedData = switch (variable.getType()) {
            case "int" -> String.format("%-20s %-3s ?", variable.getName(), "dw");
            case "double" -> String.format("%-20s %-3s ?", variable.getName(), "dd");
            default -> String.format("%-20s %-3s 256 DUP(\"$\")", variable.getName(), "db");
        };
        return generateTabs(tabs) + formattedData + "\n";
    }

    public static String stringAssigmentTemplate(String varName, String stringValue, int tabs) {
        StringBuilder stringCode = new StringBuilder();
        char[] characters = stringValue.toCharArray();

        for (int j = 0; j < characters.length; j++) {
            stringCode.append(generateTabs(tabs))
                    .append(String.format("MOV [%-20s + %d], '%c'\n", varName, j, characters[j]));
        }

        return stringCode.toString();
    }

    public static String expressionAssigmentTemplate(String varName, String expression, int tabs) {
        String assemblyCode = "";

        if (!expression.contains("+") && !expression.contains("-") && !expression.contains("*") && !expression.contains("/")) {
            assemblyCode += movTemplate(varName, expression, tabs);
        } else {
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                String leftPart = parts[0].trim();
                String rightPart = parts[1].trim();
                assemblyCode += movTemplate("AX", leftPart, tabs);
                assemblyCode += addTemplate("AX", rightPart, tabs);
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                String leftPart = parts[0].trim();
                String rightPart = parts[1].trim();
                assemblyCode += movTemplate("AX", leftPart, tabs);
                assemblyCode += subTemplate("AX", rightPart, tabs);
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                String leftPart = parts[0].trim();
                String rightPart = parts[1].trim();
                assemblyCode += movTemplate("AX", leftPart, tabs);
                assemblyCode += generateTabs(tabs) + String.format("IMUL AX, %-10s\n", rightPart);
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                String leftPart = parts[0].trim();
                String rightPart = parts[1].trim();
                assemblyCode += movTemplate("AX", leftPart, tabs);
                assemblyCode += generateTabs(tabs) + "XOR DX, DX\n";
                assemblyCode += generateTabs(tabs) + String.format("DIV %-10s\n", rightPart);
            }
            assemblyCode += movTemplate(varName, "AX", tabs);
        }

        return assemblyCode;
    }

    public static String printStringTemplate(String varName, int tabs) {
        return generateTabs(tabs) + String.format("MOV DX, OFFSET %-20s\n", varName) +
                generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "INT 21h\n";
    }

    public static String prinIdTemplate(String varName, int tabs) {
        return generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "LEA DX, " + varName + "\n" +
                generateTabs(tabs) + "INT 21h\n";
    }


    public static String endOfProgram(int tabs) {
        return generateTabs(tabs) + "JMP ENDE0\n" +
                "ENDE0:\n" +
                generateTabs(tabs) + movTemplate("AH", "4Ch", 0) +
                generateTabs(tabs) + intTemplate();
    }

    private static String intTemplate() {
        return generateTabs(0) + String.format("%-7s %s\n", "INT", "21h");
    }

    public static String movTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "MOV", destination, source);
    }

    public static String addTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "ADD", destination, source);
    }

    public static String subTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "SUB", destination, source);
    }

}
