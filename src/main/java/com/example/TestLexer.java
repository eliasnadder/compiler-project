package com.example;

import org.antlr.v4.runtime.*;
import java.io.*;
import java.nio.file.*;

public class TestLexer {
    public static void main(String[] args) throws Exception {
        String inputFilePath = "train.sql";
        String outputFilePath = "output.txt";

        String sqlInput;
        try {
            sqlInput = Files.readString(Paths.get(inputFilePath));
            System.out.println("Successfully read input from: " + inputFilePath);
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            CharStream stream = CharStreams.fromString(sqlInput);
            SQLLexer lexer = new SQLLexer(stream);

            writer.write("Tokens Found:\n");
            writer.write("-".repeat(80) + "\n");
            writer.write(String.format("%-5s | %-25s | %s\n", "No.", "Token Type", "Text"));
            writer.write("-".repeat(80) + "\n");

            int tokenCount = 0;
            while (true) {
                Token token = lexer.nextToken();

                if (token.getType() == Token.EOF) {
                    break;
                }

                tokenCount++;
                String tokenName = SQLLexer.VOCABULARY.getSymbolicName(token.getType());

                writer.write(String.format("%-5d | %-25s | %s\n",
                        tokenCount,
                        tokenName != null ? tokenName : "UNKNOWN",
                        token.getText()));
            }

            writer.write("-".repeat(80) + "\n");
            writer.write("\nTotal tokens found: " + tokenCount + "\n");

            System.out.println("Tokenization complete!");
            System.out.println("Total tokens found: " + tokenCount);
            System.out.println("Output written to: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}