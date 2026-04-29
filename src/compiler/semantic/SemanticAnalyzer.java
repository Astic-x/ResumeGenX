package compiler.semantic;

import compiler.ast.*;
import java.util.*;

public class SemanticAnalyzer {

    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    // Return warnings to the caller
    public List<String> analyze(Resume resume) {
        checkHeader(resume);
        checkSections(resume);
        checkMandatorySections(resume);
        printReport();

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(" | ", errors));
        }

        return warnings;
    }

    private void checkHeader(Resume resume) {
        Map<String, String> header = resume.headerInfo;

        if (!header.containsKey("Name") || header.get("Name").trim().isEmpty()) {
            errors.add("Missing required field: Name");
        }

        if (!header.containsKey("Email") || header.get("Email").trim().isEmpty()) {
            errors.add("Missing required field: Email");
        }

        if (header.containsKey("Email") && !header.get("Email").trim().isEmpty()) {
            String email = header.get("Email").trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errors.add("Invalid Email format: " + email);
            }
        }

        if (header.containsKey("GitHub")) {
            String github = header.get("GitHub").trim();
            if (!github.contains("github.com") && github.contains(" ")) {
                warnings.add("GitHub field contains spaces. Provide a valid handle or URL.");
            }
        }

        for (String key : header.keySet()) {
            if (header.get(key).trim().isEmpty()) {
                warnings.add("Header field '" + key + "' is declared but empty.");
            }
        }
    }

    private void checkSections(Resume resume) {
        if (resume.sections.isEmpty()) {
            errors.add("Resume must contain at least one Section.");
            return;
        }

        Set<String> sectionNames = new HashSet<>();

        for (Section section : resume.sections) {
            if (!sectionNames.add(section.title)) {
                warnings.add("Duplicate Section found: " + section.title + ". Consider merging them.");
            }
            validateSectionName(section.title);
            checkSection(section);
        }
    }

    private void validateSectionName(String title) {
        Set<String> validSections = Set.of(
                "Education", "Experience", "Projects",
                "Skills", "Certifications", "Awards", "Publications", "Languages", "Summary");

        boolean isValid = validSections.stream().anyMatch(title::equalsIgnoreCase);

        if (!isValid) {
            warnings.add("Unconventional Section Name: '" + title + "'. Ensure this is intentional.");
        }
    }

    private void checkSection(Section section) {
        if (section.subSections.isEmpty()) {
            warnings.add("Section '" + section.title + "' is empty (has no SubSections).");
        }

        for (SubSection sub : section.subSections) {
            checkSubSection(sub, section.title);
        }
    }

    private void checkSubSection(SubSection sub, String sectionName) {
        boolean hasKeyValues = !sub.keyValues.isEmpty();
        boolean hasBullets = !sub.bullets.isEmpty();

        if (!hasKeyValues && !hasBullets) {
            warnings.add("SubSection '" + sub.title + "' in Section '" + sectionName + "' has no content.");
        }

        for (Map.Entry<String, String> entry : sub.keyValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().trim();

            if (key.equalsIgnoreCase("Highlights")) {
                if (sub.bullets.isEmpty()) {
                    warnings.add(
                            "SubSection '" + sub.title + "' has a 'Highlights' key but no bullet points beneath it.");
                }
                continue;
            }

            // Allow any custom key for dynamic templates

            if (value.isEmpty()) {
                warnings.add("Empty value for key '" + key + "' in SubSection '" + sub.title + "'.");
            }

            if (key.equalsIgnoreCase("Description") && value.length() > 0 && value.length() < 20) {
                warnings.add("Description is very brief in '" + sub.title + "'. Consider expanding.");
            }

            if (key.equalsIgnoreCase("StartDate") || key.equalsIgnoreCase("Timeline")
                    || key.equalsIgnoreCase("ExpectedGraduation")) {
                if (!value.isEmpty() && !value.matches(".*(?:19|20)\\d{2}.*")
                        && !value.toLowerCase().contains("present")) {
                    warnings.add("Unusual date format in SubSection '" + sub.title + "' (" + key + ": " + value
                            + "). Expected a year (e.g., 2026).");
                }
            }
        }

        for (String bullet : sub.bullets) {
            String actualBulletText = bullet.contains(":") ? bullet.substring(bullet.indexOf(":") + 1).trim()
                    : bullet.trim();

            if (actualBulletText.length() > 0 && actualBulletText.length() < 15) {
                warnings.add(
                        "Bullet point in '" + sub.title + "' is too short (< 15 chars). Elaborate on your impact.");
            }
        }
    }

    private void checkMandatorySections(Resume resume) {
        boolean hasEducation = resume.sections.stream().anyMatch(s -> s.title.equalsIgnoreCase("Education"));
        boolean hasExperience = resume.sections.stream().anyMatch(s -> s.title.equalsIgnoreCase("Experience"));
        boolean hasProjects = resume.sections.stream().anyMatch(s -> s.title.equalsIgnoreCase("Projects"));

        if (!hasEducation) {
            warnings.add("Missing 'Education' section. This is highly recommended for students/recent grads.");
        }

        if (!hasExperience && !hasProjects) {
            warnings.add("Resume lacks both 'Experience' and 'Projects' sections. You need to show your work!");
        }
    }

    private void printReport() {
        System.out.println("\n Semantic Analysis Report");
        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println("✔ No logic or formatting issues found. Perfect!");
            return;
        }

        if (!errors.isEmpty()) {
            System.out.println("\n❌ CRITICAL ERRORS (Will halt generation):");
            for (String err : errors) {
                System.out.println("  - " + err);
            }
        }

        if (!warnings.isEmpty()) {
            System.out.println("\n⚠️  WARNINGS (Suggestions for a better resume):");
            for (String warn : warnings) {
                System.out.println("  - " + warn);
            }
        }
    }
}