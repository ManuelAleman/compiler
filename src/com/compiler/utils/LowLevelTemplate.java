package com.compiler.utils;

import java.util.HashMap;
import java.util.Objects;

public class LowLevelTemplate {
    public static StringBuilder objectCode = new StringBuilder();
    public static String memoryDataSegment = "0000 0000 0000 0000";
    public static String memoryCodeSegment = "0000 0000 0000 0000";
    public static HashMap<String, String> registerMap = new HashMap<>();

    private static String generateTabs(int tabs) {
        return "\t".repeat(tabs);
    }
    public static String dataTemplate(Variable variable, int tabs) {
        String formattedData;

        switch (variable.getType()) {
            case "int": {
                formattedData = String.format("%-5s %-3s ?", variable.getName(), "dw");
                registerMap.put(variable.getName(), memoryDataSegment);
                objectCode.append(memoryDataSegment)
                        .append(generateTabs(tabs))
                        .append("0000 0000 0000 0000\n");
                memoryDataSegment = incrementMemorySegment(memoryDataSegment, 2);
                break;
            }
            case "double": {
                formattedData = String.format("%-5s %-3s ?", variable.getName(), "dd");
                registerMap.put(variable.getName(), memoryDataSegment);
                objectCode.append(memoryDataSegment)
                        .append(generateTabs(tabs))
                        .append("0000 0000 0000 0000\n");
                memoryDataSegment = incrementMemorySegment(memoryDataSegment, 2);
                break;
            }
            default: {
                formattedData = String.format("%-5s %-3s 256 DUP(\"$\")", variable.getName(), "db");
                registerMap.put(variable.getName(), memoryDataSegment);
                for(int i=0; i<256; i++){
                    objectCode.append(memoryDataSegment)
                            .append(generateTabs(tabs))
                            .append("0010 0100\n");
                    memoryDataSegment = incrementMemorySegment(memoryDataSegment, 1);
                }
                break;
            }
        }

        return generateTabs(tabs) + formattedData + "\n";
    }

    public static String stringAssigmentTemplate(String varName, String stringValue, int tabs) {
        StringBuilder stringCode = new StringBuilder();
        char[] characters = stringValue.toCharArray();
        String memoryStringLoc = getMemoryVariable(varName);
        for (int j = 0; j < characters.length; j++) {
            stringCode.append(generateTabs(tabs))
                    .append(String.format("MOV [%s + %d], '%c'\n", varName, j, characters[j]));
            objectCode.append(memoryCodeSegment).append(generateTabs(tabs)).append("1100 0110 0000 0110 ").append(memoryStringLoc).append(charToBinaryWithSpace(characters[j])).append("\n");
            memoryStringLoc = incrementMemorySegment(memoryStringLoc, 1);
            memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 5);
        }

