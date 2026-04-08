package compiler.lexer;

import compiler.parser.Parser;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Lexer {
    private final String input;
    private int position = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String input) {
        // Normalize line endings to standard Unix format
        this.input = input.replace("\r\n", "\n").replace("\r", "\n");
    }

    // Main lexical loop
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < input.length()) {
            char currentChar = peek();

            // Skip spaces
            if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r') {
                advance();
                continue;
            }

            // Handle newlines
            if (currentChar == '\n') {
                tokens.add(new Token(Token.TokenType.NEWLINE, "\\n", line, column));
                advance();
                line++;
                column = 1;
                continue;
            }

            // Skip inline comments
            if (currentChar == '/' && peekAhead(1) == '/') {
                while (position < input.length() && peek() != '\n') {
                    advance();
                }
                continue;
            }

            // Handle bullet points
            if (currentChar == '-') {
                tokens.add(readBulletItem());
                continue;
            }

            // Handle assignments and switch to value reading
            if (currentChar == '=' || currentChar == ':') {
                tokens.add(new Token(Token.TokenType.ASSIGN_OP, String.valueOf(currentChar), line, column));
                advance();
                Token valueToken = readValue();
                if (valueToken != null) {
                    tokens.add(valueToken);
                }
                continue;
            }

            // Handle keywords and identifiers
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifierOrKeyword());
                continue;
            }

            // Skip unrecognized characters
            advance();
        }
        // End stream with EOF token
        tokens.add(new Token(Token.TokenType.EOF, "", line, column));
        return tokens;
    }

    // Value reading mode
    private Token readValue() {
        // Skip spaces after assignment
        while (peek() == ' ' || peek() == '\t')
            advance();

        // Return nothing if Enter is pressed after colon
        if (peek() == '\n' || position >= input.length()) {
            return null;
        }

        int startLine = line;
        int startCol = column;
        StringBuilder sb = new StringBuilder();

        while (position < input.length()) {
            char c = peek();

            // Smart lookahead rule
            if (c == '\n') {
                int lookPos = position;
                lookPos++; // Skip newline

                // Count next line indentation
                while (lookPos < input.length() && (input.charAt(lookPos) == ' ' || input.charAt(lookPos) == '\t')) {
                    lookPos++;
                }

                if (lookPos < input.length()) {
                    char nextChar = input.charAt(lookPos);

                    // Check for bullet point
                    if (nextChar == '-') {
                        break;
                    }

                    // Check for comment
                    else if (nextChar == '/' && lookPos + 1 < input.length() && input.charAt(lookPos + 1) == '/') {
                        break;
                    }

                    // Check for new key-value pair
                    boolean isNewKey = false;
                    int tempPos = lookPos;

                    // Read first word on new line
                    while (tempPos < input.length() && Character.isLetterOrDigit(input.charAt(tempPos))) {
                        tempPos++;
                    }
                    // Skip spaces after word
                    while (tempPos < input.length()
                            && (input.charAt(tempPos) == ' ' || input.charAt(tempPos) == '\t')) {
                        tempPos++;
                    }
                    // Check if next character is an assignment operator
                    if (tempPos < input.length() && (input.charAt(tempPos) == '=' || input.charAt(tempPos) == ':')) {
                        isNewKey = true;
                    }

                    if (isNewKey) {
                        break;
                    } else if (lookPos > position + 1) {
                        // Multi-line string continuation
                        sb.append(" ");
                        position = lookPos;
                        line++;
                        column = 1 + (lookPos - (position + 1));
                        continue;
                    }
                }

                // String is done if no indentation
                break;
            }

            sb.append(c);
            advance();
        }

        return new Token(Token.TokenType.STRING_VALUE, sb.toString().trim(), startLine, startCol);
    }

    // Helpers for specific tokens
    private Token readIdentifierOrKeyword() {
        int startCol = column;
        StringBuilder sb = new StringBuilder();

        while (position < input.length() && Character.isLetterOrDigit(peek())) {
            sb.append(advance());
        }

        String word = sb.toString();
        if (word.equals("Section"))
            return new Token(Token.TokenType.KEYWORD_SECTION, word, line, startCol);
        if (word.equals("SubSection"))
            return new Token(Token.TokenType.KEYWORD_SUBSECTION, word, line, startCol);

        return new Token(Token.TokenType.IDENTIFIER, word, line, startCol);
    }

    private Token readBulletItem() {
        int startCol = column;
        advance(); // Consume hyphen

        while (peek() == ' ' || peek() == '\t')
            advance(); // Skip spaces

        StringBuilder sb = new StringBuilder();
        while (position < input.length() && peek() != '\n') {
            sb.append(advance());
        }

        return new Token(Token.TokenType.BULLET_ITEM, sb.toString().trim(), line, startCol);
    }

    // Pointer movement
    private char peek() {
        if (position >= input.length())
            return '\0';
        return input.charAt(position);
    }

    private char peekAhead(int offset) {
        if (position + offset >= input.length())
            return '\0';
        return input.charAt(position + offset);
    }

    private char advance() {
        char c = input.charAt(position);
        position++;
        column++;
        return c;
    }

//    // Independent lexer test
//    public static void main(String[] args) {
//        System.out.println("Starting lexer test");
//        try {
//            String fileContent = Files.readString(Path.of("Sample.rdl"));
//            System.out.println("File read successfully");
//            Lexer lexer = new Lexer(fileContent);
//            List<Token> tokens = lexer.tokenize();
//            System.out.println("Tokens generated:");
//            for (Token t : tokens) {
//                System.out.println(t.getType() + " : " + t.getValue().replace("\n", "\\n"));
//            }
//            System.out.println("Lexer test complete");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error reading the file: " + e.getMessage());
//        }
//    }
}