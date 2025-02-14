package com.compiler.MVC.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Interface extends JFrame {

    private JTextArea codeArea;
    private JTextArea lexicoArea;
    private JTextArea sintacticoArea;
    private JTextArea bajoNivelArea;
    private JTextArea binarioArea;
    private JTextArea consoleArea;
    private JButton analyzeButton;
    private JButton parserButton;
    private JButton semanticButton;
    private JButton intermediateButton;
    private JButton objectButton;

    public Interface() {
        setTitle("CompilorR");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem openMenuItem = new JMenuItem("Abrir");
        JMenuItem saveMenuItem = new JMenuItem("Guardar");

        openMenuItem.addActionListener(_ -> openFile());
        saveMenuItem.addActionListener(_ -> saveFile());

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        analyzeButton = new JButton("Analizar");
        parserButton = new JButton("Parser");
        semanticButton = new JButton("Semántico");
        intermediateButton = new JButton("Intermedio");
        objectButton = new JButton("Objeto");

        buttonPanel.add(analyzeButton);
        buttonPanel.add(parserButton);
        //buttonPanel.add(semanticButton);
        //buttonPanel.add(intermediateButton);
        //buttonPanel.add(objectButton);

        add(buttonPanel, BorderLayout.NORTH);

        codeArea = new JTextArea();
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setBorder(BorderFactory.createTitledBorder("Código Fuente"));

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
        //resultsPanel.add(bajoNivelScrollPane);

        binarioArea = new JTextArea();
        binarioArea.setEditable(false);
        JScrollPane binarioScrollPane = new JScrollPane(binarioArea);
        binarioScrollPane.setBorder(BorderFactory.createTitledBorder("Código Binario"));
        //resultsPanel.add(binarioScrollPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, resultsPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
        consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Consola"));
        consoleScrollPane.setPreferredSize(new Dimension(getWidth(), 150));

        add(consoleScrollPane, BorderLayout.SOUTH);
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
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(codeArea.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    public void setBajoNivelContent(String content) {
        bajoNivelArea.setText(content);
    }

    public void setBinarioContent(String content) {
        binarioArea.setText(content);
    }


    public void clearConsole() {
        consoleArea.setText("");
    }

    public void clearLexicoContent() {
        lexicoArea.setText("");
    }

    public void clearSintacticoContent() {
        sintacticoArea.setText("");
    }

    public void logToConsole(String message) {
        consoleArea.append(message + "\n");
    }

    public void clearConsoleArea() {
        consoleArea.setText("");
    }

    public void setLexicalButtonEnabled(boolean enabled) {
        parserButton.setEnabled(enabled);
    }

    public void setParserButtonEnabled(boolean enabled) {
        parserButton.setEnabled(enabled);
    }

}