package com.example;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {

    // قائمة الأبناء لهذه العقدة
    protected List<ASTNode> children;

    // رقم السطر في الكود المصدري
    protected int lineNumber;

    // رقم العمود في الكود المصدري
    protected int columnNumber;

    public ASTNode() {
        this.children = new ArrayList<>();
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public ASTNode(int lineNumber, int columnNumber) {
        this.children = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    // إضافة ابن لهذه العقدة
    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    // إضافة عدة أبناء
    public void addChildren(List<ASTNode> nodes) {
        if (nodes != null) {
            for (ASTNode node : nodes) {
                addChild(node);
            }
        }
    }

    // احضار اﻷبناء
    public List<ASTNode> getChildren() {
        return children;
    }

    // احضار رقم السطر
    public int getLineNumber() {
        return lineNumber;
    }

    // احضار رقم العمود
    public int getColumnNumber() {
        return columnNumber;
    }

    // احضار اسم نوع العقدة يجب تنفيذه في كل صف ابن
    public abstract String getNodeType();

    // تابع الطباعة - يطبع الشجرة بشكل هرمي
    public void print() {
        print("", true);
    }

    // تابع الطباعة الداخلي (recursive)
    protected void print(String prefix, boolean isTail) {
        // طباعة العقدة الحالية
        System.out.println(prefix + (isTail ? "└── " : "├── ") + toString());

        // طباعة الأبناء
        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            children.get(i).print(prefix + (isTail ? "    " : "│   "), isLast);
        }
    }

    @Override
    public String toString() {
        return getNodeType();
    }

    // تابع لتحويل الشجرة إلى JSON
    public String toJSON() {
        return toJSON(0);
    }

    // تابع JSON الداخلي
    protected String toJSON(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);

        sb.append(indentStr).append("{\n");
        sb.append(indentStr).append("  \"type\": \"").append(getNodeType()).append("\",\n");

        if (lineNumber >= 0) {
            sb.append(indentStr).append("  \"line\": ").append(lineNumber).append(",\n");
        }

        if (!children.isEmpty()) {
            sb.append(indentStr).append("  \"children\": [\n");
            for (int i = 0; i < children.size(); i++) {
                sb.append(children.get(i).toJSON(indent + 2));
                if (i < children.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(indentStr).append("  ]\n");
        } else {
            sb.append(indentStr).append("  \"children\": []\n");
        }

        sb.append(indentStr).append("}");
        return sb.toString();
    }
}