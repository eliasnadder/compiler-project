package com.example.Tests;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import com.example.SQLLexer;
import com.example.SQLParser;

import java.nio.file.*;

public class TestParser {
    private static void printTree(ParseTree tree, SQLParser parser, String prefix, boolean isTail, StringBuilder sb) {
        if (tree == null)
            return;

        String nodeName;

        if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            String type = parser.getVocabulary().getSymbolicName(token.getType());
            String text = token.getText();

            if (text.equals(".") || text.equals(",") || text.equals(";") || text.equals("(") || text.equals(")")) {
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
            printTree(tree.getChild(i), parser, prefix + (isTail ? "    " : "│   "), i == childCount - 1, sb);
        }
    }

    public static void main(String[] args) throws Exception {

        String sqlInput = Files.readString(Paths.get("train.sql"));

        CharStream input = CharStreams.fromString(sqlInput);
        SQLLexer lexer = new SQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);

        ParseTree tree = parser.sqlScript();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("❌ Parsing failed with " + parser.getNumberOfSyntaxErrors() + " errors!");
            return;
        }

        System.out.println("✅ Parsing completed successfully!\n");


        StringBuilder sb = new StringBuilder();
        printTree(tree, parser, "", true, sb);

        String outputFile = "ParserOutput.txt";
        Files.writeString(Paths.get(outputFile), sb.toString());

        System.out.println("Parser output written to: " + outputFile);
    }
}
