package compiler.parser;

import compiler.lexer.*;
import compiler.ast.*;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int position = 0;
    private Token currentToken;

    // AST state
    private Resume resume;
    private Section currentSection;
    private SubSection currentSubSection;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(position);
        this.resume = new Resume();
    }

    private void eat(Token.TokenType type) {
        if (currentToken.getType() == type) {
            position++;
            if (position < tokens.size()) {
                currentToken = tokens.get(position);
            }
        } else {
            throw new RuntimeException("Error at line " + currentToken.getLine() +
                    ": Expected " + type + " but found " + currentToken.getType());
        }
    }

    // Parse Resume
    public Resume parseResume() {
        while (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.EOF) {
            if (currentToken.getType() == Token.TokenType.KEYWORD_SECTION) {
                parseSection();
            } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                // Add top-level keys to header
                String[] kv = parseKeyValue();
                resume.headerInfo.put(kv[0], kv[1]);
            } else if (currentToken.getType() == Token.TokenType.NEWLINE) {
                eat(Token.TokenType.NEWLINE);
            } else {
                throw new RuntimeException("Unexpected token at top level: " + currentToken);
            }
        }
        return resume;
    }

    // Parse Section
    private void parseSection() {
        eat(Token.TokenType.KEYWORD_SECTION);
        eat(Token.TokenType.ASSIGN_OP); // Enforce Section assignment

        String sectionName = currentToken.getValue();
        eat(Token.TokenType.STRING_VALUE);

        // Create active Section
        currentSection = new Section(sectionName);
        currentSubSection = null;

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
                currentToken.getType() != Token.TokenType.EOF) {
            parseContent();
        }

        // Attach Section
        resume.sections.add(currentSection);
    }

    // Parse Content
    private void parseContent() {
        switch (currentToken.getType()) {
            case IDENTIFIER:
                String[] kv = parseKeyValue();
                if (currentSubSection != null) {
                    currentSubSection.keyValues.put(kv[0], kv[1]);
                }
                break;

            case KEYWORD_SUBSECTION:
                parseSubSection();
                break;

            case NEWLINE:
                eat(Token.TokenType.NEWLINE);
                break;

            default:
                throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    // Parse Key-Value and bullets
    private String[] parseKeyValue() {
        String key = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);
        eat(Token.TokenType.ASSIGN_OP); // Mandatory assignment

        String value = "";

        // Handle string value
        if (currentToken.getType() == Token.TokenType.STRING_VALUE) {
            value = currentToken.getValue();
            eat(Token.TokenType.STRING_VALUE);

            if (currentToken.getType() == Token.TokenType.NEWLINE) {
                eat(Token.TokenType.NEWLINE);
            }
        }
        // Handle bullet list or newline
        else if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);

            // Parse bullets
            while (currentToken.getType() == Token.TokenType.BULLET_ITEM) {
                String bulletText = parseBullet();
                if (currentSubSection != null) {
                    // Prepend key to bullet
                    currentSubSection.bullets.add(key + ": " + bulletText);
                }
            }
        }

        return new String[] { key, value };
    }

    // Parse Bullet
    private String parseBullet() {
        String bullet = currentToken.getValue();
        eat(Token.TokenType.BULLET_ITEM);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        return bullet;
    }

    // Parse SubSection
    private void parseSubSection() {
        eat(Token.TokenType.KEYWORD_SUBSECTION);
        eat(Token.TokenType.ASSIGN_OP); // Enforce SubSection assignment

        String name = currentToken.getValue();
        eat(Token.TokenType.STRING_VALUE);

        // Create SubSection
        currentSubSection = new SubSection(name);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SUBSECTION &&
                currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
                currentToken.getType() != Token.TokenType.EOF) {
            parseContent();
        }

        // Attach SubSection
        currentSection.subSections.add(currentSubSection);
    }

    // // Independent parser test
    // public static void main(String[] args) {
    // System.out.println("Starting parser test");
    // try {
    // String fileContent =
    // java.nio.file.Files.readString(java.nio.file.Path.of("Sample.rdl"));
    // Lexer lexer = new Lexer(fileContent);
    // java.util.List<Token> tokens = lexer.tokenize();
    //
    // System.out.println("Lexer tokens ready");
    //
    // Parser parser = new Parser(tokens);
    // Resume myResume = parser.parseResume();
    //
    // System.out.println("AST built successfully");
    // System.out.println("Candidate Name: " + myResume.headerInfo.get("Name"));
    // System.out.println("Total Sections: " + myResume.sections.size());
    //
    // for (Section s : myResume.sections) {
    // System.out.println(" -> Section: " + s.title + " (" + s.subSections.size() +
    // " subsections)");
    // }
    // System.out.println("Parser test complete");
    // } catch (java.io.IOException e) {
    // e.printStackTrace();
    // System.err.println("Error reading the file: " + e.getMessage());
    // }
    // }
}