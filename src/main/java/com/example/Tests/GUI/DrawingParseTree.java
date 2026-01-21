package com.example.Tests.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.antlr.v4.runtime.tree.*;
import com.example.SQLParser;
import org.antlr.v4.runtime.*;
import javax.imageio.ImageIO;

/**
 * واجهة محسّنة لرسم Parse Tree - تعرض الشجرة كاملة مع جميع الأبناء
 */
public class DrawingParseTree extends JPanel {

    private ParseTree root;
    private SQLParser parser;
    private final int NODE_WIDTH = 160;
    private final int NODE_HEIGHT = 50;
    private final int VERTICAL_SPACING = 100;
    private final int MIN_HORIZONTAL_SPACING = 40;

    // Zoom settings
    private double zoomLevel = 1.0;
    private Point dragStart;
    private Point offset = new Point(0, 0);

    // حجم الشجرة الفعلي
    private int treeWidth;
    private int treeHeight;

    // Colors
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color RULE_COLOR = new Color(59, 130, 246);
    private final Color TERMINAL_COLOR = new Color(16, 185, 129);
    private final Color RULE_BORDER = new Color(37, 99, 235);
    private final Color TERMINAL_BORDER = new Color(5, 150, 105);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color LINE_COLOR = new Color(148, 163, 184);
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public DrawingParseTree(ParseTree root, SQLParser parser) {
        this.root = root;
        this.parser = parser;

        // حساب الحجم الفعلي للشجرة
        calculateTreeDimensions();

        // ضبط الحجم المفضل ليشمل الشجرة كاملة
        setPreferredSize(new Dimension(treeWidth + 200, treeHeight + 200));
        setBackground(BG_COLOR);

        // إضافة Zoom
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomLevel *= 1.1;
                } else {
                    zoomLevel /= 1.1;
                }
                zoomLevel = Math.max(0.1, Math.min(zoomLevel, 5.0));

