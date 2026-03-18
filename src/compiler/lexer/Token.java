package compiler.lexer;
public class Token {

    // The distinct types of tokens defined by our BNF grammar
    public enum TokenType {
        KEYWORD_SECTION,     // "Section"
        KEYWORD_SUBSECTION,  // "SubSection"
        IDENTIFIER,          // Keys like "Name", "Degree", "Location"
        ASSIGN_OP,           // '=' or ':'
        STRING_VALUE,        // The actual text content
        BULLET_ITEM,         // A list item that started with '-'
        NEWLINE,             // '\n'
        EOF                  // End of File
    }

    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    // Constructor
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    // Getters
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

    // For easy debugging when we print the tokens to the console
    @Override
    public String toString() {
        // Formatting the output so it aligns nicely in the terminal
        if (type == TokenType.NEWLINE) {
            return String.format("Token[Type: %-18s, Value: \\n, Line: %d, Col: %d]", type, line, column);
        }
        return String.format("Token[Type: %-18s, Value: '%s', Line: %d, Col: %d]", type, value, line, column);
    }
}