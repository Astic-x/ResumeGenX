package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreativeBoldGenerator implements ResumeGenerator {

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

    // Golden Rule: Dynamic Parsing
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

        List<Section> leftSections = new ArrayList<>();
        List<Section> rightSections = new ArrayList<>();

        for (Section s : resume.sections) {
            // Sidebar sections: Skills, Languages, Tech, Tools
            if (sectionIs(s.title, "skill", "language", "tech", "tool", "arsenal")) {
                leftSections.add(s);
            } else {
                rightSections.add(s);
            }
        }

        buildPreamble(latex);

        // Setup Paracol for split layout
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
        // Golden Rule: Base font locked strictly to 12pt
        b.append("\\documentclass[letterpaper, 12pt]{article}\n\n");

        b.append("\\usepackage[top=0.6in, bottom=0.6in, left=0.4in, right=0.6in]{geometry}\n");
        b.append("\\usepackage[T1]{fontenc}\n");
        b.append("\\usepackage[utf8]{inputenc}\n");
        b.append("\\usepackage[scaled]{helvet}\n");
        b.append("\\renewcommand{\\familydefault}{\\sfdefault}\n");

        b.append("\\usepackage{xcolor}\n");
        b.append("\\usepackage{paracol}\n");
        b.append("\\usepackage{enumitem}\n");
        b.append("\\usepackage{tikz}\n");
        b.append("\\usepackage{eso-pic}\n");
        b.append("\\usepackage[hidelinks]{hyperref}\n\n");

        b.append("\\definecolor{SidebarDark}{HTML}{0A192F}\n");
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

        // Section Headers: 14pt, Uppercase, with a tight rule
        b.append("\\newcommand{\\cvsection}[1]{%\n");
        b.append("  \\vspace*{10pt}\n");
        b.append("  \\noindent{\\fontsize{14pt}{16pt}\\selectfont\\bfseries\\uppercase{#1}}\\par\n");
        b.append("  \\vspace{2pt}\\hrulefill\\par\\vspace{8pt}\n");
        b.append("}\n\n");

        // Sidebar background painter
        b.append("\\AddToShipoutPictureBG{%\n");
        b.append("  \\begin{tikzpicture}[remember picture,overlay]\n");
        b.append(
                "    \\fill[SidebarDark] (current page.north west) rectangle ([xshift=0.34\\paperwidth]current page.south west);\n");
        b.append("  \\end{tikzpicture}%\n");
        b.append("}\n\n");

        b.append("\\begin{document}\n\n");
    }

    private void buildLeftColumn(StringBuilder b, Resume resume, List<Section> leftSections) {
        b.append("\\raggedright\\color{white}\n");
        b.append("\\setlength{\\baselineskip}{1.3\\baselineskip}\n\n");

        b.append("\\vspace*{10pt}\n");
        b.append("{\\fontsize{14pt}{16pt}\\selectfont\\bfseries PERSONAL INFO}\\par\n");
        b.append("\\vspace{2pt}\\rule{\\linewidth}{0.4pt}\\par\\vspace{8pt}\n");

        Set<String> consumedHeaderKeys = new HashSet<>();
        consumeKey(resume.headerInfo, consumedHeaderKeys, "name", "title", "role", "about", "summary", "image",
                "photo");

        for (String k : resume.headerInfo.keySet()) {
            if (!consumedHeaderKeys.contains(k)) {
                String val = escapeLatex(resume.headerInfo.get(k));
                String cleanVal = val.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
                b.append("\\textbf{").append(escapeLatex(k)).append("}\\par\n");
                b.append("{\\fontsize{12pt}{14pt}\\selectfont ").append(cleanVal).append("}\\par\\vspace{6pt}\n");
            }
        }

        for (Section section : leftSections) {
            boolean isFirstSub = true;
            for (SubSection sub : section.subSections) {
                // Golden Rule: Unbreakable Box
                b.append("\\begin{minipage}{\\linewidth}\n");

                // Header inside the box
                if (isFirstSub) {
                    b.append("\\vspace*{10pt}\n");
                    b.append("{\\fontsize{14pt}{16pt}\\selectfont\\bfseries ")
                            .append(escapeLatex(section.title.toUpperCase())).append("}\\par\n");
                    b.append("\\vspace{2pt}\\rule{\\linewidth}{0.4pt}\\par\\vspace{8pt}\n");
                    isFirstSub = false;
                }

                if (sub.title != null && !sub.title.isEmpty()) {
                    b.append("\\textbf{").append(escapeLatex(sub.title)).append("}\\par\\vspace{2pt}\n");
                }

                for (String key : sub.keyValues.keySet()) {
                    b.append("\\textbf{").append(escapeLatex(key)).append("}: ")
                            .append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                }

                if (!sub.bullets.isEmpty()) {
                    b.append("\\begin{itemize}[leftmargin=1.2em, font=\\color{white}]\n");
                    for (String bullet : sub.bullets) {
                        b.append("  \\item ").append(escapeLatex(cleanBullet(bullet))).append("\n");
                    }
                    b.append("\\end{itemize}\n");
                }
                b.append("\\end{minipage}\\par\\vspace{10pt}\n");
            }
        }
    }

    private void buildRightColumn(StringBuilder b, Resume resume, List<Section> rightSections) {
        b.append("\\raggedright\\color{TextMain}\n");
        b.append("\\setlength{\\baselineskip}{1.3\\baselineskip}\n\n");

        Set<String> consumedHeaderKeys = new HashSet<>();
        String name = consumeKey(resume.headerInfo, consumedHeaderKeys, "name");
        String title = consumeKey(resume.headerInfo, consumedHeaderKeys, "title", "role", "profession");
        String about = consumeKey(resume.headerInfo, consumedHeaderKeys, "about", "summary", "objective", "profile");

        // Bold Name: allowed large hero size
        b.append("{\\fontsize{32pt}{36pt}\\selectfont\\bfseries ").append(name).append("}\\par\\vspace{4pt}\n");
        if (!title.isEmpty()) {
            // Title strictly 14pt
            b.append("{\\fontsize{14pt}{16pt}\\selectfont\\color{TextLight} ").append(title)
                    .append("}\\par\\vspace{16pt}\n");
        }

        if (!about.isEmpty()) {
            b.append("\\cvsection{Summary}\n");
            b.append(about).append("\\par\\vspace{8pt}\n");
        }

        Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation",
                "Graduation", "Year", "TechStack", "Highlights");

        for (Section section : rightSections) {
            boolean isFirstSub = true;
            for (SubSection sub : section.subSections) {
                // Golden Rule: Unbreakable Box
                b.append("\\begin{minipage}{\\linewidth}\n");

                // Header inside the box
                if (isFirstSub) {
                    b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                    isFirstSub = false;
                }

                Set<String> consumedSubKeys = new HashSet<>();
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "position");
                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");
                String loc = consumeKey(sub.keyValues, consumedSubKeys, "loc", "city", "state");

                // Content defaults to document's 12pt base
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
                b.append("\\end{minipage}\\par\\vspace{12pt}\n\n");
            }
        }
    }
}