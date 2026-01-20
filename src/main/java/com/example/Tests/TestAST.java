package com.example.Tests;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.example.ASTNode;
import com.example.AntlrVisitor;
import com.example.SQLLexer;
import com.example.SQLParser;
import com.example.Tests.GUI.DrawingASTTree;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.nio.file.*;
import java.awt.*;
import java.io.*;

/**
 * فئة اختبار الـ AST مع واجهة محسّنة
 */
public class TestAST {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        System.out.println("========================================");
        System.out.println("   SQL Parser - AST Visualizer");
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

        // بناء الـ AST باستخدام الـ Visitor
        System.out.println("🔄 Building AST...");
        AntlrVisitor visitor = new AntlrVisitor();
        ASTNode ast = visitor.visit(tree);

        if (ast == null) {
            System.err.println("⚠️ Warning: AST is null!");
            return;
        }

        System.out.println("✅ AST built successfully!\n");

        // طباعة الـ AST في terminal
        System.out.println("========== AST Structure ==========");
        ast.print();
        System.out.println("===================================\n");

        // حفظ الـ AST كـ text
        System.out.println("💾 Saving AST outputs...");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos)) {
            PrintStream old = System.out;
            System.setOut(ps);
            ast.print();
            System.out.flush();
            System.setOut(old);
            Files.writeString(Paths.get("AST_Output.txt"), baos.toString());
            System.out.println("✅ AST text saved to: AST_Output.txt");
        }

        // حفظ الـ AST كـ JSON
        String jsonOutput = ast.toJSON();
        Files.writeString(Paths.get("AST_Output.json"), jsonOutput);
        System.out.println("✅ AST JSON saved to: AST_Output.json\n");

        // عرض الواجهة الرسومية
        System.out.println("🎨 Opening graphical AST viewer...");
        SwingUtilities.invokeLater(() -> createAndShowGUI(ast));

        System.out.println("\n========================================");
        System.out.println("   AST Generation Complete!");
        System.out.println("========================================");
    }

    /**
     * إنشاء وعرض الواجهة الرسومية المحسّنة
     */
    private static void createAndShowGUI(ASTNode ast) {
        JFrame frame = new JFrame("SQL Parser - Abstract Syntax Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // إنشاء panel الرسم
        DrawingASTTree treePanel = new DrawingASTTree(ast);

        // إضافة scroll pane
        JScrollPane scrollPane = new JScrollPane(treePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // إنشاء شريط الأدوات العلوي
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // العنوان
        JLabel titleLabel = new JLabel("🌳 Abstract Syntax Tree (AST) Visualization", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));

        JLabel infoLabel = new JLabel(
                String.format("Root: %s | Total Nodes: %d | Depth: %d",
                        ast.getNodeType(),
                        countNodes(ast),
                        calculateDepth(ast)),
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
            fileChooser.setDialogTitle("Save AST as PNG");
            fileChooser.setSelectedFile(new File("AST_Tree.png"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                    treePanel.exportToPNG(file);
                    JOptionPane.showMessageDialog(frame,
                            "✅ AST exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting AST:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exportSvgBtn = createStyledButton("🎨 Export SVG", new Color(16, 185, 129));
        exportSvgBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save AST as SVG");
            fileChooser.setSelectedFile(new File("AST_Tree.svg"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".svg")) {
                        file = new File(file.getAbsolutePath() + ".svg");
                    }
                    treePanel.exportToSVG(file);
                    JOptionPane.showMessageDialog(frame,
                            "✅ AST exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting AST:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exportJsonBtn = createStyledButton("📄 Export JSON", new Color(245, 158, 11));
        exportJsonBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save AST as JSON");
            fileChooser.setSelectedFile(new File("AST_Export.json"));

            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".json")) {
                        file = new File(file.getAbsolutePath() + ".json");
                    }
                    Files.writeString(file.toPath(), ast.toJSON());
                    JOptionPane.showMessageDialog(frame,
                            "✅ AST exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Error exporting AST:\n" + ex.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton printBtn = createStyledButton("🖨️ Print Console", new Color(139, 92, 246));
        printBtn.addActionListener(e -> {
            System.out.println("\n========== AST Structure ==========");
            ast.print();
            System.out.println("===================================\n");
            JOptionPane.showMessageDialog(frame,
                    "✅ AST printed to console!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // إضافة الأزرار
        toolbarPanel.add(zoomInBtn);
        toolbarPanel.add(zoomOutBtn);
        toolbarPanel.add(resetViewBtn);
        toolbarPanel.add(fitWindowBtn);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(exportPngBtn);
        toolbarPanel.add(exportSvgBtn);
        toolbarPanel.add(exportJsonBtn);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(printBtn);

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

    private static int countNodes(ASTNode node) {
        if (node == null)
            return 0;
        int count = 1;
        for (ASTNode child : node.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }

    private static int calculateDepth(ASTNode node) {
        if (node == null || node.getChildren().isEmpty()) {
            return 1;
        }
        int maxDepth = 0;
        for (ASTNode child : node.getChildren()) {
            maxDepth = Math.max(maxDepth, calculateDepth(child));
        }
        return maxDepth + 1;
    }
}