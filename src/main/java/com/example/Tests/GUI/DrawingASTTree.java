package com.example.Tests.GUI;

import javax.swing.*;
import com.example.ASTNode;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * واجهة محسّنة لرسم شجرة AST - تعرض الشجرة كاملة مع جميع الأبناء
 */
public class DrawingASTTree extends JPanel {

    private ASTNode root;
    private final int NODE_WIDTH = 140;
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
    private final Color NODE_COLOR = new Color(59, 130, 246);
    private final Color NODE_BORDER = new Color(37, 99, 235);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color LINE_COLOR = new Color(148, 163, 184);
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public DrawingASTTree(ASTNode root) {
        this.root = root;

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
        int width = calculateWidth(root);

        // حساب العرض الفعلي بناءً على عدد الأوراق
        int leafCount = countLeaves(root);
        treeWidth = Math.max(1600, leafCount * (NODE_WIDTH + MIN_HORIZONTAL_SPACING));

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
     * حساب عرض الشجرة (عدد العقد في أوسع مستوى)
     */
    private int calculateWidth(ASTNode node) {
        if (node == null)
            return 0;

        // حساب عدد العقد في كل مستوى
        int[] levelCounts = new int[100];
        countNodesAtLevel(node, 0, levelCounts);

        // إيجاد أوسع مستوى
        int maxWidth = 0;
        for (int count : levelCounts) {
            maxWidth = Math.max(maxWidth, count);
        }

        return maxWidth;
    }

    private void countNodesAtLevel(ASTNode node, int level, int[] levelCounts) {
        if (node == null)
            return;

        levelCounts[level]++;
        for (ASTNode child : node.getChildren()) {
            countNodesAtLevel(child, level + 1, levelCounts);
        }
    }

    /**
     * عد الأوراق (العقد التي ليس لها أطفال)
     */
    private int countLeaves(ASTNode node) {
        if (node == null)
            return 0;

        List<ASTNode> children = node.getChildren();
        if (children.isEmpty()) {
            return 1;
        }

        int leafCount = 0;
        for (ASTNode child : children) {
            leafCount += countLeaves(child);
        }
        return leafCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // تفعيل Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // تطبيق التحويلات
        g2d.translate(offset.x, offset.y);
        g2d.scale(zoomLevel, zoomLevel);

        if (root != null) {
            // بدء الرسم من المنتصف
            int startX = treeWidth / 2;
            int startY = 60;
            int initialSpacing = calculateInitialSpacing(root);
            drawTree(g2d, root, startX, startY, initialSpacing, 0);
        }

        // رسم معلومات الـ Zoom
        g2d.setTransform(new AffineTransform());
        g2d.setColor(new Color(51, 65, 85));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String zoomText = String.format("Zoom: %.0f%% | Tree Size: %dx%d",
                zoomLevel * 100, treeWidth, treeHeight);
        g2d.drawString(zoomText, 10, getHeight() - 10);
    }

    private int calculateInitialSpacing(ASTNode node) {
        if (node == null)
            return MIN_HORIZONTAL_SPACING;

        int leafCount = countLeaves(node);
        int totalWidth = leafCount * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);

        return Math.max(MIN_HORIZONTAL_SPACING, totalWidth / Math.max(1, node.getChildren().size()));
    }

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

    private void drawTree(Graphics2D g, ASTNode node, int x, int y, int horizontalSpacing, int depth) {
        // رسم العقدة الحالية
        drawNode(g, node, x, y, depth);

        List<ASTNode> children = node.getChildren();
        if (children.isEmpty()) {
            return;
        }

        int childCount = children.size();

        // حساب المسافة بين الأطفال بناءً على عدد الأوراق لكل طفل
        int[] childWidths = new int[childCount];
        int totalWidth = 0;

        for (int i = 0; i < childCount; i++) {
            int childLeaves = countLeaves(children.get(i));
            childWidths[i] = childLeaves * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);
            totalWidth += childWidths[i];
        }

        // البدء من اليسار
        int startX = x - totalWidth / 2;
        int currentX = startX;

        // رسم الخطوط والأطفال
        for (int i = 0; i < childCount; i++) {
            ASTNode child = children.get(i);
            int childX = currentX + childWidths[i] / 2;
            int childY = y + VERTICAL_SPACING;

            // رسم الخط مع تأثير منحني
            drawCurvedLine(g, x, y + NODE_HEIGHT / 2, childX, childY - NODE_HEIGHT / 2);

            // رسم الطفل بشكل متكرر
            int childSpacing = childWidths[i] / Math.max(1, child.getChildren().size());
            drawTree(g, child, childX, childY, childSpacing, depth + 1);

            currentX += childWidths[i];
        }
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

