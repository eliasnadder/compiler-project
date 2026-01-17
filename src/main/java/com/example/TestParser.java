package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;

public class TestParser {

    private static void printTree(
            ParseTree tree,
            SQLParser parser,
            String prefix,
            boolean isLast,
            StringBuilder sb
    ) {
        if (tree == null) return;

        String nodeLabel;

        if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            String text = token.getText();
            String type = parser.getVocabulary().getSymbolicName(token.getType());

            // تجاهل الرموز اللي بتكعجق العرض فقط
            if (text.matches("[.,();]")) return;

            nodeLabel = "'" + text + "' <" + type + ">";
        } else {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            nodeLabel = ctx.getClass().getSimpleName()
                    .replace("Context", "");
        }

        sb.append(prefix)
          .append(isLast ? "└── " : "├── ")
          .append(nodeLabel)
          .append("\n");

        String childPrefix = prefix + (isLast ? "    " : "│   ");
        int childCount = tree.getChildCount();

        for (int i = 0; i < childCount; i++) {
            printTree(
                    tree.getChild(i),
                    parser,
                    childPrefix,
                    i == childCount - 1,
                    sb
            );
        }
    }

    public static void main(String[] args) throws Exception {

        String sqlInput = Files.readString(Paths.get("testing.sql"));

        CharStream input = CharStreams.fromString(sqlInput);
        SQLLexer lexer = new SQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLParser parser = new SQLParser(tokens);

        ParseTree tree = parser.sqlScript();

        StringBuilder sb = new StringBuilder();
        printTree(tree, parser, "", true, sb);

        String outputFile = "ParserOutput.txt";
        Files.writeString(Paths.get(outputFile), sb.toString());

        System.out.println("Parser output written to: " + outputFile);
    }
}
