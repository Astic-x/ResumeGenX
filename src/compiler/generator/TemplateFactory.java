package compiler.generator;

public class TemplateFactory {

    public static ResumeGenerator getGenerator(String templateName) {
        if (templateName == null)
            return new MinimalGenerator();

        switch (templateName.toLowerCase()) {
            case "clean minimal":
            case "compact grid":
                return new MinimalGenerator(); // Uses your existing Elastic Engine

            case "modern split":
                return new ModernSplitGenerator();
            case "academic pro":
                return new MonogramGenerator();
            case "creative bold":
                return new CreativeBoldGenerator();
            case "centered elegant":
                return new CenteredElegantGenerator();
            case "tech focused":
                return new AltaCVGenerator();
            default:
                return new MinimalGenerator(); // Fallback
        }
    }
}