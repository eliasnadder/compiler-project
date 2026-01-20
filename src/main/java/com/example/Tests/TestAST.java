package com.example.Tests;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.example.ASTNode;
import com.example.AntlrVisitor;
import com.example.SQLLexer;
import com.example.SQLParser;
import com.example.Tests.GUI.DrawingASTTree;

import javax.swing.*;
import java.nio.file.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.*;

/**
 * فئة اختبار الـ AST مع الواجهة الرسومية
 * تقوم ببناء AST من SQL وعرضها بشكل رسومي
 */
public class TestAST {
    
    public static void main(String[] args) throws Exception {
        
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
     * إنشاء وعرض الواجهة الرسومية
     */
    private static void createAndShowGUI(ASTNode ast) {
        JFrame frame = new JFrame("SQL Parser - Abstract Syntax Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // إنشاء panel الرسم
        DrawingASTTree treePanel = new DrawingASTTree(ast);
        
        // إضافة scroll pane للتمكن من التحرك في الشجرة الكبيرة
        JScrollPane scrollPane = new JScrollPane(treePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // إضافة معلومات في الأعلى
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Abstract Syntax Tree (AST) Visualization", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));
        
        JLabel infoLabel = new JLabel(
            "Root: " + ast.getNodeType() + " | Children: " + ast.getChildren().size(),
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(70, 70, 70));
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);
        
        // إضافة أزرار تحكم
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(240, 240, 240));
        
        JButton exportButton = new JButton("💾 Export JSON");
        exportButton.addActionListener(e -> {
            try {
                Files.writeString(Paths.get("AST_Export.json"), ast.toJSON());
                JOptionPane.showMessageDialog(frame, 
                    "AST exported successfully to AST_Export.json!", 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Error exporting AST: " + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton printButton = new JButton("🖨️ Print to Console");
        printButton.addActionListener(e -> {
            System.out.println("\n========== AST Structure ==========");
            ast.print();
            System.out.println("===================================\n");
            JOptionPane.showMessageDialog(frame, 
                "AST printed to console!", 
                "Print Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        controlPanel.add(exportButton);
        controlPanel.add(printButton);
        
        topPanel.add(controlPanel, BorderLayout.CENTER);
        
        // إضافة المكونات إلى النافذة
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // ضبط حجم النافذة
        frame.setSize(1400, 900);
        frame.setLocationRelativeTo(null); // توسيط النافذة
        frame.setVisible(true);
        
        System.out.println("✅ GUI window opened successfully!");
    }
}