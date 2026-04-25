import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

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
            // Allow cross-origin requests
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // 1. Read the .rdl file content from the HTTP request
                    InputStream is = exchange.getRequestBody();
                    String rdlContent = new String(is.readAllBytes());

                    // 🔥 NEW: Grab the template name from the headers
                    String templateName = exchange.getRequestHeaders().getFirst("X-Template-Name");

                    // 2. Run the compiler pipeline!
                    Lexer lexer = new Lexer(rdlContent);
                    Parser parser = new Parser(lexer.tokenize());
                    Resume resume = parser.parseResume();

                    SemanticAnalyzer analyzer = new SemanticAnalyzer();
                    analyzer.analyze(resume);

                    // 🔥 NEW: Route through the Factory!
                    String latexCode = TemplateFactory.getGenerator(templateName).generate(resume);

                    // 3. Send the compiled LaTeX back to the browser
                    byte[] response = latexCode.getBytes();
                    exchange.getResponseHeaders().add("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();

                } catch (Exception e) {
                    // Send compilation errors back to the UI
                    String errorMsg = "COMPILER ERROR:\n" + e.getMessage();
                    exchange.sendResponseHeaders(500, errorMsg.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(errorMsg.getBytes());
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
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