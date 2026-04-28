package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private String consumeKey(Map<String, String> map, Set<String> consumed, String... keywords) {
        for (String k : map.keySet()) {
            if (!consumed.contains(k)) {
                String lowerK = k.toLowerCase();
                for (String keyword : keywords) {
                    if (lowerK.contains(keyword)) {
                        consumed.add(k);
                        return escapeLatex(map.get(k));
                    }
                }
            }
        }
        return "";
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

        // Significantly reduced spacing around the section headers
        latex.append("\\newcommand{\\cvsection}[1]{%\n");
        latex.append("  \\vspace{6pt}\n");
        latex.append("  \\noindent{\\Large\\bfseries\\uppercase{#1}}\\par\n");
        latex.append("  \\vspace{2pt}\\hrulefill\\par\\vspace{4pt}\n");
        latex.append("}\n\n");

        latex.append("\\begin{document}\n\n");

        // HEADER
        Set<String> consumedHeaderKeys = new HashSet<>();

        String name = consumeKey(resume.headerInfo, consumedHeaderKeys, "name");
        String title = consumeKey(resume.headerInfo, consumedHeaderKeys, "title", "role", "profession");
        String about = consumeKey(resume.headerInfo, consumedHeaderKeys, "about", "summary", "objective", "profile");
        consumeKey(resume.headerInfo, consumedHeaderKeys, "image", "photo", "avatar");

        latex.append("\\begin{center}\n");
        latex.append("  {\\fontsize{28pt}{32pt}\\selectfont\\bfseries ").append(name).append("}\\\\[4pt]\n");
        if (!title.isEmpty()) {
            latex.append("  {\\Large ").append(title).append("}\\\\[6pt]\n");
        }

        List<String> contactInfo = new ArrayList<>();
        for (String k : resume.headerInfo.keySet()) {
            if (!consumedHeaderKeys.contains(k)) {
                String val = escapeLatex(resume.headerInfo.get(k));
                String displayVal = val.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
                contactInfo.add(displayVal);
            }
        }

        if (!contactInfo.isEmpty()) {
            latex.append("  ").append(String.join(" \\quad|\\quad ", contactInfo)).append("\n");
        }
        //Tighter header spacing
        latex.append("\\end{center}\n\\vspace{2pt}\n\n");

        if (!about.isEmpty()) {
            latex.append(about).append("\\par\\vspace{4pt}\n");
        }

        // BODY
        for (Section section : resume.sections) {
            latex.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");

            // SKILLS / LANGUAGES / TECH HANDLER
            if (sectionIs(section.title, "skill", "language", "tech", "tool", "arsenal")) {
                for (SubSection sub : section.subSections) {
                    if (sub.title != null && !sub.title.isEmpty()) {
                        latex.append("\\textbf{").append(escapeLatex(sub.title)).append("}\\par\\vspace{2pt}\n");
                    }

                    // No more string joining. Prints every key strictly on a new line.
                    for (String key : sub.keyValues.keySet()) {
                        latex.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }

                    if (!sub.bullets.isEmpty()) {
                        latex.append("\\begin{itemize}\n");
                        for (String bullet : sub.bullets) {
                            latex.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                        }
                        latex.append("\\end{itemize}\n");
                    }
                    latex.append("\\vspace{4pt}\n");
                }
                continue;
            }

            // STANDARD SECTIONS (Experience, Education, Projects, etc.)
            for (SubSection sub : section.subSections) {
                Set<String> consumedSubKeys = new HashSet<>();

                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "position");
                String locValue = consumeKey(sub.keyValues, consumedSubKeys, "loc", "city", "state");

                latex.append("\\begin{tabularx}{\\linewidth}{@{}X r@{}}\n");
                latex.append("  \\textbf{\\large ").append(escapeLatex(sub.title)).append("} & \\textbf{").append(time)
                        .append("} \\\\\n");

                if (!role.isEmpty() || !locValue.isEmpty()) {
                    latex.append("  \\textit{").append(role).append("} & \\textit{").append(locValue)
                            .append("} \\\\\n");
                }
                latex.append("\\end{tabularx}\\par\\vspace{2pt}\n");

                String description = consumeKey(sub.keyValues, consumedSubKeys, "desc", "summary", "about", "detail");
                if (!description.isEmpty()) {
                    latex.append(description).append("\\par\\vspace{2pt}\n");
                }

                for (String key : sub.keyValues.keySet()) {
                    if (!consumedSubKeys.contains(key)) {
                        latex.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }
                }

                if (!sub.bullets.isEmpty()) {
                    latex.append("\\begin{itemize}\n");
                    for (String bullet : sub.bullets) {
                        latex.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                    }
                    latex.append("\\end{itemize}\n");
                }
                //Tighter spacing between subsections
                latex.append("\\vspace{6pt}\n\n");
            }
        }

        latex.append("\\end{document}\n");
        return latex.toString();
    }
}