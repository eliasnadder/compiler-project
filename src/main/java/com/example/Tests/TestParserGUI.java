package com.example.Tests;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.example.SQLLexer;
import com.example.SQLParser;
import com.example.Tests.GUI.DrawingParseTree;

import javax.swing.*;
import java.nio.file.*;
import java.io.*;
import java.awt.*;

/**
 * فئة اختبار الـ Parser مع طباعة Parse Tree
 * تعرض النتائج في terminal وفي واجهة رسومية
 */
public class TestParserGUI {

    /**
     * طباعة الـ Parse Tree في terminal
     */
    private static void printParseTree(ParseTree tree, SQLParser parser, String prefix, boolean isTail,
            StringBuilder sb) {
        if (tree == null)
            return;

        String nodeName;

        if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            String type = parser.getVocabulary().getSymbolicName(token.getType());
            String text = token.getText();

            if (text.equals(".") || text.equals(",") || text.equals(";") ||
                    text.equals("(") || text.equals(")")) {
                return;
            }

            nodeName = text + " [" + type + "]";
        } else {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            nodeName = parser.getRuleNames()[ctx.getRuleIndex()];
        }

        sb.append(prefix + (isTail ? "└── " : "├── ") + nodeName + "\n");

        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            printParseTree(tree.getChild(i), parser, prefix + (isTail ? "    " : "│   "),
                    i == childCount - 1, sb);
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("   SQL Parser - Parse Tree Generator");
        System.out.println("========================================\n");

        // قراءة ملف SQL
        String inputFile = "train.sql";
        String sqlInput;

        try {
            sqlInput = Files.readString(Paths.get(inputFile));
            System.out.println("✅ Successfully read SQL from: " + inputFile);
            System.out.println("📝 SQL length: " + sqlInput.length() + " characters\n");
        } catch (IOException e) {
            System.err.println("❌ Error reading input file: " + e.getMessage());
            return;
        }

        // إنشاء الـ Lexer والـ Parser
        CharStream input = CharStreams.fromString(sqlInput);
        SQLLexer lexer = new SQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);

        // Parse the SQL
        System.out.println("🔄 Parsing SQL...");
        ParseTree tree = parser.sqlScript();

        // التحقق من وجود أخطاء
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("❌ Parsing failed with " + parser.getNumberOfSyntaxErrors() + " errors!");
            return;
        }

        System.out.println("✅ Parsing completed successfully!\n");

        // طباعة الـ Parse Tree في terminal
        System.out.println("========== Parse Tree Structure ==========");
        StringBuilder parseTreeOutput = new StringBuilder();
        printParseTree(tree, parser, "", true, parseTreeOutput);
        System.out.println(parseTreeOutput.toString());
        System.out.println("==========================================\n");

        // حفظ الـ Parse Tree إلى ملف
        System.out.println("💾 Saving Parse Tree output...");
        Files.writeString(Paths.get("ParseTree_Output.txt"), parseTreeOutput.toString());
        System.out.println("✅ Parse Tree saved to: ParseTree_Output.txt\n");

        // عرض الواجهة الرسومية
        System.out.println("🎨 Opening graphical Parse Tree viewer...");
        final ParseTree finalTree = tree;
        final SQLParser finalParser = parser;
        SwingUtilities.invokeLater(() -> createAndShowGUI(finalTree, finalParser));

        System.out.println("\n========================================");
        System.out.println("   Parse Tree Generation Complete!");
        System.out.println("========================================");
    }

    /**
     * إنشاء وعرض الواجهة الرسومية
     */
    private static void createAndShowGUI(ParseTree tree, SQLParser parser) {
        JFrame frame = new JFrame("SQL Parser - Parse Tree Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // إنشاء panel الرسم
        DrawingParseTree treePanel = new DrawingParseTree(tree, parser);

        // إضافة scroll pane
        JScrollPane scrollPane = new JScrollPane(treePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // إضافة معلومات في الأعلى
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Parse Tree Visualization", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));

        String rootName = tree instanceof ParserRuleContext
                ? parser.getRuleNames()[((ParserRuleContext) tree).getRuleIndex()]
                : "Terminal";

        JLabel infoLabel = new JLabel(
                "Root: " + rootName + " | Children: " + tree.getChildCount(),
                SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(70, 70, 70));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        // إضافة أزرار تحكم
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(240, 240, 240));

        JButton exportButton = new JButton("💾 Export to File");
        exportButton.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                printParseTree(tree, parser, "", true, sb);
                Files.writeString(Paths.get("ParseTree_Export.txt"), sb.toString());
                JOptionPane.showMessageDialog(frame,
                        "Parse Tree exported successfully to ParseTree_Export.txt!",
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Error exporting Parse Tree: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton printButton = new JButton("🖨️ Print to Console");
        printButton.addActionListener(e -> {
            System.out.println("\n========== Parse Tree Structure ==========");
            StringBuilder sb = new StringBuilder();
            printParseTree(tree, parser, "", true, sb);
            System.out.println(sb.toString());
            System.out.println("==========================================\n");
            JOptionPane.showMessageDialog(frame,
                    "Parse Tree printed to console!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Legend للألوان
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(new Color(240, 240, 240));

        JLabel legendLabel = new JLabel("Legend: ");
        legendLabel.setFont(new Font("Arial", Font.BOLD, 11));

        JLabel ruleLabel = new JLabel("■ Parser Rules");
        ruleLabel.setForeground(new Color(70, 130, 180));
        ruleLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel terminalLabel = new JLabel("■ Terminals");
        terminalLabel.setForeground(new Color(34, 139, 34));
        terminalLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        legendPanel.add(legendLabel);
        legendPanel.add(ruleLabel);
        legendPanel.add(Box.createHorizontalStrut(15));
        legendPanel.add(terminalLabel);

        controlPanel.add(exportButton);
        controlPanel.add(printButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(legendPanel);

        topPanel.add(controlPanel, BorderLayout.CENTER);

        // إضافة المكونات إلى النافذة
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ضبط حجم النافذة
        frame.setSize(1400, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println("✅ GUI window opened successfully!");
    }
}
