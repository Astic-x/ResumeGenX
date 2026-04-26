import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;
import compiler.ast.Resume;
import compiler.ast.Section;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("  ResumeGenX - Combined Compiler Test  ");
        try {
            // 1. READ INPUT
            String fileName = args.length > 0 ? args[0] : "Sample.rdl";

            System.out.println("1. Reading input file '" + fileName + "'...");
            String fileContent = Files.readString(Path.of(fileName));
            System.out.println("      File read successfully. Length: "
                    + fileContent.length() + " characters.\n");

            // 2. LEXER
            System.out.println("2. Running Lexer...");
            Lexer lexer = new Lexer(fileContent);
            List<Token> tokens = lexer.tokenize();
            System.out.println("      Lexer finished successfully. Generated "
                    + tokens.size() + " tokens.\n");

            // 3. PARSER
            System.out.println("3. Running Parser...");
            Parser parser = new Parser(tokens);
            Resume myResume = parser.parseResume();
            System.out.println("      Parser finished successfully.\n");

            // DEBUG
            System.out.println("DEBUG: AST successfully created\n");

            // 4. SEMANTIC ANALYSIS
            System.out.println("4. Running Semantic Analyzer...");
            SemanticAnalyzer analyzer = new SemanticAnalyzer();

            analyzer.analyze(myResume);
            System.out.println("      Semantic analysis completed.\n");

            // 5. PRINT AST
            System.out.println("  Abstract Syntax Tree (AST) Summary ");
            System.out.println("Header Info:");
            for (String key : myResume.headerInfo.keySet()) {
                System.out.println("  - " + key + ": " + myResume.headerInfo.get(key));
            }

            System.out.println("\nSections (" + myResume.sections.size() + " total):");
            for (Section s : myResume.sections) {
                System.out.println(" -> Section: [" + s.title + "] with "
                        + s.subSections.size() + " subsections");
            }

            System.out.println("\n  Test Completed Without Errors  ");
            System.out.println("  Execution Completed ");
            // 6. LATEX GENERATION
            System.out.println("5. Generating LaTeX...");

            String latexCode = LatexGenerator.generate(myResume);

            // Print preview
            System.out.println("\nGENERATED LATEX \n");
            // System.out.println(latexCode);

            // Save to file
            
            // Define the workspace directory
            Path workspaceDir = Path.of("workspace");

            // Safely create the directory if it doesn't exist yet
            if (!Files.exists(workspaceDir)) {
                Files.createDirectories(workspaceDir);
                System.out.println("      Created new 'workspace' directory.");
            }

            // Resolve the output path inside the workspace folder
            Path outputPath = workspaceDir.resolve("output.tex");
            Files.writeString(outputPath, latexCode);

            System.out.println("\nLaTeX file generated successfully: " + outputPath.toString());

        } catch (IOException e) {
            System.err.println("\n[ERROR] File not found or cannot be read!");
            System.err.println("Make sure 'Sample.rdl' exists in project root.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n[COMPILATION ERROR]");
            System.err.println(e.getMessage());
        }
    }
}