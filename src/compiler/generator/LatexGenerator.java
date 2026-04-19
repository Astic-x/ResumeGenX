package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Locale;

public class LatexGenerator {

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

    public static String generate(Resume resume) {
        StringBuilder latex = new StringBuilder();

        // CALCULATE CONTENT SCORE
        int totalSubSections = 0;
        int totalBullets = 0;
        for (Section s : resume.sections) {
            totalSubSections += s.subSections.size();
            for (SubSection sub : s.subSections) {
                totalBullets += sub.bullets.size();
            }
        }

        int contentScore = (resume.sections.size() * 10) + (totalSubSections * 8) + (totalBullets * 2);

        double pivotScore = 120.0;
        double minS = 20.0;

        double w = Math.min(1.0, (contentScore - minS) / (pivotScore - minS));

        // 3. THE SCALING MATH
        double fontSizeVal = 17.0 - (5.0 * w);
        double lineSpacingMultiplier = 1.7 - (0.5 * w);
        int lineSpacing = (int) Math.floor(fontSizeVal * lineSpacingMultiplier);

        double sectionFontSize = fontSizeVal + 3.0;
        int sectionLineSpacing = (int) Math.floor(sectionFontSize * 1.2);

        double subtitleSize = fontSizeVal + 1.5;

        double topMarginVal = 1.5 + (-1.7 * w);

        int headerGap = (int) Math.floor(25.0 + (0.0 - 25.0) * w);
        int beforeSectionGap = (int) Math.floor(45.0 + (8.0 - 45.0) * w);
        int afterSectionGap = (int) Math.floor(14.0 + (4.0 - 14.0) * w);
        int itemSep = (int) Math.floor(22.0 + (1.0 - 22.0) * w);
        int topSep = (int) Math.floor(18.0 + (2.0 - 18.0) * w);

        String topMargin = String.format(Locale.US, "%.2fin", topMarginVal);
        String headerSpacing = String.format(Locale.US, "\\vspace{%dpt}", headerGap);
        String itemSpacing = String.format(Locale.US, "itemsep=%dpt,parsep=%dpt,topsep=%dpt", itemSep,
                (int) (itemSep / 2), topSep);

        // PREAMBLE
        latex.append("% ResumeGenX - Pivot-Based Elastic Scaling\n");
        latex.append("% Score: ").append(contentScore).append(" | Weight: ").append(String.format(Locale.US, "%.2f", w))
                .append("\n");
        latex.append("% Dynamic Font: ").append(String.format(Locale.US, "%.1f", fontSizeVal)).append("pt\n");

        latex.append("\\documentclass[letterpaper,10pt]{article}\n\n");

        latex.append("\\usepackage[empty]{fullpage}\n");
        latex.append("\\usepackage{titlesec}\n");
        latex.append("\\usepackage{marvosym}\n");
        latex.append("\\usepackage[usenames,dvipsnames]{color}\n");
        latex.append("\\usepackage{enumitem}\n");
        latex.append("\\usepackage[pdftex,hidelinks]{hyperref}\n");
        latex.append("\\usepackage{fancyhdr}\n");
        latex.append("\\usepackage{anyfontsize}\n\n");

        latex.append("\\addtolength{\\topmargin}{").append(topMargin).append("}\n");
        latex.append("\\addtolength{\\textheight}{1.8in}\n");
        latex.append("\\addtolength{\\textwidth}{1.0in}\n");
        latex.append("\\addtolength{\\oddsidemargin}{-0.5in}\n");
        latex.append("\\addtolength{\\evensidemargin}{-0.5in}\n\n");

        latex.append("\\urlstyle{same}\n\\raggedbottom\n\\raggedright\n\n");

        latex.append("\\titleformat{\\section}{\n");
        latex.append("  \\scshape\\raggedright\\color{black}");
        latex.append("\\fontsize{").append(String.format(Locale.US, "%.1f", sectionFontSize))
                .append("}{").append(sectionLineSpacing).append("}\\selectfont\n");
        latex.append("}{}{0em}{}[\\titlerule]\n\n");

        latex.append("\\titlespacing*{\\section}{0pt}{")
                .append(beforeSectionGap).append("pt}{")
                .append(afterSectionGap).append("pt}\n\n");

        latex.append("\\newcommand{\\resumeSubHeadingListStart}{\\begin{itemize}[leftmargin=*,label={}]}\n");
        latex.append("\\newcommand{\\resumeSubHeadingListEnd}{\\end{itemize}}\n");
        latex.append("\\newcommand{\\resumeItemListStart}{\\begin{itemize}[leftmargin=1.2em,").append(itemSpacing)
                .append("]}\n");
        latex.append("\\newcommand{\\resumeItemListEnd}{\\end{itemize}\\vspace{-5pt}}\n\n");

        latex.append("\\begin{document}\n\n");

        latex.append("\\fontsize{").append(String.format(Locale.US, "%.1f", fontSizeVal))
                .append("}{").append(lineSpacing).append("}\\selectfont\n\n");

        // HEADER
        String name = escapeLatex(resume.headerInfo.getOrDefault("Name", ""));
        String email = escapeLatex(resume.headerInfo.getOrDefault("Email", ""));
        String github = escapeLatex(resume.headerInfo.getOrDefault("GitHub", "").replaceFirst("https?://", ""));

        latex.append("\\begin{center}\n");
        double nameSize = fontSizeVal + 8.0;
        latex.append("    {\\fontsize{").append(String.format(Locale.US, "%.1f", nameSize))
                .append("}{").append(nameSize + 2).append("}\\selectfont \\textbf{").append(name.toUpperCase())
                .append("}} \\\\[4pt]\n");

        latex.append("    {\\fontsize{").append(String.format(Locale.US, "%.1f", subtitleSize))
                .append("}{").append(subtitleSize + 2).append("}\\selectfont Student} \\\\[6pt]\n");

        latex.append("    \\small ").append(email);
        if (!github.isEmpty())
            latex.append(" \\ \\textemdash \\ ").append(github);
        latex.append("\n\\end{center}\n");
        latex.append(headerSpacing).append("\n\n");

        // SECTIONS
        Set<String> ignore = Set.of("Location", "Role", "Timeline", "Degree", "ExpectedGraduation", "Graduation",
                "TechStack");

        for (Section section : resume.sections) {
            latex.append("\\section{").append(escapeLatex(section.title)).append("}\n");
            latex.append("  \\resumeSubHeadingListStart\n");

            for (SubSection sub : section.subSections) {
                String role = escapeLatex(sub.keyValues.getOrDefault("Role", sub.keyValues.getOrDefault("Degree", "")));
                String time = escapeLatex(
                        sub.keyValues.getOrDefault("Timeline", sub.keyValues.getOrDefault("ExpectedGraduation", "")));
                String tech = escapeLatex(sub.keyValues.getOrDefault("TechStack", ""));

                String subtitle = role;
                if (!tech.isEmpty() && role.isEmpty())
                    subtitle = "Tech: " + tech;

                latex.append("    \\item\n");
                latex.append("    \\begin{tabular*}{0.97\\textwidth}{l@{\\extracolsep{\\fill}}r}\n");
                latex.append("      \\textbf{").append(escapeLatex(sub.title)).append("} & ").append(time)
                        .append(" \\\\\n");
                latex.append("      \\textit{\\small ").append(subtitle).append("} & \\textit{\\small ")
                        .append(escapeLatex(sub.keyValues.getOrDefault("Location", ""))).append("} \\\\\n");
                latex.append("    \\end{tabular*}\\vspace{-5pt}\n");

                List<String> customKeys = new ArrayList<>();
                for (String key : sub.keyValues.keySet()) {
                    if (ignore.stream().noneMatch(key::equalsIgnoreCase) && !key.equalsIgnoreCase("Highlights")) {
                        customKeys.add(key);
                    }
                }

                if (!sub.bullets.isEmpty() || !customKeys.isEmpty()) {
                    latex.append("    \\resumeItemListStart\n");
                    for (String key : customKeys) {
                        latex.append("      \\item\\small{\\textbf{").append(escapeLatex(key)).append("}: ")
                                .append(escapeLatex(sub.keyValues.get(key))).append("}\n");
                    }
                    for (String bullet : sub.bullets) {
                        String clean = bullet.contains(":") ? bullet.substring(bullet.indexOf(":") + 1).trim() : bullet;
                        latex.append("      \\item\\small{").append(escapeLatex(clean)).append("}\n");
                    }
                    latex.append("    \\resumeItemListEnd\n");
                }
            }
            latex.append("  \\resumeSubHeadingListEnd\n");
        }

        latex.append("\\end{document}\n");
        return latex.toString();
    }
}