package com.example;

import org.antlr.v4.runtime.*;

public class TestLexer {
    public static void main(String[] args) throws Exception {
        String sqlInput = "SELECT id, name FROM Users WHERE age >= 18 AND country = 'KSA'; -- Check users";

        System.out.println("Testing SQL Input: " + sqlInput);
        System.out.println("--------------------------------------------------");

        CharStream stream = CharStreams.fromString(sqlInput);

        SQLLexer lexer = new SQLLexer(stream);

        while (true) {
            Token token = lexer.nextToken();

            if (token.getType() == Token.EOF) {
                break;
            }

            String tokenName = SQLLexer.VOCABULARY.getSymbolicName(token.getType());
            System.out.printf("Token Type: %-20s | Text: %s\n", tokenName, token.getText());
        }
    }
}