    private void drawNode(Graphics2D g, ASTNode node, int x, int y, int depth) {
        int nodeX = x - NODE_WIDTH / 2;
        int nodeY = y - NODE_HEIGHT / 2;

        // رسم الظل
        g.setColor(SHADOW_COLOR);
        g.fillRoundRect(nodeX + 3, nodeY + 3, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // تدرج لوني حسب العمق
        Color nodeColor = getColorForDepth(depth);

        // رسم الخلفية بتدرج
        GradientPaint gradient = new GradientPaint(
                nodeX, nodeY, nodeColor,
                nodeX, nodeY + NODE_HEIGHT, nodeColor.darker());
        g.setPaint(gradient);
        g.fillRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // رسم الحدود
        g.setColor(NODE_BORDER);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, 20, 20);

        // رسم النص
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String text = node.getNodeType();

        // تقسيم النص إذا كان طويلاً
        if (text.length() > 22) {
            String line1 = text.substring(0, 22);
            String line2 = "...";

            FontMetrics fm = g.getFontMetrics();
            int textWidth1 = fm.stringWidth(line1);
            int textWidth2 = fm.stringWidth(line2);

            g.drawString(line1, x - textWidth1 / 2, y - 5);
            g.drawString(line2, x - textWidth2 / 2, y + 10);
        } else {
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int textX = x - textWidth / 2;
            int textY = y + textHeight / 4;

            g.drawString(text, textX, textY);
        }
    }

    private Color getColorForDepth(int depth) {
        Color[] colors = {
                new Color(59, 130, 246), // Blue
                new Color(16, 185, 129), // Green
                new Color(245, 158, 11), // Amber
                new Color(239, 68, 68), // Red
                new Color(168, 85, 247), // Purple
                new Color(236, 72, 153) // Pink
        };

        return colors[depth % colors.length];
    }

    /**
     * تصدير الشجرة كاملة إلى صورة PNG
     */
    public void exportToPNG(File file) throws IOException {
        // حساب الحجم الكامل للشجرة
        int width = treeWidth + 200;
        int height = treeHeight + 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // تفعيل Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // رسم الخلفية
        g2d.setColor(BG_COLOR);
        g2d.fillRect(0, 0, width, height);

        // رسم الشجرة كاملة
        int startX = treeWidth / 2 + 100;
        int startY = 100;
        int initialSpacing = calculateInitialSpacing(root);
        drawTree(g2d, root, startX, startY, initialSpacing, 0);

        g2d.dispose();

        // حفظ الصورة
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

        buildSVGTree(svg, root, startX, startY, initialSpacing, 0);

        svg.append("</svg>");

        java.nio.file.Files.write(file.toPath(), svg.toString().getBytes());
    }

    private void buildSVGTree(StringBuilder svg, ASTNode node, int x, int y, int spacing, int depth) {
        List<ASTNode> children = node.getChildren();

        // حساب مواقع الأطفال
        if (!children.isEmpty()) {
            int childCount = children.size();
            int[] childWidths = new int[childCount];
            int totalWidth = 0;

            for (int i = 0; i < childCount; i++) {
                int childLeaves = countLeaves(children.get(i));
                childWidths[i] = childLeaves * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);
                totalWidth += childWidths[i];
            }

            int startX = x - totalWidth / 2;
            int currentX = startX;

            // رسم الخطوط للأطفال
            for (int i = 0; i < childCount; i++) {
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
        Color nodeColor = getColorForDepth(depth);
        int nodeX = x - NODE_WIDTH / 2;
        int nodeY = y - NODE_HEIGHT / 2;

        svg.append(String.format(
                "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" rx=\"20\" fill=\"#%02x%02x%02x\" stroke=\"#%02x%02x%02x\" stroke-width=\"2.5\"/>\n",
                nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT,
                nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(),
                NODE_BORDER.getRed(), NODE_BORDER.getGreen(), NODE_BORDER.getBlue()));

        String text = node.getNodeType();
        if (text.length() > 22) {
            text = text.substring(0, 22) + "...";
        }

        svg.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-family=\"Segoe UI\" font-size=\"12\" font-weight=\"bold\" fill=\"white\" text-anchor=\"middle\">%s</text>\n",
                x, y + 5, escapeXml(text)));

        // رسم الأطفال
        if (!children.isEmpty()) {
            int childCount = children.size();
            int[] childWidths = new int[childCount];
            int totalWidth = 0;

            for (int i = 0; i < childCount; i++) {
                int childLeaves = countLeaves(children.get(i));
                childWidths[i] = childLeaves * (NODE_WIDTH + MIN_HORIZONTAL_SPACING);
                totalWidth += childWidths[i];
            }

            int startX = x - totalWidth / 2;
            int currentX = startX;

            for (int i = 0; i < childCount; i++) {
                ASTNode child = children.get(i);
                int childX = currentX + childWidths[i] / 2;
                int childY = y + VERTICAL_SPACING;
                int childSpacing = childWidths[i] / Math.max(1, child.getChildren().size());
                buildSVGTree(svg, child, childX, childY, childSpacing, depth + 1);
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
            zoomLevel = Math.min(zoomX, zoomY) * 0.95; // 95% للهوامش
            zoomLevel = Math.max(0.1, Math.min(zoomLevel, 5.0));

            offset = new Point(0, 0);
            updatePreferredSize();
            revalidate();
            repaint();
        }
    }
}