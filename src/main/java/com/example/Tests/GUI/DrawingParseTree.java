package com.example.Tests.GUI;


import javax.swing.*;
import java.awt.*;
import org.antlr.v4.runtime.tree.*;

import com.example.SQLParser;

import org.antlr.v4.runtime.*;

/**
 * فئة لرسم Parse Tree بشكل رسومي باستخدام Swing
 */
public class DrawingParseTree extends JPanel {
    
    private ParseTree root;
    private SQLParser parser;
    private final int NODE_WIDTH = 140;
    private final int NODE_HEIGHT = 40;
    private final int VERTICAL_SPACING = 80;
    private final int MIN_HORIZONTAL_SPACING = 30;
    
    public DrawingParseTree(ParseTree root, SQLParser parser) {
        this.root = root;
        this.parser = parser;
        setPreferredSize(new Dimension(1400, 900));
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (root != null) {
            int startX = getWidth() / 2;
            int startY = 50;
            int initialSpacing = calculateInitialSpacing(root);
            drawTree(g2d, root, startX, startY, initialSpacing);
        }
    }
    
    private int calculateInitialSpacing(ParseTree node) {
        int depth = calculateDepth(node);
        return Math.max(250, (int) Math.pow(2, depth - 1) * MIN_HORIZONTAL_SPACING);
    }
    
    private int calculateDepth(ParseTree node) {
        if (node == null || node.getChildCount() == 0) {
            return 1;
        }
        
        int maxDepth = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            maxDepth = Math.max(maxDepth, calculateDepth(node.getChild(i)));
        }
        return maxDepth + 1;
    }
    
    private void drawTree(Graphics2D g, ParseTree node, int x, int y, int horizontalSpacing) {
        // تخطي الرموز الصغيرة غير المهمة
        if (node instanceof TerminalNode) {
            Token token = ((TerminalNode) node).getSymbol();
            String text = token.getText();
            if (text.equals(".") || text.equals(",") || text.equals(";") || 
                text.equals("(") || text.equals(")")) {
                return;
            }
        }
        
        drawNode(g, node, x, y);
        
        int childCount = node.getChildCount();
        if (childCount == 0) {
            return;
        }
        
        // حساب عدد الأطفال المرئية (تخطي الرموز الصغيرة)
        int visibleChildren = 0;
        for (int i = 0; i < childCount; i++) {
            if (shouldDrawNode(node.getChild(i))) {
                visibleChildren++;
            }
        }
        
        if (visibleChildren == 0) {
            return;
        }
        
        int totalWidth = (visibleChildren - 1) * horizontalSpacing;
        int startX = x - totalWidth / 2;
        int currentChild = 0;
        
        for (int i = 0; i < childCount; i++) {
            ParseTree child = node.getChild(i);
            
            if (!shouldDrawNode(child)) {
                continue;
            }
            
            int childX = startX + currentChild * horizontalSpacing;
            int childY = y + VERTICAL_SPACING;
            
            g.setColor(new Color(100, 100, 100));
            g.setStroke(new BasicStroke(2));
            g.drawLine(x, y + NODE_HEIGHT / 2, childX, childY - NODE_HEIGHT / 2);
            
            drawTree(g, child, childX, childY, horizontalSpacing / 2);
            currentChild++;
        }
    }
    
    private boolean shouldDrawNode(ParseTree node) {
        if (node instanceof TerminalNode) {
            Token token = ((TerminalNode) node).getSymbol();
            String text = token.getText();
            return !(text.equals(".") || text.equals(",") || text.equals(";") || 
                    text.equals("(") || text.equals(")"));
        }
        return true;
    }
    
    private void drawNode(Graphics2D g, ParseTree node, int x, int y) {
        int nodeX = x - NODE_WIDTH / 2;
        int nodeY = y - NODE_HEIGHT / 2;
        
        String nodeName;
        Color bgColor;
        
        if (node instanceof TerminalNode) {
            Token token = ((TerminalNode) node).getSymbol();
            String type = parser.getVocabulary().getSymbolicName(token.getType());
            String text = token.getText();
            nodeName = text + " [" + type + "]";
            bgColor = new Color(34, 139, 34); // Forest Green للـ terminals
        } else {
            ParserRuleContext ctx = (ParserRuleContext) node;
            nodeName = parser.getRuleNames()[ctx.getRuleIndex()];
            bgColor = new Color(70, 130, 180); // Steel Blue للـ rules
        }
        
        // رسم الخلفية
        g.setColor(bgColor);
        g.fillRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 15, 15);
        
        // رسم الحدود
        g.setColor(bgColor.darker());
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 15, 15);
        
        // رسم النص
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        
        if (nodeName.length() > 25) {
            nodeName = nodeName.substring(0, 22) + "...";
        }
        
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(nodeName);
        int textHeight = fm.getHeight();
        
        int textX = x - textWidth / 2;
        int textY = y + textHeight / 4;
        
        g.drawString(nodeName, textX, textY);
    }
}