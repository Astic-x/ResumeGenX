package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MonogramGenerator implements ResumeGenerator {

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

    private String getInitials(String name) {
        if (name == null || name.isEmpty())
            return "RE";
        String[] parts = name.trim().split("\\s+");
        String initials = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials += parts[0].substring(0, 1).toUpperCase();
        }
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
        }
        return initials.isEmpty() ? "RE" : initials;
    }

    @Override
    public String generate(Resume resume) {
        StringBuilder latex = new StringBuilder();

        List<Section> leftSections = new ArrayList<>();
        List<Section> rightSections = new ArrayList<>();

        for (Section s : resume.sections) {
            // Summary, Education, Skills, Languages go to the left
            if (sectionIs(s.title, "education", "skill", "language", "tech", "certification", "summary", "about")) {
                leftSections.add(s);
            } else {
                rightSections.add(s);
            }
        }

        buildPreamble(latex);
        buildHeader(latex, resume);

        // Setup Paracol
        latex.append("\\columnratio{0.32}\n");
        latex.append("\\setlength{\\columnsep}{0.05\\textwidth}\n");
        latex.append("\\begin{paracol}{2}\n\n");

        buildLeftColumn(latex, resume, leftSections);

        latex.append("\n\\switchcolumn\n\n");

        buildRightColumn(latex, resume, rightSections);

        latex.append("\\end{paracol}\n");
        latex.append("\\end{document}\n");

        return latex.toString();
    }

    private void buildPreamble(StringBuilder b) {
        // Golden Rule: Base font locked to 12pt
        b.append("\\documentclass[letterpaper, 12pt]{article}\n\n");

        b.append("\\usepackage[top=0.6in, bottom=0.6in, left=0.6in, right=0.6in]{geometry}\n");
        b.append("\\usepackage[T1]{fontenc}\n");
        b.append("\\usepackage[utf8]{inputenc}\n");
        b.append("\\usepackage{mathptmx} % Professional Serif Font\n");

        b.append("\\usepackage{xcolor}\n");
        b.append("\\usepackage{paracol}\n");
        b.append("\\usepackage{enumitem}\n");
        b.append("\\usepackage{tikz}\n");
        b.append("\\usetikzlibrary{shapes.geometric}\n");
        b.append("\\usepackage[hidelinks]{hyperref}\n\n");

        // The Academic Pro Signature Blue
        b.append("\\definecolor{HexBlue}{HTML}{2B5B84}\n");
        b.append("\\definecolor{TextMain}{HTML}{000000}\n");
        b.append("\\definecolor{TextLight}{HTML}{444444}\n\n");

        b.append("\\pagestyle{empty}\n");
        b.append("\\setlength{\\parindent}{0pt}\n\n");

        b.append("\\setlist[itemize]{\n");
        b.append("  label=\\textbullet,\n");
        b.append("  leftmargin=1.5em,\n");
        b.append("  topsep=4pt,\n");
        b.append("  itemsep=2pt,\n");
        b.append("  parsep=0pt\n");
        b.append("}\n\n");

        // Golden Rule: Headers locked to 14pt, colored HexBlue
        b.append("\\newcommand{\\cvsection}[1]{%\n");
        b.append("  \\vspace*{10pt}\n");
        b.append("  \\noindent{\\fontsize{14pt}{16pt}\\selectfont\\bfseries\\color{HexBlue}\\uppercase{#1}}\\par\n");
        b.append("  \\vspace{2pt}{\\color{HexBlue}\\hrulefill}\\par\\vspace{8pt}\n");
        b.append("}\n\n");

        b.append("\\begin{document}\n\n");
    }

    private void buildHeader(StringBuilder b, Resume resume) {
        Set<String> consumedHeaderKeys = new HashSet<>();
        String name = consumeKey(resume.headerInfo, consumedHeaderKeys, "name");
        String title = consumeKey(resume.headerInfo, consumedHeaderKeys, "title", "role", "profession");
        consumeKey(resume.headerInfo, consumedHeaderKeys, "image", "photo", "avatar"); // hide image key

        String initials = getInitials(name);

        b.append("\\begin{minipage}[c]{0.15\\textwidth}\n");
        b.append("  \\begin{tikzpicture}\n");
        b.append(
                "    \\node[regular polygon, regular polygon sides=6, draw=HexBlue, thick, minimum size=1.6cm, text=HexBlue, font=\\Large\\bfseries] {\\uppercase{")
                .append(initials).append("}};\n");
        b.append("  \\end{tikzpicture}\n");
        b.append("\\end{minipage}%\n");

        b.append("\\begin{minipage}[c]{0.45\\textwidth}\n");
        b.append("  {\\fontsize{26pt}{30pt}\\selectfont\\color{HexBlue} ").append(name).append("}\\par\n");
        if (!title.isEmpty()) {
            b.append("  \\vspace{4pt}{\\large\\color{TextLight} ").append(title).append("}\\par\n");
        }
        b.append("\\end{minipage}%\n");

        b.append("\\begin{minipage}[c]{0.4\\textwidth}\n");
        b.append("  \\raggedleft\\small\n");

        for (String k : resume.headerInfo.keySet()) {
            if (!consumedHeaderKeys.contains(k) && !k.toLowerCase().contains("about")
                    && !k.toLowerCase().contains("summary")) {
                String val = escapeLatex(resume.headerInfo.get(k));
                String cleanVal = val.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
                b.append("  \\textbf{").append(cleanVal).append("}\\par\n");
            }
        }
        b.append("\\end{minipage}\n");
        b.append("\\vspace{16pt}\n\n");
    }

    private void buildLeftColumn(StringBuilder b, Resume resume, List<Section> leftSections) {
        b.append("\\raggedright\n"); 
        b.append("\\color{TextMain}\n");
        b.append("\\setlength{\\baselineskip}{1.3\\baselineskip}\n\n");

        // We pull "About" / "Summary" directly from Header if it exists
        Set<String> dummy = new HashSet<>();
        String about = consumeKey(resume.headerInfo, dummy, "about", "summary", "objective", "profile");
        if (!about.isEmpty()) {
            b.append("\\cvsection{Summary}\n");
            b.append(about).append("\\par\\vspace{8pt}\n");
        }

        for (Section section : leftSections) {
            // Skip explicit summary section if we already printed it from header
            if (sectionIs(section.title, "summary", "about"))
                continue;

            if (section.subSections.isEmpty()) {
                b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                continue;
            }

            boolean isFirstSub = true;
            for (SubSection sub : section.subSections) {
                // Golden Rule: Unbreakable Box
                b.append("\\begin{minipage}{\\linewidth}\n");

                // Golden Rule: Header inside the box
                if (isFirstSub) {
                    b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                    isFirstSub = false;
                }

                Set<String> consumedSubKeys = new HashSet<>();
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "level");
                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");

                if (sub.title != null && !sub.title.isEmpty()) {
                    b.append("\\textbf{").append(escapeLatex(sub.title)).append("}\\par\n");
                }
                if (!role.isEmpty()) {
                    b.append("{\\textit{").append(role).append("}}\\par\n");
                }
                if (!time.isEmpty()) {
                    b.append("{\\color{TextLight}").append(time).append("}\\par\n");
                }

                // Print leftover keys generically
                for (String key : sub.keyValues.keySet()) {
                    if (!consumedSubKeys.contains(key)) {
                        b.append("{\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("}\\par\n");
                    }
                }

                if (!sub.bullets.isEmpty()) {
                    b.append("\\begin{itemize}[leftmargin=1.2em]\n");
                    for (String bullet : sub.bullets) {
                        b.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                    }
                    b.append("\\end{itemize}\n");
                }
                b.append("\\end{minipage}\\par\\vspace{8pt}\n");
            }
        }
    }

    private void buildRightColumn(StringBuilder b, Resume resume, List<Section> rightSections) {
        b.append("\\raggedright\n");
        b.append("\\color{TextMain}\n");
        b.append("\\setlength{\\baselineskip}{1.3\\baselineskip}\n\n");

        Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation",
                "Graduation", "Year", "TechStack", "Highlights");

        for (Section section : rightSections) {
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

                Set<String> consumedSubKeys = new HashSet<>();
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "position");
                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");
                String loc = consumeKey(sub.keyValues, consumedSubKeys, "loc", "city", "state");

                b.append("{\\textbf{").append(escapeLatex(sub.title)).append("}} \\hfill {\\textbf{").append(time)
                        .append("}}\\par\\vspace{2pt}\n");

                if (!role.isEmpty() || !loc.isEmpty()) {
                    b.append("{\\color{TextLight}\\textit{").append(role)
                            .append("}} \\hfill {\\color{TextLight}\\textit{").append(loc)
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
                b.append("\\end{minipage}\\par\\vspace{10pt}\n\n");
            }
        }
    }
}