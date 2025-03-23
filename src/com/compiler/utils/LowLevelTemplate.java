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
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                assemblyCode += addTemplate("AX", parts[1].trim(), tabs);
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                assemblyCode += subTemplate("AX", parts[1].trim(), tabs);
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                assemblyCode += movTemplate("BX", parts[1].trim(), tabs);
                assemblyCode += generateTabs(tabs) + "IMUL BX\n";
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                assemblyCode += generateTabs(tabs) + "XOR DX, DX\n";
                assemblyCode += movTemplate("BX", parts[1].trim(), tabs);
                assemblyCode += generateTabs(tabs) + "IDIV BX\n";
            }
            assemblyCode += movTemplate(varName, "AX", tabs);
        }
        return assemblyCode;
    }

    public static String printStringTemplate(String varName, int tabs) {
        return generateTabs(tabs) + String.format("MOV DX, OFFSET %-20s\n", varName) +
                generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "INT 21h\n" +
                nextLine(tabs);
    }

    public static String readStringTemplate(String varName, int tabs, int labelCount) {
        return movTemplate("SI", "0", tabs)
                + "LEER" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV AH, 01h\n" +
                generateTabs(tabs) + "INT 21h\n" +
                generateTabs(tabs) + "CMP AL, 0Dh\n" +
                generateTabs(tabs) + "JE FIN" + labelCount + "\n" +
                generateTabs(tabs) + "MOV [%-20s + SI], AL\n".formatted(varName) +
                generateTabs(tabs) + "INC SI\n" +
                generateTabs(tabs) + "JMP LEER" + labelCount + "\n" +
                "FIN" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV [%-20s + SI], '$'\n".formatted(varName) +
                nextLine(tabs);
    }

    public static String readNumberTemplate(String varName, int tabs, int labelCount){
        return movTemplate("BX", "0", tabs)
                + "LEERNUM" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV AH, 01h\n" +
                generateTabs(tabs) + "INT 21h\n" +
                generateTabs(tabs) + "CMP AL, 0Dh\n" +
                generateTabs(tabs) + "JE FINNUM" + labelCount + "\n" +
                generateTabs(tabs) + "SUB AL, 48\n" +
                generateTabs(tabs) + "MOV AH, 0\n" +
                generateTabs(tabs) + "MOV CX, AX\n" +
                generateTabs(tabs) + "MOV AX, 10\n" +
                generateTabs(tabs) + "MUL BX\n" +
                generateTabs(tabs) + "MOV BX, AX\n" +
                generateTabs(tabs) + "ADD BX, CX\n" +
                generateTabs(tabs) + "JMP LEERNUM" + labelCount + "\n" +
                "FINNUM" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV [%-20s], BX\n".formatted(varName) +
                nextLine(tabs);
    }

    public static String printNumberIdTemplate(String varName, int tabs) {
        return movTemplate("AX", varName, tabs) +
                generateTabs(tabs) + "XOR CX,CX \n";
    }

    public static String cicloTemplate(int tabs, int labelCount) {
        return "CICLO" + labelCount + ":\n" +
                generateTabs(tabs) + "XOR DX, DX\n" +
                generateTabs(tabs) + "MOV BX, 10\n" +
                generateTabs(tabs) + "DIV BX\n" +
                generateTabs(tabs) + "ADD DL, 30h\n" +
                generateTabs(tabs) + "PUSH DX\n" +
                generateTabs(tabs) + "INC CX\n" +
                generateTabs(tabs) + "CMP AX, 0\n" +
                generateTabs(tabs) + "JNE CICLO" + labelCount + "\n";
    }

    public static String printNumber(int tabs, int labelCount) {
        return "MOSTRAR" + labelCount + ":\n" +
                generateTabs(tabs) + "XOR DX, DX\n" +
                generateTabs(tabs) + "POP DX\n" +
                generateTabs(tabs) + "MOV AH, 2\n" +
                generateTabs(tabs) + "INT 21h\n" +
                generateTabs(tabs) + "LOOP MOSTRAR" + labelCount + "\n" +
                nextLine(tabs);
    }

    public static String endOfProgram() {
        return generateTabs(1) + movTemplate("AH", "4Ch", 0) +
                generateTabs(1) + intTemplate();
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

    private static String nextLine(int tabs) {
        return generateTabs(tabs) + "MOV DX, OFFSET new_line\n" +
                generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "INT 21h\n";
    }

    private static String intTemplate() {
        return generateTabs(0) + String.format("%-7s %s\n", "INT", "21h");
    }

    public static String CompTemplate(String destination, String ope, String source, int tabs, int labelCount) {
        String code = generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "CMP", destination, source);
        switch (ope) {
            case "==":
                code += generateTabs(tabs) + "JNE ";
                break;
            case "<>":
                code += generateTabs(tabs) + "JE ";
                break;
            case ">":
                code += generateTabs(tabs) + "JLE ";
                break;
            case "<":
                code += generateTabs(tabs) + "JGE ";
                break;
            case ">=":
                code += generateTabs(tabs) + "JL ";
                break;
            case "<=":
                code += generateTabs(tabs) + "JG ";
                break;
        }
        code += "ELSE" + labelCount + "\n";
        return code;
    }
}
