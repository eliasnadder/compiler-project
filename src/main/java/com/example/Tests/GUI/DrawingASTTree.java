package com.example.Tests.GUI;

import javax.swing.*;

import com.example.ASTNode;

import java.awt.*;
import java.util.List;

/**
 * فئة لرسم شجرة AST بشكل رسومي باستخدام Swing
 * تدعم الأشجار العامة (n-ary trees) وليس فقط الثنائية
 */
public class DrawingASTTree extends JPanel {
    
    private ASTNode root;
    private final int NODE_WIDTH = 120;
    private final int NODE_HEIGHT = 40;
    private final int VERTICAL_SPACING = 80;
    private final int MIN_HORIZONTAL_SPACING = 30;
    
    public DrawingASTTree(ASTNode root) {
        this.root = root;
        setPreferredSize(new Dimension(1200, 800));
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // تفعيل anti-aliasing للحصول على رسم أفضل
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (root != null) {
            int startX = getWidth() / 2;
            int startY = 50;
            int initialSpacing = calculateInitialSpacing(root);
            drawTree(g2d, root, startX, startY, initialSpacing);
        }
    }
    
    /**
     * حساب المسافة الأفقية الأولية بناءً على عمق الشجرة
     */
    private int calculateInitialSpacing(ASTNode node) {
        int depth = calculateDepth(node);
        return Math.max(200, (int) Math.pow(2, depth - 1) * MIN_HORIZONTAL_SPACING);
    }
    
    /**
     * حساب عمق الشجرة
     */
    private int calculateDepth(ASTNode node) {
        if (node == null || node.getChildren().isEmpty()) {
            return 1;
        }
        
        int maxDepth = 0;
        for (ASTNode child : node.getChildren()) {
            maxDepth = Math.max(maxDepth, calculateDepth(child));
        }
        return maxDepth + 1;
    }
    
    /**
     * رسم الشجرة بشكل متكرر
     */
    private void drawTree(Graphics2D g, ASTNode node, int x, int y, int horizontalSpacing) {
        // رسم العقدة الحالية
        drawNode(g, node, x, y);
        
        List<ASTNode> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }
        
        int childCount = children.size();
        int totalWidth = (childCount - 1) * horizontalSpacing;
        int startX = x - totalWidth / 2;
        
        // رسم الخطوط والأطفال
        for (int i = 0; i < childCount; i++) {
            ASTNode child = children.get(i);
            int childX = startX + i * horizontalSpacing;
            int childY = y + VERTICAL_SPACING;
            
            // رسم الخط من العقدة الحالية إلى الطفل
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(2));
            g.drawLine(x, y + NODE_HEIGHT / 2, childX, childY - NODE_HEIGHT / 2);
            
            // رسم الطفل بشكل متكرر
            drawTree(g, child, childX, childY, horizontalSpacing / 2);
        }
    }
    
    /**
     * رسم عقدة واحدة
     */
    private void drawNode(Graphics2D g, ASTNode node, int x, int y) {
        int nodeX = x - NODE_WIDTH / 2;
        int nodeY = y - NODE_HEIGHT / 2;
        
        // رسم الخلفية
        g.setColor(new Color(70, 130, 180)); // Steel Blue
        g.fillRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 15, 15);
        
        // رسم الحدود
        g.setColor(new Color(25, 25, 112)); // Midnight Blue
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 15, 15);
        
        // رسم النص
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        
        String text = node.getNodeType();
        
        // تقسيم النص إذا كان طويلاً
        if (text.length() > 20) {
            text = text.substring(0, 17) + "...";
        }
        
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        int textX = x - textWidth / 2;
        int textY = y + textHeight / 4;
        
        g.drawString(text, textX, textY);
    }
}