                // تحديث الحجم المفضل مع الـ Zoom
                updatePreferredSize();
                revalidate();
                repaint();
            }
        });

        // إضافة السحب
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    offset.x += e.getX() - dragStart.x;
                    offset.y += e.getY() - dragStart.y;
                    dragStart = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * حساب أبعاد الشجرة الفعلية
     */
    private void calculateTreeDimensions() {
        if (root == null) {
            treeWidth = 800;
            treeHeight = 600;
            return;
        }

        int depth = calculateDepth(root);

        // حساب عدد الأوراق المرئية
        int leafCount = countVisibleLeaves(root);
        treeWidth = Math.max(1800, leafCount * (NODE_WIDTH + MIN_HORIZONTAL_SPACING));

        // حساب الارتفاع بناءً على العمق
        treeHeight = depth * (NODE_HEIGHT + VERTICAL_SPACING) + 200;
    }

    /**
     * تحديث الحجم المفضل مع الـ Zoom
     */
    private void updatePreferredSize() {
        int newWidth = (int) (treeWidth * zoomLevel) + 200;
        int newHeight = (int) (treeHeight * zoomLevel) + 200;
        setPreferredSize(new Dimension(newWidth, newHeight));
    }

    /**
     * عد الأوراق المرئية (التي سيتم رسمها)
     */
    private int countVisibleLeaves(ParseTree node) {
        if (node == null)
            return 0;

        if (!shouldDrawNode(node))
            return 0;

        int childCount = 0;
        int visibleChildren = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (shouldDrawNode(node.getChild(i))) {
                childCount++;
                visibleChildren += countVisibleLeaves(node.getChild(i));
            }
        }

        return childCount == 0 ? 1 : visibleChildren;
    }

    private int calculateDepth(ParseTree node) {
        if (node == null)
            return 0;

        if (!shouldDrawNode(node))
            return 0;

        int maxDepth = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (shouldDrawNode(node.getChild(i))) {
                maxDepth = Math.max(maxDepth, calculateDepth(node.getChild(i)));
            }
        }
        return maxDepth + 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // تطبيق التحويلات
        g2d.translate(offset.x, offset.y);
        g2d.scale(zoomLevel, zoomLevel);

        if (root != null) {
            int startX = treeWidth / 2;
            int startY = 60;
            int initialSpacing = calculateInitialSpacing(root);
            drawTree(g2d, root, startX, startY, initialSpacing);
        }

        // رسم معلومات الـ Zoom
        g2d.setTransform(new AffineTransform());
        g2d.setColor(new Color(51, 65, 85));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String zoomText = String.format("Zoom: %.0f%% | Tree Size: %dx%d",
                zoomLevel * 100, treeWidth, treeHeight);
        g2d.drawString(zoomText, 10, getHeight() - 10);
    }

    private int calculateInitialSpacing(ParseTree node) {
        if (node == null)
            return MIN_HORIZONTAL_SPACING;

        int leafCount = countVisibleLeaves(node);
        int totalWidth = leafCount * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);

        int visibleChildren = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (shouldDrawNode(node.getChild(i))) {
                visibleChildren++;
            }
        }

        return Math.max(MIN_HORIZONTAL_SPACING, totalWidth / Math.max(1, visibleChildren));
    }

    private void drawTree(Graphics2D g, ParseTree node, int x, int y, int horizontalSpacing) {
        // تخطي الرموز الصغيرة
        if (!shouldDrawNode(node)) {
            return;
        }

        drawNode(g, node, x, y);

        int childCount = node.getChildCount();
        if (childCount == 0) {
            return;
        }

        // حساب الأطفال المرئية
        int visibleChildren = 0;
        int[] childWidths = new int[childCount];
        int totalWidth = 0;

        for (int i = 0; i < childCount; i++) {
            if (shouldDrawNode(node.getChild(i))) {
                int childLeaves = countVisibleLeaves(node.getChild(i));
                childWidths[i] = childLeaves * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);
                totalWidth += childWidths[i];
                visibleChildren++;
            }
        }

        if (visibleChildren == 0) {
            return;
        }

        int startX = x - totalWidth / 2;
        int currentX = startX;

        for (int i = 0; i < childCount; i++) {
            ParseTree child = node.getChild(i);

            if (!shouldDrawNode(child)) {
                continue;
            }

            int childX = currentX + childWidths[i] / 2;
            int childY = y + VERTICAL_SPACING;

            // رسم خط منحني
            drawCurvedLine(g, x, y + NODE_HEIGHT / 2, childX, childY - NODE_HEIGHT / 2);

            int childSpacing = childWidths[i] / Math.max(1, countVisibleChildren(child));
            drawTree(g, child, childX, childY, childSpacing);

            currentX += childWidths[i];
        }
    }

    private int countVisibleChildren(ParseTree node) {
        int count = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            if (shouldDrawNode(node.getChild(i))) {
                count++;
            }
        }
        return count;
    }

    private void drawCurvedLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(LINE_COLOR);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D path = new Path2D.Double();
        path.moveTo(x1, y1);

        int ctrlY = (y1 + y2) / 2;
        path.curveTo(x1, ctrlY, x2, ctrlY, x2, y2);

        g.draw(path);
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
        Color borderColor;

        if (node instanceof TerminalNode) {
            Token token = ((TerminalNode) node).getSymbol();
            String type = parser.getVocabulary().getSymbolicName(token.getType());
            String text = token.getText();
            nodeName = text + " [" + type + "]";
            bgColor = TERMINAL_COLOR;
            borderColor = TERMINAL_BORDER;
        } else {
            ParserRuleContext ctx = (ParserRuleContext) node;
            nodeName = parser.getRuleNames()[ctx.getRuleIndex()];
            bgColor = RULE_COLOR;
            borderColor = RULE_BORDER;
        }

        // رسم الظل
        g.setColor(SHADOW_COLOR);
        g.fillRoundRect(nodeX + 3, nodeY + 3, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // رسم الخلفية بتدرج
        GradientPaint gradient = new GradientPaint(
                nodeX, nodeY, bgColor,
                nodeX, nodeY + NODE_HEIGHT, bgColor.darker());
        g.setPaint(gradient);
        g.fillRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // رسم الحدود
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // رسم النص
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Segoe UI", Font.BOLD, 11));

        if (nodeName.length() > 28) {
            String line1 = nodeName.substring(0, 28);
            String line2 = "...";

            FontMetrics fm = g.getFontMetrics();
            int textWidth1 = fm.stringWidth(line1);
            int textWidth2 = fm.stringWidth(line2);

            g.drawString(line1, x - textWidth1 / 2, y - 5);
            g.drawString(line2, x - textWidth2 / 2, y + 10);
        } else {
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(nodeName);
            int textHeight = fm.getHeight();

            int textX = x - textWidth / 2;
            int textY = y + textHeight / 4;

            g.drawString(nodeName, textX, textY);
        }
    }

    /**
     * تصدير الشجرة كاملة إلى صورة PNG
     */
    public void exportToPNG(File file) throws IOException {
        int width = treeWidth + 200;
        int height = treeHeight + 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setColor(BG_COLOR);
        g2d.fillRect(0, 0, width, height);

        int startX = treeWidth / 2 + 100;
        int startY = 100;
        int initialSpacing = calculateInitialSpacing(root);
        drawTree(g2d, root, startX, startY, initialSpacing);

        g2d.dispose();

        ImageIO.write(image, "PNG", file);
    }

    /**
     * تصدير الشجرة كاملة إلى صورة SVG
     */
    public void exportToSVG(File file) throws IOException {
        int width = treeWidth + 200;
        int height = treeHeight + 200;

        StringBuilder svg = new StringBuilder();
        svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        svg.append(String.format("<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">\n",
                width, height));
        svg.append(String.format("<rect width=\"%d\" height=\"%d\" fill=\"#%02x%02x%02x\"/>\n",
                width, height, BG_COLOR.getRed(), BG_COLOR.getGreen(), BG_COLOR.getBlue()));

        int startX = treeWidth / 2 + 100;
        int startY = 100;
        int initialSpacing = calculateInitialSpacing(root);

        buildSVGTree(svg, root, startX, startY, initialSpacing);

        svg.append("</svg>");

        java.nio.file.Files.write(file.toPath(), svg.toString().getBytes());
    }

    private void buildSVGTree(StringBuilder svg, ParseTree node, int x, int y, int spacing) {
        if (!shouldDrawNode(node)) {
            return;
        }

        int childCount = node.getChildCount();
        int visibleChildren = 0;
        int[] childWidths = new int[childCount];
        int totalWidth = 0;

        for (int i = 0; i < childCount; i++) {
            if (shouldDrawNode(node.getChild(i))) {
                int childLeaves = countVisibleLeaves(node.getChild(i));
                childWidths[i] = childLeaves * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);
                totalWidth += childWidths[i];
                visibleChildren++;
            }
        }

        // رسم الخطوط
        if (visibleChildren > 0) {
            int startX = x - totalWidth / 2;
            int currentX = startX;

            for (int i = 0; i < childCount; i++) {
                if (!shouldDrawNode(node.getChild(i)))
                    continue;

                int childX = currentX + childWidths[i] / 2;
                int childY = y + VERTICAL_SPACING;

                svg.append(String.format(
                        "<path d=\"M %d %d Q %d %d %d %d\" stroke=\"#%02x%02x%02x\" stroke-width=\"2.5\" fill=\"none\"/>\n",
                        x, y + NODE_HEIGHT / 2,
                        x, (y + childY) / 2,
                        childX, childY - NODE_HEIGHT / 2,
                        LINE_COLOR.getRed(), LINE_COLOR.getGreen(), LINE_COLOR.getBlue()));

                currentX += childWidths[i];
            }
        }

        // رسم العقدة
        String nodeName;
        Color bgColor;
        Color borderColor;

        if (node instanceof TerminalNode) {
            Token token = ((TerminalNode) node).getSymbol();
            String type = parser.getVocabulary().getSymbolicName(token.getType());
            String text = token.getText();
            nodeName = text + " [" + type + "]";
            bgColor = TERMINAL_COLOR;
            borderColor = TERMINAL_BORDER;
        } else {
            ParserRuleContext ctx = (ParserRuleContext) node;
            nodeName = parser.getRuleNames()[ctx.getRuleIndex()];
            bgColor = RULE_COLOR;
            borderColor = RULE_BORDER;
        }

        int nodeX = x - NODE_WIDTH / 2;
        int nodeY = y - NODE_HEIGHT / 2;

        svg.append(String.format(
                "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" rx=\"20\" fill=\"#%02x%02x%02x\" stroke=\"#%02x%02x%02x\" stroke-width=\"2.5\"/>\n",
                nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT,
                bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
                borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue()));

        if (nodeName.length() > 28) {
            nodeName = nodeName.substring(0, 28) + "...";
        }

        svg.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"Segoe UI\" font-size=\"11\" font-weight=\"bold\" fill=\"white\" text-anchor=\"middle\">%s</text>\n",
                x, y + 5, escapeXml(nodeName)));

        // رسم الأطفال
        if (visibleChildren > 0) {
            int startX = x - totalWidth / 2;
            int currentX = startX;

            for (int i = 0; i < childCount; i++) {
                ParseTree child = node.getChild(i);
                if (!shouldDrawNode(child))
                    continue;

                int childX = currentX + childWidths[i] / 2;
                int childY = y + VERTICAL_SPACING;
                int childSpacing = childWidths[i] / Math.max(1, countVisibleChildren(child));
                buildSVGTree(svg, child, childX, childY, childSpacing);
                currentX += childWidths[i];
            }
        }
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public void resetView() {
        zoomLevel = 1.0;
        offset = new Point(0, 0);
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public void zoomIn() {
        zoomLevel *= 1.2;
        zoomLevel = Math.min(zoomLevel, 5.0);
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public void zoomOut() {
        zoomLevel /= 1.2;
        zoomLevel = Math.max(zoomLevel, 0.1);
        updatePreferredSize();
        revalidate();
        repaint();
    }

    /**
     * ضبط الـ Zoom ليناسب النافذة (Fit to Window)
     */
    public void fitToWindow() {
        Container parent = getParent();
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            double zoomX = (double) parentSize.width / (treeWidth + 200);
            double zoomY = (double) parentSize.height / (treeHeight + 200);
            zoomLevel = Math.min(zoomX, zoomY) * 0.95;
            zoomLevel = Math.max(0.1, Math.min(zoomLevel, 5.0));

            offset = new Point(0, 0);
            updatePreferredSize();
            revalidate();
            repaint();
        }
    }
}