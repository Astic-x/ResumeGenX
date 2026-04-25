package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CenteredElegantGenerator implements ResumeGenerator {

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

        buildPreamble(latex);
        buildHeader(latex, resume);
        buildBody(latex, resume);

        latex.append("\\end{document}\n");
        return latex.toString();
    }

    private void buildPreamble(StringBuilder b) {
        // Golden Rule: Base font locked strictly to 12pt
        b.append("\\documentclass[letterpaper, 12pt]{article}\n\n");

        b.append("\\usepackage[top=0.7in, bottom=0.7in, left=0.7in, right=0.7in]{geometry}\n");
        b.append("\\usepackage[T1]{fontenc}\n");
        b.append("\\usepackage[utf8]{inputenc}\n");
        b.append("\\usepackage{mathptmx} % Classic Academic Serif Font\n");
        b.append("\\usepackage{enumitem}\n");
        b.append("\\usepackage[hidelinks]{hyperref}\n\n");

        b.append("\\pagestyle{empty}\n");
        b.append("\\setlength{\\parindent}{0pt}\n\n");

        b.append("\\setlist[itemize]{\n");
        b.append("  label=\\textbullet,\n");
        b.append("  leftmargin=1.5em,\n");
        b.append("  topsep=4pt,\n");
        b.append("  itemsep=2pt,\n");
        b.append("  parsep=0pt\n");
        b.append("}\n\n");

        // Golden Rule: Section headers locked strictly to 14pt
        b.append("\\newcommand{\\cvsection}[1]{%\n");
        b.append("  \\vspace*{12pt}\n");
        b.append("  \\begin{center}\n");
        b.append("    {\\fontsize{14pt}{16pt}\\selectfont\\scshape #1}\n");
        b.append("  \\end{center}\n");
        b.append("  \\vspace{-14pt}\\hrulefill\\par\\vspace{8pt}\n");
        b.append("}\n\n");

        b.append("\\begin{document}\n\n");
    }

    private void buildHeader(StringBuilder b, Resume resume) {
        Set<String> consumedHeaderKeys = new HashSet<>();
        String name = consumeKey(resume.headerInfo, consumedHeaderKeys, "name");
        String title = consumeKey(resume.headerInfo, consumedHeaderKeys, "title", "role", "profession");
        String about = consumeKey(resume.headerInfo, consumedHeaderKeys, "about", "summary", "objective", "profile");
        consumeKey(resume.headerInfo, consumedHeaderKeys, "image", "photo", "avatar");

        b.append("\\begin{center}\n");
        // Name allowed to be large hero size
        b.append("  {\\fontsize{28pt}{32pt}\\selectfont\\scshape ").append(name).append("}\\\\[6pt]\n");
        if (!title.isEmpty()) {
            // 🔥 FIX: Replaced \large with strict 14pt boundary
            b.append("  {\\fontsize{14pt}{16pt}\\selectfont\\textit{").append(title).append("}}\\\\[6pt]\n");
        }

        List<String> contactInfo = new ArrayList<>();
        for (String k : resume.headerInfo.keySet()) {
            if (!consumedHeaderKeys.contains(k)) {
                String val = escapeLatex(resume.headerInfo.get(k));
                String cleanVal = val.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
                contactInfo.add(cleanVal);
            }
        }

        if (!contactInfo.isEmpty()) {
            b.append("  ").append(String.join(" \\quad\\textbullet\\quad ", contactInfo)).append("\n");
        }
        b.append("\\end{center}\n\\vspace{4pt}\n\n");

        if (!about.isEmpty()) {
            b.append(about).append("\\par\\vspace{8pt}\n");
        }
    }

    private void buildBody(StringBuilder b, Resume resume) {
        Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation",
                "Graduation", "Year", "TechStack", "Highlights");

        for (Section section : resume.sections) {
            if (section.subSections.isEmpty()) {
                b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                continue;
            }

            boolean isFirstSub = true;

            for (SubSection sub : section.subSections) {
                // Golden Rule: Unbreakable Box
                b.append("\\begin{minipage}{\\linewidth}\n");

                if (isFirstSub) {
                    b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                    isFirstSub = false;
                }

                if (sectionIs(section.title, "skill", "language", "tech", "tool", "arsenal")) {
                    if (sub.title != null && !sub.title.isEmpty()) {
                        b.append("\\textbf{").append(escapeLatex(sub.title)).append("}\\par\\vspace{2pt}\n");
                    }

                    for (String key : sub.keyValues.keySet()) {
                        b.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }

                    if (!sub.bullets.isEmpty()) {
                        b.append("\\begin{itemize}\n");
                        for (String bullet : sub.bullets) {
                            b.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                        }
                        b.append("\\end{itemize}\n");
                    }

                    b.append("\\end{minipage}\\par\\vspace{6pt}\n");
                    continue;
                }

                Set<String> consumedSubKeys = new HashSet<>();
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "position");
                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");
                String loc = consumeKey(sub.keyValues, consumedSubKeys, "loc", "city", "state");

                b.append("\\noindent {\\textbf{").append(escapeLatex(sub.title)).append("}} \\hfill {\\textbf{")
                        .append(time).append("}}\\par\\vspace{2pt}\n");

                if (!role.isEmpty() || !loc.isEmpty()) {
                    b.append("\\noindent {\\textit{").append(role).append("}} \\hfill {\\textit{").append(loc)
                            .append("}}\\par\\vspace{4pt}\n");
                }

                String description = consumeKey(sub.keyValues, consumedSubKeys, "desc", "summary", "about", "detail");
                if (!description.isEmpty()) {
                    b.append(description).append("\\par\\vspace{4pt}\n");
                }

                for (String key : sub.keyValues.keySet()) {
                    if (!consumedSubKeys.contains(key) && !ignore.contains(key)) {
                        b.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }
                }

                if (!sub.bullets.isEmpty()) {
                    b.append("\\vspace{-4pt}\n");
                    b.append("\\begin{itemize}\n");
                    for (String bullet : sub.bullets) {
                        b.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                    }
                    b.append("\\end{itemize}\n");
                }

                b.append("\\end{minipage}\\par\\vspace{12pt}\n\n");
            }
        }
    }
}