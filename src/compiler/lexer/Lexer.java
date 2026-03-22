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
        // Sanitize Windows line endings (\r\n) to standard Unix newlines (\n)
        this.input = input.replace("\r\n", "\n").replace("\r", "\n");
    }

    // --- Core Lexical Loop ---
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char currentChar = peek();

            // 1. Skip standard whitespace (but NOT newlines)
            if (currentChar == ' ' || currentChar == '\t' || currentChar == '\r') {
                advance();
                continue;
            }

            // 2. Handle Newlines
            if (currentChar == '\n') {
                tokens.add(new Token(Token.TokenType.NEWLINE, "\\n", line, column));
                advance();
                line++;
                column = 1; // Reset column on new line
                continue;
            }

            // 3. Skip Comments (// to end of line)
            if (currentChar == '/' && peekAhead(1) == '/') {
                while (position < input.length() && peek() != '\n') {
                    advance();
                }
                continue;
            }

            // 4. Handle Bullet Points (Hyphen at start of text)
            if (currentChar == '-') {
                tokens.add(readBulletItem());
                continue;
            }

            // 5. Handle Assignments (= or :) and switch to Value Reading Mode
            if (currentChar == '=' || currentChar == ':') {
                tokens.add(new Token(Token.TokenType.ASSIGN_OP, String.valueOf(currentChar), line, column));
                advance();

                // Immediately switch state to absorb the string value
                Token valueToken = readValue();
                if (valueToken != null) {
                    tokens.add(valueToken);
                }
                continue;
            }

            // 6. Handle Keywords and Identifiers
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifierOrKeyword());
                continue;
            }

            // If we hit an unrecognized character, skip it
            advance();
        }

        // Always end the stream with an EOF token
        tokens.add(new Token(Token.TokenType.EOF, "", line, column));
        return tokens;
    }

    // --- State: Value Reading Mode ---
    private Token readValue() {
        // Skip spaces immediately after the = or :
        while (peek() == ' ' || peek() == '\t')
            advance();

        // If the user just pressed Enter after the colon (e.g., for a bullet list),
        // return nothing
        if (peek() == '\n' || position >= input.length()) {
            return null;
        }

        int startLine = line;
        int startCol = column;
        StringBuilder sb = new StringBuilder();

        while (position < input.length()) {
            char c = peek();

            // THE SMART LOOKAHEAD RULE
            if (c == '\n') {
                int lookPos = position;
                lookPos++; // Skip the \n itself

                // Count the indentation on the next line
                while (lookPos < input.length() && (input.charAt(lookPos) == ' ' || input.charAt(lookPos) == '\t')) {
                    lookPos++;
                }

                if (lookPos < input.length()) {
                    char nextChar = input.charAt(lookPos);

                    // 1. Is it a bullet point?
                    if (nextChar == '-') {
                        break; // Stop reading the string
                    }

                    // 1.5 Is it a comment?
                    else if (nextChar == '/' && lookPos + 1 < input.length() && input.charAt(lookPos + 1) == '/') {
                        break; // Stop reading the string, let the main loop handle the comment
                    }

                    // 2. Is it a new Key-Value pair? (e.g., SubSection = Microsoft)
                    boolean isNewKey = false;
                    int tempPos = lookPos;

                    // Read the first word on the new line
                    while (tempPos < input.length() && Character.isLetterOrDigit(input.charAt(tempPos))) {
                        tempPos++;
                    }
                    // Skip spaces after that word
                    while (tempPos < input.length()
                            && (input.charAt(tempPos) == ' ' || input.charAt(tempPos) == '\t')) {
                        tempPos++;
                    }
                    // Is the very next thing an assignment operator?
                    if (tempPos < input.length() && (input.charAt(tempPos) == '=' || input.charAt(tempPos) == ':')) {
                        isNewKey = true;
                    }

                    if (isNewKey) {
                        break; // It's a new key! Stop reading the string value.
                    } else if (lookPos > position + 1) {
                        // It has indentation, it's not a bullet, and it's NOT a new key.
                        // It MUST be a multi-line string continuation!
                        sb.append(" ");
                        position = lookPos; // Fast-forward lexer position
                        line++;
                        column = 1 + (lookPos - (position + 1));
                        continue;
                    }
                }

                // If it has no indentation, the string is done.
                break;
            }

            sb.append(c);
            advance();
        }

        return new Token(Token.TokenType.STRING_VALUE, sb.toString().trim(), startLine, startCol);
    }

    // --- Helpers for specific token types ---
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
        advance(); // Consume the '-'

        while (peek() == ' ' || peek() == '\t')
            advance(); // skip spaces after hyphen

        StringBuilder sb = new StringBuilder();
        while (position < input.length() && peek() != '\n') {
            sb.append(advance());
        }

        return new Token(Token.TokenType.BULLET_ITEM, sb.toString().trim(), line, startCol);
    }

    // --- Basic Pointer Movement ---
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

//    // =========================================================================
//    // Independent Lexer Verification Test
//    // =========================================================================
//    public static void main(String[] args) {
//        System.out.println("--- Booting Independent Lexer Test ---");
//
//        try {
//            String fileContent = Files.readString(Path.of("Sample.rdl"));
//            System.out.println("--- READING FILE SUCCESSFUL ---");
//
//            Lexer lexer = new Lexer(fileContent);
//            List<Token> tokens = lexer.tokenize();
//
//            System.out.println("--- TOKENS GENERATED ---");
//            for (Token t : tokens) {
//                System.out.println(t.getType() + " : " + t.getValue().replace("\n", "\\n"));
//            }
//
//            System.out.println("--- Lexer Test Complete ---");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error reading the file: " + e.getMessage());
//        }
//    }
}