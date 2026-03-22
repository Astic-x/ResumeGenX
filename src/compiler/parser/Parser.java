package compiler.parser;

import compiler.lexer.*;
import compiler.ast.*; // Import the new AST classes!
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private int position = 0;
    private Token currentToken;

    // --- AST State Tracking ---
    private Resume resume;
    private Section currentSection;
    private SubSection currentSubSection;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(position);
        this.resume = new Resume(); // Initialize the root of our tree
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

    // ================= ENTRY =================
    // Now returns the completed Resume object!
    public Resume parseResume() {
        while (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.EOF) {
            if (currentToken.getType() == Token.TokenType.KEYWORD_SECTION) {
                parseSection();
            } else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                // Top-level KeyValues (Name, Email, etc.) go into the Resume header
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

    // ================= SECTION =================
    private void parseSection() {
        eat(Token.TokenType.KEYWORD_SECTION);

        if (currentToken.getType() == Token.TokenType.ASSIGN_OP) {
            eat(Token.TokenType.ASSIGN_OP);
        }

        String sectionName = currentToken.getValue();
        eat(Token.TokenType.STRING_VALUE);

        // Create a new Section node and set it as the active section
        currentSection = new Section(sectionName);
        currentSubSection = null; // Reset the active subsection

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
                currentToken.getType() != Token.TokenType.EOF) {
            parseContent();
        }

        // Once the section is done, attach it to the Resume root
        resume.sections.add(currentSection);
    }

    // ================= CONTENT =================
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

            case BULLET_ITEM:
                String bullet = parseBullet();
                if (currentSubSection != null) {
                    currentSubSection.bullets.add(bullet);
                }
                break;

            case NEWLINE:
                eat(Token.TokenType.NEWLINE);
                break;

            default:
                throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

    // ================= KEY VALUE =================
    // Now returns a String array [Key, Value] instead of void
    private String[] parseKeyValue() {
        String key = currentToken.getValue();
        eat(Token.TokenType.IDENTIFIER);

        eat(Token.TokenType.ASSIGN_OP);

        String value = "";
        if (currentToken.getType() == Token.TokenType.STRING_VALUE) {
            value = currentToken.getValue();
            eat(Token.TokenType.STRING_VALUE);
        }

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        return new String[] { key, value };
    }

    // ================= SUBSECTION =================
    private void parseSubSection() {
        eat(Token.TokenType.KEYWORD_SUBSECTION);

        if (currentToken.getType() == Token.TokenType.ASSIGN_OP) {
            eat(Token.TokenType.ASSIGN_OP);
        }

        String name = currentToken.getValue();
        eat(Token.TokenType.STRING_VALUE);

        // Create a new SubSection node
        currentSubSection = new SubSection(name);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        while (currentToken.getType() != Token.TokenType.KEYWORD_SUBSECTION &&
                currentToken.getType() != Token.TokenType.KEYWORD_SECTION &&
                currentToken.getType() != Token.TokenType.EOF) {
            parseContent();
        }

        // Attach this finished SubSection to the active Section
        currentSection.subSections.add(currentSubSection);
    }

    // ================= BULLET =================
    // Now returns the bullet text string
    private String parseBullet() {
        String bullet = currentToken.getValue();
        eat(Token.TokenType.BULLET_ITEM);

        if (currentToken.getType() == Token.TokenType.NEWLINE) {
            eat(Token.TokenType.NEWLINE);
        }

        return bullet;
    }

    // =========================================================================
    // Independent Parser Verification Test
    // =========================================================================
//    public static void main(String[] args) {
//        System.out.println("--- Booting Independent Parser Test ---");
//
//        try {
//            String fileContent = java.nio.file.Files.readString(java.nio.file.Path.of("Sample.rdl"));
//            Lexer lexer = new Lexer(fileContent);
//            java.util.List<Token> tokens = lexer.tokenize();
//            
//            System.out.println("--- LEXER TOKENS READY ---");
//            
//            Parser parser = new Parser(tokens);
//            Resume myResume = parser.parseResume();
//
//            System.out.println("--- AST BUILT SUCCESSFULLY ---");
//            System.out.println("Candidate Name: " + myResume.headerInfo.get("Name"));
//            System.out.println("Total Sections Found: " + myResume.sections.size());
//
//            for (Section s : myResume.sections) {
//                System.out.println(" -> Section: " + s.title + " (" + s.subSections.size() + " subsections)");
//            }
//
//            System.out.println("--- Parser Test Complete ---");
//
//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//            System.err.println("Error reading the file: " + e.getMessage());
//        }
//    }
}