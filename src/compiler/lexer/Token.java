package compiler.lexer;

public class Token {

    // Token types for grammar
    public enum TokenType {
        KEYWORD_SECTION,     // Section
        KEYWORD_SUBSECTION,  // SubSection
        IDENTIFIER,          // Keys
        ASSIGN_OP,           // = or :
        STRING_VALUE,        // Text content
        BULLET_ITEM,         // List item
        NEWLINE,             // Newline \n
        EOF                  // End of file
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

    // Formatting token output
    @Override
    public String toString() {
        if (type == TokenType.NEWLINE) {
            return String.format("Token[Type: %-18s, Value: \\n, Line: %d, Col: %d]", type, line, column);
        }
        return String.format("Token[Type: %-18s, Value: '%s', Line: %d, Col: %d]", type, value, line, column);
    }
}