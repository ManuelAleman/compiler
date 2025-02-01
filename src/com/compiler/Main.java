package com.compiler;

public class Main {
    public static void main(String[] args) {
        Lexical lexical = new Lexical();
        lexical.analyze("START\n" +
                "    int edad;\n" +
                "    double altura;\n" +
                "    string nombre;\n" +
                "\n" +
                "    nombre = \"Carlos\";\n" +
                "    edad = 25;\n" +
                "    altura = 1.75;\n" +
                "\n" +
                "    print(nombre);\n" +
                "    print(edad);\n" +
                "    print(altura);\n" +
                "\n" +
                "    if(edad > 18) {\n" +
                "        print(\"Es mayor de edad\");\n" +
                "    } else {\n" +
                "        print(\"Es menor de edad\");\n" +
                "    }\n" +
                "\n" +
                "    read(edad);\n" +
                "END\n");

        //lexical.printPairs();
        System.out.println("PRINT TOKENS");
        lexical.printSimbols();


    }
}
