package com.example.Tests;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.example.SQLLexer;
import com.example.SQLParser;
import com.example.Tests.GUI.DrawingParseTree;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.nio.file.*;
import java.io.*;
import java.awt.*;

/**
 * فئة اختبار الـ Parser مع واجهة محسّنة
 */
public class TestParserGUI {

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
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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
     * إنشاء وعرض الواجهة الرسومية المحسّنة
     */
    private static void createAndShowGUI(ParseTree tree, SQLParser parser) {
        JFrame frame = new JFrame("SQL Parser - Parse Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // إنشاء panel الرسم
        DrawingParseTree treePanel = new DrawingParseTree(tree, parser);

        // إضافة scroll pane
        JScrollPane scrollPane = new JScrollPane(treePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // إنشاء شريط الأدوات العلوي
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // العنوان
        String rootName = tree instanceof ParserRuleContext
                ? parser.getRuleNames()[((ParserRuleContext) tree).getRuleIndex()]
                : "Terminal";

        JLabel titleLabel = new JLabel("🌲 Parse Tree Visualization", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));

        JLabel infoLabel = new JLabel(
                String.format("Root: %s | Total Nodes: %d | Depth: %d",
                        rootName,
                        countNodes(tree),
                        calculateDepth(tree)),
                SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(70, 70, 70));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(infoLabel, BorderLayout.SOUTH);

        // شريط الأدوات
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        toolbarPanel.setBackground(new Color(240, 240, 240));

        // أزرار التحكم بالـ Zoom
        JButton zoomInBtn = createStyledButton("🔍+ Zoom In", new Color(59, 130, 246));
        zoomInBtn.addActionListener(e -> treePanel.zoomIn());

        JButton zoomOutBtn = createStyledButton("🔍- Zoom Out", new Color(59, 130, 246));
        zoomOutBtn.addActionListener(e -> treePanel.zoomOut());

        JButton resetViewBtn = createStyledButton("🎯 Reset View", new Color(99, 102, 241));
        resetViewBtn.addActionListener(e -> treePanel.resetView());

        JButton fitWindowBtn = createStyledButton("📐 Fit to Window", new Color(139, 92, 246));
        fitWindowBtn.addActionListener(e -> treePanel.fitToWindow());

        // أزرار التصدير
        JButton exportPngBtn = createStyledButton("📸 Export PNG", new Color(16, 185, 129));
        exportPngBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Parse Tree as PNG");
            fileChooser.setSelectedFile(new File("ParseTree.png"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                    treePanel.exportToPNG(file);
                    JOptionPane.showMessageDialog(frame,
                            "✅ Parse Tree exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting Parse Tree:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exportSvgBtn = createStyledButton("🎨 Export SVG", new Color(16, 185, 129));
        exportSvgBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Parse Tree as SVG");
            fileChooser.setSelectedFile(new File("ParseTree.svg"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".svg")) {
                        file = new File(file.getAbsolutePath() + ".svg");
                    }
                    treePanel.exportToSVG(file);
                    JOptionPane.showMessageDialog(frame,
                            "✅ Parse Tree exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting Parse Tree:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exportTextBtn = createStyledButton("📄 Export Text", new Color(245, 158, 11));
        exportTextBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Parse Tree as Text");
            fileChooser.setSelectedFile(new File("ParseTree_Export.txt"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".txt")) {
                        file = new File(file.getAbsolutePath() + ".txt");
                    }
                    StringBuilder sb = new StringBuilder();
                    printParseTree(tree, parser, "", true, sb);
                    Files.writeString(file.toPath(), sb.toString());
                    JOptionPane.showMessageDialog(frame,
                            "✅ Parse Tree exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting Parse Tree:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton printBtn = createStyledButton("🖨️ Print Console", new Color(139, 92, 246));
        printBtn.addActionListener(e -> {
            System.out.println("\n========== Parse Tree Structure ==========");
            StringBuilder sb = new StringBuilder();
            printParseTree(tree, parser, "", true, sb);
            System.out.println(sb.toString());
            System.out.println("==========================================\n");
            JOptionPane.showMessageDialog(frame,
                    "✅ Parse Tree printed to console!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Legend للألوان
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legendPanel.setBackground(new Color(240, 240, 240));

        JLabel legendLabel = new JLabel("Legend:");
        legendLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel ruleLabel = new JLabel("■ Parser Rules");
        ruleLabel.setForeground(new Color(59, 130, 246));
        ruleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel terminalLabel = new JLabel("■ Terminals");
        terminalLabel.setForeground(new Color(16, 185, 129));
        terminalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        legendPanel.add(legendLabel);
        legendPanel.add(ruleLabel);
        legendPanel.add(terminalLabel);

        // إضافة الأزرار
        toolbarPanel.add(zoomInBtn);
        toolbarPanel.add(zoomOutBtn);
        toolbarPanel.add(resetViewBtn);
        toolbarPanel.add(fitWindowBtn);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(exportPngBtn);
        toolbarPanel.add(exportSvgBtn);
        toolbarPanel.add(exportTextBtn);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(printBtn);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(legendPanel);

        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(toolbarPanel, BorderLayout.CENTER);

        // شريط المساعدة السفلي
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        helpPanel.setBackground(new Color(250, 250, 250));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel helpLabel = new JLabel(
                "💡 Tips: Use mouse wheel to zoom | Drag to pan | Click 📐 Fit to Window to see full tree");
        helpLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        helpLabel.setForeground(new Color(100, 100, 100));
        helpPanel.add(helpLabel);

        // تجميع المكونات
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(helpPanel, BorderLayout.SOUTH);

        // ضبط النافذة
        frame.setSize(1500, 950);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println("✅ GUI window opened successfully!");
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private static int countNodes(ParseTree node) {
        if (node == null)
            return 0;
        int count = 1;
        for (int i = 0; i < node.getChildCount(); i++) {
            count += countNodes(node.getChild(i));
        }
        return count;
    }

    private static int calculateDepth(ParseTree node) {
        if (node == null || node.getChildCount() == 0) {
            return 1;
        }
        int maxDepth = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            maxDepth = Math.max(maxDepth, calculateDepth(node.getChild(i)));
        }
        return maxDepth + 1;
    }
}