        return stringCode.toString();
    }

    public static String charToBinaryWithSpace(char c) {
        int ascii = c;
        String binary = String.format("%8s", Integer.toBinaryString(ascii)).replace(' ', '0');
        return " " + binary.substring(0, 4) + " " + binary.substring(4);
    }


    public static String expressionAssigmentTemplate(String varName, String expression, int tabs) {
        String assemblyCode = "";

        if (!expression.contains("+") && !expression.contains("-") && !expression.contains("*") && !expression.contains("/")) {
            assemblyCode += movTemplate(varName, expression, tabs);
            objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 0111 0000 0110 ").append(getMemoryVariable(varName)).append(" ").append(numberToBinary(expression)).append("\n");
            memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 6);
        }else {
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1000 ").append(numberToBinary(parts[0].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += addTemplate("AX", parts[1].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0000 0101 ").append(numberToBinary(parts[1].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1000 ").append(numberToBinary(parts[0].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += subTemplate("AX", parts[1].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0010 1101 ").append(numberToBinary(parts[1].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1000 ").append(numberToBinary(parts[0].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += movTemplate("BX", parts[1].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1011 ").append(numberToBinary(parts[0].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += generateTabs(tabs) + "IMUL BX\n";
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1111 0111 1110 0011 ").append(getMemoryVariable(varName)).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                assemblyCode += movTemplate("AX", parts[0].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1000 ").append(numberToBinary(parts[0].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += generateTabs(tabs) + "XOR DX, DX\n";
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 0011 1101 0010 ");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
                assemblyCode += movTemplate("BX", parts[1].trim(), tabs);
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1011 ").append(numberToBinary(parts[1].trim())).append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
                assemblyCode += generateTabs(tabs) + "IDIV BX\n";
                objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1111 0111 1111 1011 ").append("\n");
                memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
            }
            assemblyCode += movTemplate(varName, "AX", tabs);
            objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1011 0001 0000 ").append(getMemoryVariable(varName)).append("\n");
            memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        }
        return assemblyCode;
    }

    public static String printStringTemplate(String varName, int tabs) {
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1101 0001 0110 ").append(getMemoryVariable(varName)).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0100 0000 1001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return generateTabs(tabs) + String.format("LEA DX, %-3s\n", varName) +
                generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "INT 21h\n" +
                nextLine(tabs);
    }

    public static String readStringTemplate(String varName, int tabs, int labelCount) {
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1110 0000 0000 0000 0000").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0100 0000 0001 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 1100 0000 0000").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0111 0100 1000 1011").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1000 1000 0100 ").append(getMemoryVariable(varName)).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0100 0110 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 1);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1110 1011 1000 1101").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 0111 1000 0100 ").append(getMemoryVariable(varName)).append(" 0010 0100").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        return movTemplate("SI", "0", tabs)
                + "LEER" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV AH, 01h\n" +
                generateTabs(tabs) + "INT 21h\n" +
                generateTabs(tabs) + "CMP AL, 0Dh\n" +
                generateTabs(tabs) + "JE FIN" + labelCount + "\n" +
                generateTabs(tabs) + "MOV [%-3s + SI], AL\n".formatted(varName) + //pendiente
                generateTabs(tabs) + "INC SI\n" +
                generateTabs(tabs) + "JMP LEER" + labelCount + "\n" + //pendiente
                "FIN" + labelCount + ":\n" +
                generateTabs(tabs) + "MOV [%3s + SI], '$'\n".formatted(varName) + //pendiente
                nextLine(tabs);
    }

    public static String readNumberTemplate(String varName, int tabs, int labelCount){
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1011 0000 0000 0000 0000 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0100 0000 0001 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 1100 0000 1101").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 0000 1110 1000 0011 0000 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0100 0000 0000 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1011 1100 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1000 0000 1010 0000 0000").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1111 0111 1101 1011 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1011 1100 0011 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0000 0011 1100 1011 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1110 1011 1001 1000").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1001 0001 1110 ").append(getMemoryVariable(varName)).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);

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
                generateTabs(tabs) + "MOV [%-3s], BX\n".formatted(varName) +
                nextLine(tabs);
    }

    public static String printNumberIdTemplate(String varName, int tabs) {
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 1011 0000 0110  ").append(getMemoryVariable(varName)).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 0011 1100 1001 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return movTemplate("AX", varName, tabs) +
                generateTabs(tabs) + "XOR CX,CX \n";
    }

    public static String cicloTemplate(int tabs, int labelCount) {
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 0011 1101 0010 ");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 1011 ").append(numberToBinary("10")).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 3);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1111 0111 1111 0011 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1000 0010 1100 0010 ").append(numberToBinary("30")).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0101 0010 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 1);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0100 0001 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 1);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 1101 0000 0000").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0111 0101 1000 1011").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
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
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0011 0011 1101 0010 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("0101 1010").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 1);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0010 0000 0010 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1110 0010 1000 0101 ").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return "MOSTRAR" + labelCount + ":\n" +
                generateTabs(tabs) + "XOR DX, DX\n" +
                generateTabs(tabs) + "POP DX\n" +
                generateTabs(tabs) + "MOV AH, 2\n" +
                generateTabs(tabs) + "INT 21h\n" +
                generateTabs(tabs) + "LOOP MOSTRAR" + labelCount + "\n" +
                nextLine(tabs);
    }

    public static String endOfProgram() {
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0100 0100 1100").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return generateTabs(1) + movTemplate("AH", "4Ch", 0) +
                generateTabs(1) + intTemplate();
    }

    public static String movTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-3s %-3s, %s\n", "MOV", destination, source);
    }

    public static String addTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "ADD", destination, source);
    }

    public static String subTemplate(String destination, String source, int tabs) {
        return generateTabs(tabs) + String.format("%-6s %-10s, %s\n", "SUB", destination, source);
    }

    private static String nextLine(int tabs) {
        objectCode.append(memoryCodeSegment).append(generateTabs(tabs)).append("1000 1101 0001 0110 ").append(getMemoryVariable("new_line")).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 4);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1011 0010 0000 1001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        objectCode.append(memoryCodeSegment).append(generateTabs(1)).append("1100 1101 0010 0001").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return generateTabs(tabs) + "LEA DX, new_line\n" +
                generateTabs(tabs) + "MOV AH, 09h\n" +
                generateTabs(tabs) + "INT 21h\n";
    }

    private static String intTemplate() {
        return generateTabs(0) + String.format("%-3s %s\n", "INT", "21h");
    }

    public static String CompTemplate(String destination, String ope, String source, int tabs, int labelCount) {
        String code = generateTabs(tabs) + String.format("%-3s %-3s, %s\n", "CMP", destination, source);
        objectCode.append(memoryCodeSegment).append(generateTabs(tabs)).append("1000 0001 0011 1110 ").append(getMemoryVariable(destination)).append(" ").append(numberToBinary(source)).append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 6);
        objectCode.append(memoryCodeSegment).append(generateTabs(tabs));
        switch (ope) {
            case "==":
                code += generateTabs(tabs) + "JNE ";
                objectCode.append("0111 0101 ");
                break;
            case "<>":
                code += generateTabs(tabs) + "JE ";
                objectCode.append("0111 0100 ");
                break;
            case ">":
                code += generateTabs(tabs) + "JLE ";
                objectCode.append("0111 1110 ");
                break;
            case "<":
                code += generateTabs(tabs) + "JGE ";
                objectCode.append("0111 1101 ");
                break;
            case ">=":
                code += generateTabs(tabs) + "JL ";
                objectCode.append("0111 1100 ");
                break;
            case "<=":
                code += generateTabs(tabs) + "JG ";
                objectCode.append("0111 1111 ");
                break;
        }
        code += "ELSE" + labelCount + "\n";
        objectCode.append("0000 1011").append("\n");
        memoryCodeSegment = incrementMemorySegment(memoryCodeSegment, 2);
        return code;
    }
    public static String incrementMemorySegment(String segment, int bytesToAdd) {
        String cleanBinary = segment.replace(" ", "");
        int current = Integer.parseUnsignedInt(cleanBinary, 2);
        current += bytesToAdd;
        current &= 0xFFFF;
        String newBinary = String.format("%16s", Integer.toBinaryString(current)).replace(' ', '0');
        return newBinary.replaceAll("(.{4})(?!$)", "$1 ");
    }


    public static void resetObjectCode() {
        objectCode = new StringBuilder();
        memoryDataSegment = "0000 0000 0000 0000";
        memoryCodeSegment = "0000 0000 0000 0000";
    }

    public static String getMemoryVariable(String name){
        return registerMap.getOrDefault(name, "0000 0000 0000 0000");
    }

    public static String numberToBinary(String number) {
        int num = Integer.parseInt(number);
        String binary = Integer.toBinaryString(num);
        String padded = String.format("%16s", binary).replace(' ', '0');
        return padded.replaceAll("(.{4})(?!$)", "$1 ");
    }


}
