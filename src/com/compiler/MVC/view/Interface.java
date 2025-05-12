package com.compiler.MVC.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Interface extends JFrame {

    private JTextArea codeArea;
    private JTextArea lexicoArea;
    private JTextArea lowLevelArea;
    private JTextArea objectCodeArea;
    private JPanel sintacticoSemanticPanel;
    private JTextArea consoleArea;
    private JButton analyzeButton;
    private JButton parserButton;
    private JButton semanticButton;
    private JButton lowLevelButton;
    private JButton objectCodeButton;

    private JLabel sintacticLabel;
    private JLabel semanticLabel;

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
        lowLevelButton = new JButton("Código de Bajo Nivel");
        objectCodeButton = new JButton("Código Objeto");
        buttonPanel.add(analyzeButton);
        buttonPanel.add(parserButton);
        buttonPanel.add(semanticButton);
        buttonPanel.add(lowLevelButton);
        buttonPanel.add(objectCodeButton);

        add(buttonPanel, BorderLayout.NORTH);

        codeArea = new JTextArea();
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setBorder(BorderFactory.createTitledBorder("Código Fuente"));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        lexicoArea = new JTextArea();
        lexicoArea.setEditable(false);
        JScrollPane lexicoScrollPane = new JScrollPane(lexicoArea);
        lexicoScrollPane.setBorder(BorderFactory.createTitledBorder("Análisis Léxico"));
        lexicoScrollPane.setPreferredSize(new Dimension(0, 120));
        leftPanel.add(lexicoScrollPane);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        sintacticoSemanticPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        sintacticLabel = new JLabel();
        sintacticLabel.setBorder(BorderFactory.createTitledBorder("Análisis Sintáctico"));
        semanticLabel = new JLabel();
        semanticLabel.setBorder(BorderFactory.createTitledBorder("Análisis Semántico"));
        sintacticoSemanticPanel.add(sintacticLabel);
        sintacticoSemanticPanel.add(semanticLabel);
        sintacticoSemanticPanel.setPreferredSize(new Dimension(0, 100));
        leftPanel.add(sintacticoSemanticPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        lowLevelArea = new JTextArea();
        lowLevelArea.setEditable(false);
        lowLevelArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane lowLevelScrollPane = new JScrollPane(lowLevelArea);
        lowLevelScrollPane.setBorder(BorderFactory.createTitledBorder("Código de Bajo Nivel"));
        lowLevelScrollPane.setPreferredSize(new Dimension(0, 300));
        leftPanel.add(lowLevelScrollPane);


        objectCodeArea = new JTextArea();
        objectCodeArea.setEditable(false);
        objectCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane objectCodeScrollPane = new JScrollPane(objectCodeArea);
        objectCodeScrollPane.setBorder(BorderFactory.createTitledBorder("Código Objeto"));


        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, objectCodeScrollPane);
        horizontalSplit.setResizeWeight(0.4);
        horizontalSplit.setDividerLocation(0.4);


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, horizontalSplit);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerLocation(0.3);

        add(splitPane, BorderLayout.CENTER);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Arial", Font.BOLD, 14));
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

    public void setLowLevelButtonListener(ActionListener listener) {
        lowLevelButton.addActionListener(listener);
    }

    public void setObjectCodeButtonListener(ActionListener listener) {
        objectCodeButton.addActionListener(listener);
    }

    public String getCode() {
        return codeArea.getText();
    }

    public void setLexicoContent(String content, boolean lexicalCorrect) {
        lexicoArea.setText(content);
        lexicoArea.setForeground(lexicalCorrect ? Color.BLACK : Color.RED);
    }

    public void clearLexicoContent() {
        lexicoArea.setText("");
    }

    public void setLowLevelContent(String content){
        lowLevelArea.setText(content);
    }

    public void clearLowLevelContent(){
        lowLevelArea.setText("");
    }

    public void setObjectCodeContent(String content){
        objectCodeArea.setText(content);
    }

    public void clearObjectCodeContent(){
        objectCodeArea.setText("");
    }

    public void logToConsole(String message) {
        consoleArea.append(message + "\n");
    }

    public void clearConsoleArea() {
        consoleArea.setText("");
    }

    public void setParserButtonEnabled(boolean enabled) {
        parserButton.setEnabled(enabled);
    }

    public void setSemanticButtonEnabled(boolean enabled){
        semanticButton.setEnabled(enabled);
    }

    public void setLowLevelButtonEnabled(boolean enabled){
        lowLevelButton.setEnabled(enabled);
    }

    public void setObjectCodeButtonEnabled(boolean enabled){
        objectCodeButton.setEnabled(enabled);
    }

    public void setSintacticoColor(boolean isValid) {
        sintacticLabel.setOpaque(true);
        sintacticLabel.setBackground(isValid ? new Color(176, 255, 208) : new Color(253, 115, 115));
    }

    public void setSemanticColor(boolean isValid) {
        semanticLabel.setOpaque(true);
        semanticLabel.setBackground(isValid ? new Color(176, 255, 208) : new Color(253, 115, 115));
    }

    public void clearParserSemanticLabels(){
        semanticLabel.setBackground(null);
        sintacticLabel.setBackground(null);
    }
}
