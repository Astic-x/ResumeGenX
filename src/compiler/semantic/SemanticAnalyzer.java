package compiler.semantic;

import compiler.ast.*;

import java.util.*;

public class SemanticAnalyzer {

    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void analyze(Resume resume) {
        checkHeader(resume);
        checkSections(resume);
        checkMandatorySections(resume);

        printReport();

        // Stop execution if errors exist
        if (!errors.isEmpty()) {
            throw new RuntimeException("Semantic analysis failed due to errors.");
        }
    }

    // ================= HEADER CHECK =================
    private void checkHeader(Resume resume) {
        Map<String, String> header = resume.headerInfo;

        // Required fields
        if (!header.containsKey("Name") || header.get("Name").isEmpty()) {
            errors.add("Missing required field: Name");
        }

        if (!header.containsKey("Email") || header.get("Email").isEmpty()) {
            errors.add("Missing required field: Email");
        }

        // 🔥 Email format validation
        if (header.containsKey("Email")) {
            String email = header.get("Email");
            if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errors.add("Invalid Email format: " + email);
            }
        }

        // Check for empty values
        for (String key : header.keySet()) {
            if (header.get(key).trim().isEmpty()) {
                warnings.add("Header field '" + key + "' has empty value");
            }
        }

        // Duplicate detection (basic idea)
        Set<String> seen = new HashSet<>();
        for (String key : header.keySet()) {
            if (!seen.add(key)) {
                warnings.add("Duplicate header field: " + key);
            }
        }
    }

    // ================= SECTION CHECK =================
    private void checkSections(Resume resume) {
        if (resume.sections.isEmpty()) {
            errors.add("Resume must contain at least one Section");
            return;
        }

        // 🔥 Duplicate section detection
        Set<String> sectionNames = new HashSet<>();

        for (Section section : resume.sections) {

            if (!sectionNames.add(section.title)) {
                warnings.add("Duplicate Section: " + section.title);
            }

            // 🔥 Validate section name
            validateSectionName(section.title);

            checkSection(section);
        }
    }

    private void validateSectionName(String title) {
        Set<String> validSections = Set.of("Education", "Experience", "Projects");

        if (!validSections.contains(title)) {
            warnings.add("Unknown Section: " + title);
        }
    }

    private void checkSection(Section section) {
        if (section.subSections.isEmpty()) {
            errors.add("Section '" + section.title + "' has no SubSections");
        }

        for (SubSection sub : section.subSections) {
            checkSubSection(sub, section.title);
        }
    }

    // ================= SUBSECTION CHECK =================
    private void checkSubSection(SubSection sub, String sectionName) {

        boolean hasKeyValues = !sub.keyValues.isEmpty();
        boolean hasBullets = !sub.bullets.isEmpty();

        if (!hasKeyValues && !hasBullets) {
            errors.add("SubSection '" + sub.title + "' in Section '"
                    + sectionName + "' has no content");
        }

        // 🔥 Allowed keys per section
        Map<String, Set<String>> allowedKeys = getAllowedKeys();

        for (Map.Entry<String, String> entry : sub.keyValues.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            // 🔥 SPECIAL CASE: Highlights
            if (key.equalsIgnoreCase("Highlights")) {

                if (sub.bullets.isEmpty()) {
                    errors.add("SubSection '" + sub.title + "' has 'Highlights' but no bullet points");
                }

                continue;
            }

            // 🔥 Key validation per section
            if (allowedKeys.containsKey(sectionName)) {
                if (!allowedKeys.get(sectionName).contains(key)) {
                    warnings.add("Invalid key '" + key + "' in Section '" + sectionName + "'");
                }
            }

            // 🔥 Empty value check
            if (value.trim().isEmpty()) {
                warnings.add("Empty value for key '" + key
                        + "' in SubSection '" + sub.title + "'");
            }

            // 🔥 Description quality check
            if (key.equalsIgnoreCase("Description") && value.length() < 20) {
                warnings.add("Description too short in '" + sub.title + "'");
            }

            // 🔥 Date validation
            if (key.equalsIgnoreCase("StartDate") || key.equalsIgnoreCase("Timeline")) {
                if (!value.matches(".*\\d{4}.*")) {
                    warnings.add("Invalid date format in SubSection '" + sub.title + "'");
                }
            }
        }

        // 🔥 Bullet quality check
        for (String bullet : sub.bullets) {
            if (bullet.trim().length() < 10) {
                warnings.add("Weak bullet point in '" + sub.title + "'");
            }
        }

        // Warning if only title exists (weak content)
        if (hasKeyValues && sub.keyValues.size() == 1 && !hasBullets) {
            warnings.add("SubSection '" + sub.title + "' has very little content");
        }
    }

    // ================= ALLOWED KEYS =================
    private Map<String, Set<String>> getAllowedKeys() {

        Map<String, Set<String>> allowedKeys = new HashMap<>();

        allowedKeys.put("Education",
                Set.of("Degree", "Graduation", "ExpectedGraduation", "Coursework"));

        allowedKeys.put("Experience",
                Set.of("Role", "StartDate", "Description"));

        allowedKeys.put("Projects",
                Set.of("Role", "TechStack", "Description", "Timeline", "Highlights"));

        return allowedKeys;
    }

    // ================= MANDATORY SECTIONS =================
    private void checkMandatorySections(Resume resume) {

        boolean hasProjects = resume.sections.stream()
                .anyMatch(s -> s.title.equalsIgnoreCase("Projects"));

        if (!hasProjects) {
            warnings.add("Resume should contain a Projects section");
        }
    }

    // ================= REPORT =================
    private void printReport() {
        System.out.println("\n==========================================");
        System.out.println("  Semantic Analysis Report");
        System.out.println("==========================================");

        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println("✔ No issues found.");
            return;
        }

        if (!errors.isEmpty()) {
            System.out.println("\n❌ Errors:");
            for (String err : errors) {
                System.out.println("  - " + err);
            }
        }

        if (!warnings.isEmpty()) {
            System.out.println("\n⚠️ Warnings:");
            for (String warn : warnings) {
                System.out.println("  - " + warn);
            }
        }

        System.out.println("\n==========================================");
    }
}