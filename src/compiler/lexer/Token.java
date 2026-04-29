package compiler.lexer;

public class Token {

    // Token types
    public enum TokenType {
        KEYWORD_SECTION,     // Section keyword
        KEYWORD_SUBSECTION,  // Subsection keyword
        IDENTIFIER,          // Identifier keys
        ASSIGN_OP,           // Assignment operator
        STRING_VALUE,        // String content
        BULLET_ITEM,         // Bullet item
        NEWLINE,             // Newline character
        EOF                  // End of file marker
    }

    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    // Format token string
    @Override
    public String toString() {
        if (type == TokenType.NEWLINE) {
            return String.format("Token[Type: %-18s, Value: \\n, Line: %d, Col: %d]", type, line, column);
        }
        return String.format("Token[Type: %-18s, Value: '%s', Line: %d, Col: %d]", type, value, line, column);
    }
}