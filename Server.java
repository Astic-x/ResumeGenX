import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.Base64;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.List;

import compiler.lexer.Lexer;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;
import compiler.generator.MinimalGenerator;
import compiler.generator.TemplateFactory;
import compiler.ast.Resume;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 1. Serve the frontend files
        server.createContext("/", new StaticFileHandler());

        // 2. The Compiler API Endpoint
        server.createContext("/api/generate", new GenerateHandler());

        server.setExecutor(null); // Use default executor
        server.start();
        System.out.println("==========================================");
        System.out.println("  ResumeGenX Web Server is LIVE!");
        System.out.println("  Open your browser to: http://localhost:8080");
        System.out.println("==========================================");
    }

    // Handles requests to http://localhost:8080/api/generate
    static class GenerateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 1. Global CORS Headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            // 🔥 Added X-Output-Format to allowed headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers",
                    "Content-Type, X-Template-Name, X-Output-Format");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody();
                    String rdlContent = new String(is.readAllBytes());
                    String templateName = exchange.getRequestHeaders().getFirst("X-Template-Name");
                    String outputFormat = exchange.getRequestHeaders().getFirst("X-Output-Format"); // 'latex' or 'pdf'

                    Lexer lexer = new Lexer(rdlContent);
                    Parser parser = new Parser(lexer.tokenize());
                    Resume resume = parser.parseResume();

                    SemanticAnalyzer analyzer = new SemanticAnalyzer();
                    List<String> warnings = analyzer.analyze(resume);

                    String latexCode = TemplateFactory.getGenerator(templateName).generate(resume);
                    String pdfBase64 = null;

                    // 🔥 NEW: Compile to PDF if requested
                    if ("pdf".equalsIgnoreCase(outputFormat)) {
                        pdfBase64 = compilePdf(latexCode);
                    }

                    String escapedLatex = latexCode
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
                            .replace("\r", "");

                    StringBuilder json = new StringBuilder();
                    json.append("{\n");
                    json.append("  \"latex\": \"").append(escapedLatex).append("\",\n");

                    if (pdfBase64 != null) {
                        json.append("  \"pdfBase64\": \"").append(pdfBase64).append("\",\n");
                    }

                    json.append("  \"warnings\": [\n");
                    for (int i = 0; i < warnings.size(); i++) {
                        String escapedWarning = warnings.get(i).replace("\"", "\\\"");
                        json.append("    \"").append(escapedWarning).append("\"");
                        if (i < warnings.size() - 1)
                            json.append(",");
                        json.append("\n");
                    }
                    json.append("  ]\n");
                    json.append("}");

                    byte[] responseBytes = json.toString().getBytes("UTF-8");
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseBytes);
                    os.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    String errorMsg = "FATAL EXCEPTION: " + (e.getMessage() != null ? e.getMessage() : e.toString());
                    exchange.sendResponseHeaders(500, errorMsg.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(errorMsg.getBytes());
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }

        // 🔥 NEW: The PDF Compilation Engine
        private String compilePdf(String latex) throws Exception {
            File tempDir = Files.createTempDirectory("resumegenx_").toFile();
            File texFile = new File(tempDir, "resume.tex");
            File pdfFile = new File(tempDir, "resume.pdf");

            try {
                Files.writeString(texFile.toPath(), latex);

                ProcessBuilder pb = new ProcessBuilder(
                        "pdflatex",
                        "-interaction=nonstopmode", // Don't hang waiting for user input
                        "-halt-on-error", // Stop immediately if LaTeX breaks
                        "resume.tex");
                pb.directory(tempDir);

                // 🔥 THE FIX: Prevent Java Deadlock by redirecting the logs
                pb.redirectErrorStream(true);
                pb.inheritIO(); // Prints the LaTeX output directly to your Java terminal

                Process process = pb.start();
                int exitCode = process.waitFor();

                if (exitCode != 0 || !pdfFile.exists()) {
                    throw new RuntimeException(
                            "pdflatex compilation failed. Check the Java terminal for the exact LaTeX error.");
                }

                byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
                return java.util.Base64.getEncoder().encodeToString(pdfBytes);

            } finally {
                // Always clean up temp files to prevent disk leak
                for (File f : tempDir.listFiles()) {
                    f.delete();
                }
                tempDir.delete();
            }
        }
    }

    // Simple handler to serve index.html, templates.html, and style.css
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/"))
                path = "/index.html";

            File file = new File("frontend" + path);
            if (!file.exists()) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (path.endsWith(".css"))
                exchange.getResponseHeaders().add("Content-Type", "text/css");
            if (path.endsWith(".html"))
                exchange.getResponseHeaders().add("Content-Type", "text/html");

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}