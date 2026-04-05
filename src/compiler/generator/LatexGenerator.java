package compiler.generator;

import compiler.ast.*;

public class LatexGenerator {

    public static String generate(Resume resume) {
        StringBuilder latex = new StringBuilder();

        // ================= AUTO SIZE LOGIC =================
        int totalSubSections = 0;
        for (Section s : resume.sections) {
            totalSubSections += s.subSections.size();
        }

        // ================= DOCUMENT SETUP =================
        latex.append("%-------------------------\n");
        latex.append("% Resume in Latex\n");
        latex.append("%------------------------\n\n");

        // Keep normal font sizes unless the resume is extremely empty
        String fontSize = (totalSubSections <= 3) ? "12pt" : "11pt";

        latex.append("\\documentclass[letterpaper," + fontSize + "]{article}\n\n");

        latex.append("\\usepackage{latexsym}\n");
        latex.append("\\usepackage[empty]{fullpage}\n");
        latex.append("\\usepackage{titlesec}\n");
        latex.append("\\usepackage{marvosym}\n");
        latex.append("\\usepackage[usenames,dvipsnames]{color}\n");
        latex.append("\\usepackage{verbatim}\n");
        latex.append("\\usepackage{enumitem}\n");
        latex.append("\\usepackage[pdftex,hidelinks]{hyperref}\n");
        latex.append("\\usepackage{fancyhdr}\n\n");

        latex.append("\\pagestyle{fancy}\n\\fancyhf{}\n");
        latex.append("\\renewcommand{\\headrulewidth}{0pt}\n");
        latex.append("\\renewcommand{\\footrulewidth}{0pt}\n\n");

        latex.append("\\addtolength{\\oddsidemargin}{-0.375in}\n");
        latex.append("\\addtolength{\\evensidemargin}{-0.375in}\n");
        latex.append("\\addtolength{\\textwidth}{1in}\n");
        latex.append("\\addtolength{\\topmargin}{-.5in}\n");
        latex.append("\\addtolength{\\textheight}{1.0in}\n\n");

        latex.append("\\urlstyle{same}\n\\raggedbottom\n\\raggedright\n");
        latex.append("\\setlength{\\tabcolsep}{0in}\n\n");

        latex.append("\\titleformat{\\section}{\n");
        latex.append("  \\vspace{-4pt}\\scshape\\raggedright\\large\n");
        latex.append("}{}{0em}{}[\\color{black}\\titlerule \\vspace{-5pt}]\n\n");

        // ================= CUSTOM COMMANDS (RESTORED FORMATTING) =================
        latex.append("% Custom commands\n\n");

        latex.append("\\newcommand{\\resumeItem}[2]{\n");
        latex.append("  \\item\\small{\n");
        latex.append("    \\textbf{#1}{: #2 \\vspace{-2pt}}\n"); // Restored to your original format
        latex.append("  }\n");
        latex.append("}\n\n");

        latex.append("\\newcommand{\\resumeSubheading}[4]{\n");
        latex.append("  \\vspace{-1pt}\\item\n");
        latex.append("    \\begin{tabular*}{0.97\\textwidth}{l@{\\extracolsep{\\fill}}r}\n");
        latex.append("      \\textbf{#1} & \\small #2 \\\\\n");
        latex.append("      \\textit{\\small #3} & \\textit{\\small #4} \\\\\n");
        latex.append("    \\end{tabular*}\\vspace{-5pt}\n");
        latex.append("}\n\n");

        latex.append("\\newcommand{\\resumeSubItem}[2]{\\resumeItem{#1}{#2}\\vspace{-4pt}}\n\n");

        // Set the second level bullet to a small circle (matches screenshot)
        latex.append("\\renewcommand{\\labelitemii}{$\\circ$}\n\n");

        latex.append("\\newcommand{\\resumeSubHeadingListStart}{%\n");
        latex.append("  \\begin{itemize}[leftmargin=*,label={}]}\n");
        latex.append("\\newcommand{\\resumeSubHeadingListEnd}{\\end{itemize}}\n\n");

        // Bullet point formatting - itemsep=1pt keeps it tight like the screenshot
        latex.append("\\newcommand{\\resumeItemListStart}{%\n");
        latex.append("  \\begin{itemize}[leftmargin=1.5em,itemsep=1pt,parsep=0pt,topsep=3pt]}\n");
        latex.append("\\newcommand{\\resumeItemListEnd}{\\end{itemize}\\vspace{-4pt}}\n\n");

        latex.append("\\newcommand{\\resumeItemBullet}[1]{\\item\\small{#1}}\n\n");

        latex.append("\\begin{document}\n\n");

        // ================= HEADER =================
        String name           = resume.headerInfo.getOrDefault("Name",     "");
        String email          = resume.headerInfo.getOrDefault("Email",    "");
        String headerLocation = resume.headerInfo.getOrDefault("Location", ""); 
        String github         = resume.headerInfo.getOrDefault("GitHub",   "");

        latex.append("\\begin{center}\n");
        latex.append("    {\\Huge \\textbf{" + name.toUpperCase() + "}} \\\\[4pt]\n");
        latex.append("    {\\large Student} \\\\[6pt]\n");
        latex.append("    \\small ");

        boolean firstHeader = true;
        if (!email.isEmpty()) {
            latex.append("\\href{mailto:" + email + "}{" + email + "}");
            firstHeader = false;
        }
        if (!headerLocation.isEmpty()) {
            if (!firstHeader) latex.append(" \\ \\textemdash \\ ");
            latex.append(headerLocation);
            firstHeader = false;
        }
        if (!github.isEmpty()) {
            if (!firstHeader) latex.append(" \\ \\textemdash \\ ");
            String ghDisplay = github.replaceFirst("https?://", "");
            latex.append("\\href{https://" + ghDisplay + "}{" + ghDisplay + "}");
        }
        latex.append("\n\\end{center}\n");
latex.append("\\vspace{6pt}\n\n");

        // ================= SECTIONS =================
        for (Section section : resume.sections) {
            latex.append("\\section{" + section.title + "}\n");
            latex.append("  \\resumeSubHeadingListStart\n");

            for (SubSection sub : section.subSections) {
                String loc       = sub.keyValues.getOrDefault("Location", "");
                String role      = sub.keyValues.getOrDefault("Role", "");
                String techStack = sub.keyValues.getOrDefault("TechStack", "");
                String timeline  = sub.keyValues.getOrDefault("Timeline", "");
                String grad      = sub.keyValues.getOrDefault("Graduation", "");
                String expGrad   = sub.keyValues.getOrDefault("ExpectedGraduation", "");
                String degree    = sub.keyValues.getOrDefault("Degree", "");
                String coursework = sub.keyValues.getOrDefault("Coursework", "");
                String grade     = sub.keyValues.getOrDefault("Grade", sub.keyValues.getOrDefault("GPA", ""));

                String dateStr = timeline.isEmpty() ? (expGrad.isEmpty() ? grad : expGrad) : timeline;
                String subtitle = degree;
                if (!grade.isEmpty()) subtitle += (subtitle.isEmpty() ? "" : "; ") + grade;
                if (degree.isEmpty()) {
                    if (!role.isEmpty()) subtitle = "Tech stack: " + role;
                    else if (!techStack.isEmpty()) subtitle = "Tech stack: " + techStack;
                }

                latex.append("    \\resumeSubheading\n");
                latex.append("      {" + sub.title + "}{" + loc + "}\n");
                latex.append("      {" + subtitle + "}{" + dateStr + "}\n");

                if (!sub.bullets.isEmpty() || !sub.keyValues.getOrDefault("Description", "").isEmpty() || !coursework.isEmpty()) {
                    latex.append("    \\resumeItemListStart\n");
                    if (!coursework.isEmpty()) latex.append("      \\resumeItemBullet{\\textbf{Coursework: } " + coursework + "}\n");
                    String desc = sub.keyValues.getOrDefault("Description", "");
                    if (!desc.isEmpty()) latex.append("      \\resumeItemBullet{" + desc + "}\n");
                    for (String bullet : sub.bullets) latex.append("      \\resumeItemBullet{" + bullet + "}\n");
                    latex.append("    \\resumeItemListEnd\n");
                }
            }
            latex.append("  \\resumeSubHeadingListEnd\n");
        }

        latex.append("\\end{document}\n");
        return latex.toString();
    }
}