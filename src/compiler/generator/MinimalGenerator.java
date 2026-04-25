package compiler.generator;

import compiler.ast.*;
import java.util.Set;

public class MinimalGenerator implements ResumeGenerator {

    private static String escapeLatex(String text) {
        if (text == null)
            return "";
        text = text.trim();
        if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2) {
            text = text.substring(1, text.length() - 1);
        }
        return text.replace("%", "\\%")
                .replace("_", "\\_")
                .replace("&", "\\&")
                .replace("$", "\\$")
                .replace("#", "\\#");
    }

    private static String getHeaderVal(Resume resume, String target) {
        for (String key : resume.headerInfo.keySet()) {
            if (key.equalsIgnoreCase(target))
                return escapeLatex(resume.headerInfo.get(key));
        }
        return "";
    }

    private static boolean sectionIs(String title, String... keywords) {
        String t = title.toLowerCase();
        for (String k : keywords) {
            if (t.contains(k.toLowerCase()))
                return true;
        }
        return false;
    }

    private static String cleanBullet(String bullet) {
        if (bullet == null)
            return "";
        bullet = bullet.trim();
        if (bullet.toLowerCase().startsWith("highlights:"))
            bullet = bullet.substring(11).trim();
        if (bullet.startsWith("-"))
            bullet = bullet.substring(1).trim();
        return bullet;
    }

    @Override
    public String generate(Resume resume) {
        StringBuilder latex = new StringBuilder();

        // ================= PREAMBLE =================
        latex.append("\\documentclass[letterpaper, 11pt]{article}\n\n");
        latex.append("\\usepackage[top=0.6in, bottom=0.6in, left=0.6in, right=0.6in]{geometry}\n");
        latex.append("\\usepackage[T1]{fontenc}\n");
        latex.append("\\usepackage{mathptmx} % Classic Serif Font\n");
        latex.append("\\usepackage{enumitem}\n");
        latex.append("\\usepackage{tabularx}\n");
        latex.append("\\usepackage[hidelinks]{hyperref}\n\n");

        latex.append("\\pagestyle{empty}\n");
        latex.append("\\setlength{\\parindent}{0pt}\n\n");

        latex.append("\\setlist[itemize]{\n");
        latex.append("  label=\\textbullet,\n");
        latex.append("  leftmargin=1.5em,\n");
        latex.append("  topsep=2pt,\n");
        latex.append("  itemsep=0pt,\n");
        latex.append("  parsep=0pt\n");
        latex.append("}\n\n");

        // Custom Section Command
        latex.append("\\newcommand{\\cvsection}[1]{%\n");
        latex.append("  \\vspace{12pt}\n");
        latex.append("  \\noindent{\\Large\\bfseries\\uppercase{#1}}\\par\n");
        latex.append("  \\vspace{2pt}\\hrulefill\\par\\vspace{8pt}\n");
        latex.append("}\n\n");

        latex.append("\\begin{document}\n\n");

        // ================= HEADER =================
        String name = getHeaderVal(resume, "Name");
        String title = getHeaderVal(resume, "Title");
        String email = getHeaderVal(resume, "Email");
        String phone = getHeaderVal(resume, "Phone");
        String loc = getHeaderVal(resume, "Location");
        String githubRaw = getHeaderVal(resume, "GitHub");
        String github = githubRaw.replaceFirst("^https?://", "").replaceFirst("^www\\.", "")
                .replaceFirst("^github\\.com/", "");

        latex.append("\\begin{center}\n");
        latex.append("  {\\fontsize{28pt}{32pt}\\selectfont\\bfseries ").append(name).append("}\\\\[4pt]\n");
        if (!title.isEmpty()) {
            latex.append("  {\\Large ").append(title).append("}\\\\[6pt]\n");
        }

        java.util.List<String> contact = new java.util.ArrayList<>();
        if (!email.isEmpty())
            contact.add(email);
        if (!phone.isEmpty())
            contact.add(phone);
        if (!loc.isEmpty())
            contact.add(loc);
        if (!github.isEmpty())
            contact.add(github);

        latex.append("  ").append(String.join(" \\quad|\\quad ", contact)).append("\n");
        latex.append("\\end{center}\n\\vspace{4pt}\n\n");

        String about = getHeaderVal(resume, "About");
        if (!about.isEmpty()) {
            latex.append(about).append("\\par\\vspace{8pt}\n");
        }

        // ================= BODY =================
        Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation",
                "Graduation", "Year", "TechStack", "Highlights", "Description");

        for (Section section : resume.sections) {
            latex.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");

            // SKILLS / LANGUAGES SECTION HANDLER
            if (sectionIs(section.title, "skill", "language", "tech")) {
                for (SubSection sub : section.subSections) {
                    latex.append("\\textbf{").append(escapeLatex(sub.title)).append("}: ");

                    java.util.List<String> skillItems = new java.util.ArrayList<>();
                    for (String key : sub.keyValues.keySet()) {
                        skillItems.add(
                                "\\textit{" + escapeLatex(key) + "} (" + escapeLatex(sub.keyValues.get(key)) + ")");
                    }
                    for (String bullet : sub.bullets) {
                        skillItems.add(escapeLatex(cleanBullet(bullet)));
                    }

                    latex.append(String.join(", ", skillItems)).append("\\par\\vspace{4pt}\n");
                }
                continue;
            }

            // EXPERIENCE / PROJECTS / EDUCATION HANDLER
            for (SubSection sub : section.subSections) {
                String role = escapeLatex(sub.keyValues.getOrDefault("Role", sub.keyValues.getOrDefault("Degree", "")));
                String locValue = escapeLatex(sub.keyValues.getOrDefault("Location", ""));
                String time = escapeLatex(sub.keyValues.getOrDefault("Timeline",
                        sub.keyValues.getOrDefault("ExpectedGraduation",
                                sub.keyValues.getOrDefault("Graduation",
                                        sub.keyValues.getOrDefault("Year", "")))));

                // Heading Row 1: Title & Timeline
                latex.append("\\begin{tabularx}{\\linewidth}{@{}X r@{}}\n");
                latex.append("  \\textbf{\\large ").append(escapeLatex(sub.title)).append("} & \\textbf{").append(time)
                        .append("} \\\\\n");
                // Heading Row 2: Role & Location
                if (!role.isEmpty() || !locValue.isEmpty()) {
                    latex.append("  \\textit{").append(role).append("} & \\textit{").append(locValue)
                            .append("} \\\\\n");
                }
                latex.append("\\end{tabularx}\\par\\vspace{2pt}\n");

                // Explicitly check for Description and render as a paragraph (NO DASHES)
                String description = escapeLatex(sub.keyValues.getOrDefault("Description", ""));
                if (!description.isEmpty()) {
                    latex.append(description).append("\\par\\vspace{4pt}\n");
                }

                // Any other arbitrary key-value pairs (rendered cleanly)
                for (String key : sub.keyValues.keySet()) {
                    if (!ignore.contains(key)) {
                        latex.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }
                }

                // Bullets
                if (!sub.bullets.isEmpty()) {
                    latex.append("\\begin{itemize}\n");
                    for (String bullet : sub.bullets) {
                        latex.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                    }
                    latex.append("\\end{itemize}\n");
                }
                latex.append("\\vspace{8pt}\n\n");
            }
        }

        latex.append("\\end{document}\n");
        return latex.toString();
    }
}