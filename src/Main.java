import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.ast.Resume;
import compiler.ast.Section;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  ResumeGenX - Combined Compiler Test  ");
        System.out.println("==========================================\n");

        try {
            // 1. Read input file
            System.out.println("[1/3] Reading input file 'Sample.rdl'...");
            String fileContent = Files.readString(Path.of("Sample.rdl"));
            System.out.println("      File read successfully. Length: " + fileContent.length() + " characters.\n");

            // 2. Lexical Analysis
            System.out.println("[2/3] Running Lexer...");
            Lexer lexer = new Lexer(fileContent);
            List<Token> tokens = lexer.tokenize();
            System.out.println("      Lexer finished successfully. Generated " + tokens.size() + " tokens.\n");
            
            // Uncomment the following lines to see all tokens:
            // for (Token token : tokens) {
            //     System.out.println("      " + token.getType() + " : '" + token.getValue().replace("\n", "\\n") + "'");
            // }

            // 3. Parsing
            System.out.println("[3/3] Running Parser...");
            Parser parser = new Parser(tokens);
            Resume myResume = parser.parseResume();
            
            System.out.println("      Parser finished successfully.\n");

            // 4. Output the AST details
            System.out.println("==========================================");
            System.out.println("  Abstract Syntax Tree (AST) Summary ");
            System.out.println("==========================================");
            
            System.out.println("Header Info:");
            for (String key : myResume.headerInfo.keySet()) {
                System.out.println("  - " + key + ": " + myResume.headerInfo.get(key));
            }
            
            System.out.println("\nSections (" + myResume.sections.size() + " total):");
            for (Section s : myResume.sections) {
                System.out.println(" -> Section: [" + s.title + "] with " + s.subSections.size() + " subsections");
            }
            
            System.out.println("\n==========================================");
            System.out.println("  Test Completed Without Errors  ");
            System.out.println("==========================================");

        } catch (IOException e) {
            System.err.println("\n[ERROR] I/O Exception occurred:");
            System.err.println("Make sure 'Sample.rdl' exists in the root directory!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n[ERROR] Compilation failed:");
            e.printStackTrace();
        }
    }
}
