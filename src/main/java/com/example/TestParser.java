package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;
import java.io.*;

public class TestParser {
    public static void main(String[] args) throws Exception {

        String sqlInput = Files.readString(Paths.get("testing.sql"));

        CharStream input = CharStreams.fromString(sqlInput);
        SQLLexer lexer = new SQLLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        SQLParser parser = new SQLParser(tokens);

        ParseTree tree = parser.sqlScript();

        String outputFile = "ParserOutput.txt";
        Files.writeString(
                Paths.get(outputFile),
                tree.toStringTree(parser)
        );

        System.out.println("Parser output written to: " + outputFile);
    }
}
