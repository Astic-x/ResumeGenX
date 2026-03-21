package compiler.parser;

import compiler.lexer.*;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int position = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(position);
    }

    private void eat(Token.TokenType type) {
        if (currentToken.getType() == type) {
            position++;
            if (position < tokens.size()) {
                currentToken = tokens.get(position);
            }
        } else {
            throw new RuntimeException(
                "Error at line " + currentToken.getLine() +
                ": Expected " + type +
                " but found " + currentToken.getType()
            );
        }
    }

    // ================= ENTRY =================
    public void parseResume() {

        // Skip starting newlines
        while (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.EOF) {

    if (currentToken.getType() == Token.TokenType.KEYWORD_SECTION) {
        parseSection();
    } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
        parseKeyValue();   // handle Name, Email etc.
    } else if (currentToken.getType() == Token.TokenType.NEWLINE) {
        eat(Token.TokenType.NEWLINE);
    } else {
        throw new RuntimeException("Unexpected token at top level: " + currentToken);
    }
}

        System.out.println("Parsing Completed Successfully!");
    }

    // ================= SECTION =================
    private void parseSection() {

        eat(Token.TokenType.KEYWORD_SECTION);

// Handle optional ':' or '='
if (currentToken.getType() == Token.TokenType.ASSIGN_OP) {
    eat(Token.TokenType.ASSIGN_OP);
}

String sectionName = currentToken.getValue();
eat(Token.TokenType.STRING_VALUE);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
               currentToken.getType() != Token.TokenType.EOF) {

            parseContent();
        }

        System.out.println("Parsed Section: " + sectionName);
    }

    // ================= CONTENT =================
    private void parseContent() {

        switch (currentToken.getType()) {

            case IDENTIFIER:
                parseKeyValue();
                break;

            case KEYWORD_SUBSECTION:
                parseSubSection();
                break;

            case BULLET_ITEM:
                parseBullet();
                break;

            case NEWLINE:
                eat(Token.TokenType.NEWLINE);
                break;

            default:
                throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    // ================= KEY VALUE =================
    private void parseKeyValue() {

        String key = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);

       eat(Token.TokenType.ASSIGN_OP);

// OPTIONAL value (important fix)
String value = "";

if (currentToken.getType() == Token.TokenType.STRING_VALUE) {
    value = currentToken.getValue();
    eat(Token.TokenType.STRING_VALUE);
}

// Optional newline
if (currentToken.getType() == Token.TokenType.NEWLINE) {
    eat(Token.TokenType.NEWLINE);
}

System.out.println("KeyValue: " + key + " = " + value);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        System.out.println("KeyValue: " + key + " = " + value);
    }

    // ================= SUBSECTION =================
    private void parseSubSection() {

        eat(Token.TokenType.KEYWORD_SUBSECTION);

// Handle optional ':' or '='
if (currentToken.getType() == Token.TokenType.ASSIGN_OP) {
    eat(Token.TokenType.ASSIGN_OP);
}

String name = currentToken.getValue();
eat(Token.TokenType.STRING_VALUE);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SUBSECTION &&
               currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
               currentToken.getType() != Token.TokenType.EOF) {

            parseContent();
        }

        System.out.println("Parsed SubSection: " + name);
    }

    // ================= BULLET =================
    private void parseBullet() {

        String bullet = currentToken.getValue();
        eat(Token.TokenType.BULLET_ITEM);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        System.out.println("Bullet: " + bullet);
    }
}