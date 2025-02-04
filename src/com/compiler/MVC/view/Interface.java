package com.compiler.MVC.view;

import com.compiler.MVC.model.Lexical;
import com.compiler.utils.Simbol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Interface extends JFrame {

    private JTextArea codeArea;
    private JTextArea lexicoArea;
    private JTextArea sintacticoArea;
    private JTextArea bajoNivelArea;
    private JTextArea binarioArea;
    private JButton openButton;
    private JButton analyzeButton;
    private JButton parserButton;
    private JButton semanticButton;
    private JButton intermediateButton;
    private JButton objectButton;

    public Interface() {
        setTitle("Compilador de Nuevo Lenguaje");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        openButton = new JButton("Abrir Archivo");
        openButton.setFont(new Font("Arial", Font.BOLD, 14));
        openButton.addActionListener(_ -> openFile());
        buttonPanel.add(openButton);

        analyzeButton = new JButton("Analizar");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(analyzeButton);

        parserButton = new JButton("Parser");
        parserButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(parserButton);

        semanticButton = new JButton("Semántico");
        semanticButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(semanticButton);

        intermediateButton = new JButton("Intermedio");
        intermediateButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(intermediateButton);

        objectButton = new JButton("Objeto");
        objectButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(objectButton);

        add(buttonPanel, BorderLayout.NORTH);

        codeArea = new JTextArea();
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setBorder(BorderFactory.createTitledBorder("Código Fuente"));
        add(codeScrollPane, BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new GridLayout(2, 2));

        lexicoArea = new JTextArea();
        lexicoArea.setEditable(false);
        JScrollPane lexicoScrollPane = new JScrollPane(lexicoArea);
        lexicoScrollPane.setBorder(BorderFactory.createTitledBorder("Análisis Léxico"));
        resultsPanel.add(lexicoScrollPane);

        sintacticoArea = new JTextArea();
        sintacticoArea.setEditable(false);
        JScrollPane sintacticoScrollPane = new JScrollPane(sintacticoArea);
        sintacticoScrollPane.setBorder(BorderFactory.createTitledBorder("Análisis Sintáctico"));
        resultsPanel.add(sintacticoScrollPane);

        bajoNivelArea = new JTextArea();
        bajoNivelArea.setEditable(false);
        JScrollPane bajoNivelScrollPane = new JScrollPane(bajoNivelArea);
        bajoNivelScrollPane.setBorder(BorderFactory.createTitledBorder("Código de Bajo Nivel"));
        resultsPanel.add(bajoNivelScrollPane);

        binarioArea = new JTextArea();
        binarioArea.setEditable(false);
        JScrollPane binarioScrollPane = new JScrollPane(binarioArea);
        binarioScrollPane.setBorder(BorderFactory.createTitledBorder("Código Binario"));
        resultsPanel.add(binarioScrollPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, resultsPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
    }

    public void setAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }

    public void setParserButtonListener(ActionListener listener) {
        parserButton.addActionListener(listener);
    }

    public void setSemanticButtonListener(ActionListener listener) {
        semanticButton.addActionListener(listener);
    }

    public void setIntermediateButtonListener(ActionListener listener) {
        intermediateButton.addActionListener(listener);
    }

    public void setObjectButtonListener(ActionListener listener) {
        objectButton.addActionListener(listener);
    }

    public String getCode() {
        return codeArea.getText();
    }

    public void setLexicoContent(String content) {
        lexicoArea.setText(content);
    }

    public void setSintacticoContent(String content) {
        sintacticoArea.setText(content);
    }

    public void setSemanticoContent(String content) {
        bajoNivelArea.setText(content);
    }

    public void setIntermediateContent(String content) {
        bajoNivelArea.setText(content);
    }

    public void setObjectContent(String content) {
        binarioArea.setText(content);
    }

    public void clearLexicoContent() {
        System.out.println("clear");
        lexicoArea.setText("");
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
                codeArea.setText(fileContent.toString());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}