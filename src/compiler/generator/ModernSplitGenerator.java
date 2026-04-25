package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModernSplitGenerator implements ResumeGenerator {

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

        List<Section> leftSections = new ArrayList<>();
        List<Section> rightSections = new ArrayList<>();

        for (Section s : resume.sections) {
            if (sectionIs(s.title, "education", "skill", "language", "tech", "certification")) {
                leftSections.add(s);
            } else {
                rightSections.add(s);
            }
        }

        buildPreamble(latex);

        latex.append("\\columnratio{0.28}\n");
        latex.append("\\setlength{\\columnsep}{0.06\\textwidth}\n");
        latex.append("\\begin{paracol}{2}\n\n");

        buildLeftColumn(latex, resume, leftSections);

        latex.append("\n\\switchcolumn\n\n");

        buildRightColumn(latex, resume, rightSections);

        latex.append("\\end{paracol}\n");
        latex.append("\\end{document}\n");

        return latex.toString();
    }

    private void buildPreamble(StringBuilder b) {
        // Base document is locked to 12pt
        b.append("\\documentclass[letterpaper, 12pt]{article}\n\n");

        b.append("\\usepackage[top=0.6in, bottom=0.6in, left=0.5in, right=0.6in]{geometry}\n");
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

        b.append("\\definecolor{SplitGray}{HTML}{F0F0F0}\n");
        b.append("\\definecolor{QuoteYellow}{HTML}{FFC107}\n");
        b.append("\\definecolor{TextMain}{HTML}{222222}\n");
        b.append("\\definecolor{TextLight}{HTML}{555555}\n\n");

        b.append("\\pagestyle{empty}\n");
        b.append("\\setlength{\\parindent}{0pt}\n\n");

        b.append("\\setlist[itemize]{\n");
        b.append("  label=\\textbullet,\n");
        b.append("  leftmargin=1.5em,\n");
        b.append("  topsep=4pt,\n");
        b.append("  itemsep=2pt,\n");
        b.append("  parsep=0pt\n");
        b.append("}\n\n");

        // Section headers locked to 14pt
        b.append("\\newcommand{\\cvsection}[1]{%\n");
        b.append("  \\vspace*{10pt}\n");
        b.append("  \\noindent{\\fontsize{14pt}{16pt}\\selectfont\\bfseries\\uppercase{#1}}\\par\n");
        b.append("  \\vspace{2pt}\\hrulefill\\par\\vspace{8pt}\n");
        b.append("}\n\n");

        b.append("\\AddToShipoutPictureBG{%\n");
        b.append("  \\begin{tikzpicture}[remember picture,overlay]\n");
        b.append(
                "    \\fill[SplitGray] (current page.north west) rectangle ([xshift=0.30\\paperwidth]current page.south west);\n");
        b.append("  \\end{tikzpicture}%\n");
        b.append("}\n\n");

        b.append("\\begin{document}\n\n");

        // Restored the massive 80pt graphic quote
        b.append("\\begin{tikzpicture}[remember picture,overlay]\n");
        b.append(
                "  \\node[text=QuoteYellow, font=\\fontsize{80}{80}\\selectfont\\bfseries, anchor=north east] at ([xshift=-0.4in, yshift=-0.3in]current page.north east) {''};\n");
        b.append("\\end{tikzpicture}\n\n");
    }

    private void buildLeftColumn(StringBuilder b, Resume resume, List<Section> leftSections) {
        b.append("\\raggedright\n");
        b.append("\\color{TextMain}\n");
        b.append("\\setlength{\\baselineskip}{1.3\\baselineskip}\n\n");

        b.append("\\cvsection{Contact}\n");

        Set<String> consumedHeaderKeys = new HashSet<>();
        consumeKey(resume.headerInfo, consumedHeaderKeys, "name", "title", "role", "about", "summary", "image",
                "photo");

        for (String k : resume.headerInfo.keySet()) {
            if (!consumedHeaderKeys.contains(k)) {
                String val = escapeLatex(resume.headerInfo.get(k));
                String cleanVal = val.replaceFirst("^https?://", "").replaceFirst("^www\\.", "");
                b.append("\\textbf{").append(escapeLatex(k)).append("}\\par\n");
                b.append("{\\color{TextLight}").append(cleanVal).append("}\\par\\vspace{6pt}\n");
            }
        }

        for (Section section : leftSections) {
            if (section.subSections.isEmpty()) {
                b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                continue;
            }

            boolean isFirstSub = true;
            for (SubSection sub : section.subSections) {
                b.append("\\begin{minipage}{\\linewidth}\n");

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

        Set<String> consumedHeaderKeys = new HashSet<>();
        String name = consumeKey(resume.headerInfo, consumedHeaderKeys, "name");
        String title = consumeKey(resume.headerInfo, consumedHeaderKeys, "title", "role", "profession");
        String about = consumeKey(resume.headerInfo, consumedHeaderKeys, "about", "summary", "objective", "profile");

        // Restored massive name size (32pt) with a proper 36pt line-height so it wraps
        // cleanly
        b.append("{\\fontsize{32pt}{36pt}\\selectfont\\bfseries\\uppercase{").append(name)
                .append("}}\\par\\vspace{4pt}\n");
        if (!title.isEmpty()) {
            b.append("{\\fontsize{14pt}{16pt}\\selectfont\\color{TextLight}").append(title)
                    .append("}\\par\\vspace{12pt}\n");
        } else {
            b.append("\\vspace{12pt}\n");
        }

        if (!about.isEmpty()) {
            b.append("\\cvsection{Summary}\n");
            b.append(about).append("\\par\\vspace{8pt}\n");
        }

        Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation",
                "Graduation", "Year", "TechStack", "Highlights");

        for (Section section : rightSections) {
            if (section.subSections.isEmpty()) {
                b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                continue;
            }

            boolean isFirstSub = true;
            for (SubSection sub : section.subSections) {
                b.append("\\begin{minipage}{\\linewidth}\n");

                if (isFirstSub) {
                    b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
                    isFirstSub = false;
                }

                Set<String> consumedSubKeys = new HashSet<>();
                String role = consumeKey(sub.keyValues, consumedSubKeys, "role", "degree", "position");
                String time = consumeKey(sub.keyValues, consumedSubKeys, "time", "date", "year", "grad");
                String loc = consumeKey(sub.keyValues, consumedSubKeys, "loc", "city", "state");

                // Content defaults to the document's 12pt base
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