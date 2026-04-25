package compiler.generator;

public class TemplateFactory {

    public static ResumeGenerator getGenerator(String templateName) {
        if (templateName == null)
            return new MinimalGenerator();

        switch (templateName.toLowerCase()) {
            case "clean minimal":
            case "compact grid":
                return new MinimalGenerator(); // Uses your existing Elastic Engine

            default:
                return new MinimalGenerator(); // Fallback
        }
    }
}