package compiler.generator;

import compiler.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AltaCVGenerator implements ResumeGenerator {

    
    // Helpers
    private static String escapeLatex(String text) {
        if (text == null) return "";
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
            if (key.equalsIgnoreCase(target)) return escapeLatex(resume.headerInfo.get(key));
        }
        return "";
    }

    private static boolean sectionIs(String title, String... keywords) {
        String t = title.toLowerCase();
        for (String k : keywords) {
            if (t.contains(k.toLowerCase())) return true;
        }
        return false;
    }
    // Entry point
    @Override
    public String generate(Resume resume) {
        StringBuilder latex = new StringBuilder();

        // Sort sections into Left vs Right column
        List<Section> leftSections = new ArrayList<>();
        List<Section> rightSections = new ArrayList<>();
        
        for (Section s : resume.sections) {
            if (sectionIs(s.title, "skill", "language", "interest", "about", "personal")) {
                leftSections.add(s);
            } else {
                rightSections.add(s);
            }
        }

        buildPreamble(latex);
        buildTopHeader(latex, resume);
        
        latex.append("\\columnratio{0.33}\n");
        latex.append("\\setlength{\\columnsep}{24pt}\n");
        latex.append("\\begin{paracol}{2}\n\n");
        
        buildLeftColumn(latex, resume, leftSections);
        latex.append("\n\\switchcolumn\n\n");
        buildRightColumn(latex, rightSections);

        latex.append("\\end{paracol}\n");
        latex.append("\\end{document}\n");
        
        return latex.toString();
    }
    // Preamble


    private void buildPreamble(StringBuilder b) {
        b.append("\\documentclass[a4paper, 10pt]{article}\n\n");

        b.append("\\usepackage[top=0in, bottom=0.5in, left=0.4in, right=0.5in]{geometry}\n");
        b.append("\\usepackage[T1]{fontenc}\n");
        b.append("\\usepackage[utf8]{inputenc}\n");
        b.append("\\usepackage{helvet}\n");
        b.append("\\renewcommand{\\familydefault}{\\sfdefault}\n");
        b.append("\\usepackage{xcolor}\n");
        b.append("\\usepackage{tikz}\n");
        b.append("\\usepackage{paracol}\n");
        b.append("\\usepackage{enumitem}\n");
        b.append("\\usepackage{graphicx}\n");
        b.append("\\usepackage[hidelinks]{hyperref}\n\n");

        // AltaCV Colors
        b.append("\\definecolor{HeaderDark}{HTML}{454545}\n");
        b.append("\\definecolor{SidebarGray}{HTML}{E8EDF2}\n");
        b.append("\\definecolor{AccentCyan}{HTML}{4DB6AC}\n");
        b.append("\\definecolor{TextDark}{HTML}{333333}\n");
        b.append("\\definecolor{TextLight}{HTML}{666666}\n\n");

        b.append("\\pagestyle{empty}\n");
        b.append("\\setlength{\\parindent}{0pt}\n\n");

        // Background color for the left column via paracol
        b.append("\\backgroundcolor{c}[0]{SidebarGray}\n\n");

        // Right Column Section Headers
        b.append("\\newcommand{\\cvsection}[1]{%\n");
        b.append("  \\vspace{16pt}\n");
        b.append("  {\\Large\\bfseries\\color{TextDark}\\uppercase{#1}}\\par\n");
        b.append("  \\vspace{2pt}{\\color{TextLight}\\hrulefill}\\par\\vspace{8pt}\n");
        b.append("}\n\n");

        // Left Column Section Headers (Cyan Box)
        b.append("\\newcommand{\\sidebarsection}[1]{%\n");
        b.append("  \\vspace{12pt}\n");
        b.append("  \\colorbox{AccentCyan}{\\makebox[\\linewidth][c]{\\bfseries\\color{white}#1}}\\par\\vspace{6pt}\n");
        b.append("}\n\n");
        
        b.append("\\begin{document}\n\n");
    }
    // Top Full-Bleed Header
    private void buildTopHeader(StringBuilder b, Resume resume) {
        String name = getHeaderVal(resume, "Name");
        String title = getHeaderVal(resume, "Title");
        if (title.isEmpty()) title = "Professional";

        b.append("\\begin{tikzpicture}[remember picture,overlay]\n");
        b.append("  \\fill[HeaderDark] (current page.north west) rectangle ([yshift=-1.4in]current page.north east);\n");
        b.append("\\end{tikzpicture}\n\n");
        
        b.append("\\vspace*{0.3in}\n");
        b.append("\\begin{center}\n");
        b.append("  {\\fontsize{40pt}{48pt}\\selectfont\\color{white}\\textbf{").append(name).append("}}\\\\[8pt]\n");
        b.append("  {\\Large\\color{white} ").append(title).append("}\n");
        b.append("\\end{center}\n");
        b.append("\\vspace*{0.5in}\n\n"); // Push content below the drawn header
    }
    // Left Sidebar Column
    private void buildLeftColumn(StringBuilder b, Resume resume, List<Section> leftSections) {
        String image = getHeaderVal(resume, "IMAGE");
        
        if (!image.isEmpty()) {
            b.append("\\begin{center}\n");
            b.append("  \\begin{tikzpicture}\n");
            b.append("    \\clip (0,0) circle (1.8cm);\n");
            b.append("    \\node at (0,0) {\\includegraphics[width=3.6cm]{").append(image).append("}};\n");
            b.append("  \\end{tikzpicture}\n");
            b.append("\\end{center}\n\\vspace{12pt}\n\n");
        }

        b.append("\\centering\n"); // Center all text in sidebar

        // Contact Info
        String email = getHeaderVal(resume, "Email");
        String github = getHeaderVal(resume, "GitHub").replaceFirst("https?://", "");
        String location = getHeaderVal(resume, "Location");

        if (!email.isEmpty() || !github.isEmpty() || !location.isEmpty()) {
            b.append("\\sidebarsection{Contact}\n");
            b.append("\\small\\color{TextLight}\n");
            if (!email.isEmpty()) b.append(email).append("\\\\[4pt]\n");
            if (!github.isEmpty()) b.append(github).append("\\\\[4pt]\n");
            if (!location.isEmpty()) b.append(location).append("\\\\[4pt]\n");
        }

        String about = getHeaderVal(resume, "About");
        if (!about.isEmpty()) {
            b.append("\\sidebarsection{About Me}\n");
            b.append("\\small\\color{TextLight} ").append(about).append("\\par\n");
        }

        for (Section section : leftSections) {
            b.append("\\sidebarsection{").append(escapeLatex(section.title)).append("}\n");
            b.append("\\small\\color{TextLight}\n");
            
            for (SubSection sub : section.subSections) {
                if (sub.title != null && !sub.title.isEmpty()) {
                    b.append("\\textbf{\\color{TextDark}").append(escapeLatex(sub.title)).append("}\\\\[2pt]\n");
                }
                for (String key : sub.keyValues.keySet()) {
                    b.append(escapeLatex(key)).append(": ").append(escapeLatex(sub.keyValues.get(key))).append("\\\\[2pt]\n");
                }
                for (String bullet : sub.bullets) {
                    String clean = bullet.startsWith("-") ? bullet.substring(1).trim() : bullet;
                    b.append(escapeLatex(clean)).append("\\\\[2pt]\n");
                }
                b.append("\\vspace{4pt}\n");
            }
        }
    }

    // Right Main Column

    private void buildRightColumn(StringBuilder b, List<Section> rightSections) {
        b.append("\\raggedright\n");

        for (Section section : rightSections) {
            b.append("\\cvsection{").append(escapeLatex(section.title)).append("}\n");
            
            for (SubSection sub : section.subSections) {
                String role = escapeLatex(sub.keyValues.getOrDefault("Role", sub.keyValues.getOrDefault("Degree", "")));
                String time = escapeLatex(sub.keyValues.getOrDefault("Timeline", sub.keyValues.getOrDefault("ExpectedGraduation", "")));
                String loc = escapeLatex(sub.keyValues.getOrDefault("Location", ""));
                // Date in left margin, Title/Org in main body
                b.append("\\begin{tabularx}{\\linewidth}{@{}p{0.18\\linewidth} X@{}}\n");
                b.append("  \\small\\color{TextLight}").append(time).append(" &\n");
                b.append("  \\textbf{\\color{TextDark}").append(escapeLatex(sub.title)).append("}\n");
                
                if (!role.isEmpty() || !loc.isEmpty()) {
                    b.append("  \\\\[2pt] & {\\small\\color{TextLight} ");
                    if (!role.isEmpty()) b.append(role);
                    if (!role.isEmpty() && !loc.isEmpty()) b.append(" \\textbar\\ ");
                    if (!loc.isEmpty()) b.append(loc);
                    b.append("}\n");
                }
                b.append("\\end{tabularx}\\par\\vspace{4pt}\n");
                // Descriptions
                Set<String> ignore = Set.of("Role", "Timeline", "StartDate", "Location", "Degree", "ExpectedGraduation", "Graduation", "TechStack", "Highlights");
                boolean hasDesc = false;
                for (String key : sub.keyValues.keySet()) {
                    if (!ignore.contains(key)) {
                        if (!hasDesc) {
                            b.append("\\hspace{0.2\\linewidth}\\begin{minipage}{0.78\\linewidth}\\small\\color{TextLight}\n");
                            hasDesc = true;
                        }
                        b.append(escapeLatex(sub.keyValues.get(key))).append("\\par\\vspace{2pt}\n");
                    }
                }
                
                if (!sub.bullets.isEmpty()) {
                    if (!hasDesc) {
                        b.append("\\hspace{0.2\\linewidth}\\begin{minipage}{0.78\\linewidth}\\small\\color{TextLight}\n");
                        hasDesc = true;
                    }
                    b.append("\\begin{itemize}[leftmargin=1.2em, topsep=2pt, itemsep=2pt]\n");
                    for (String bullet : sub.bullets) {
                        String clean = bullet.toLowerCase().startsWith("highlights:") ? bullet.substring(11).trim() : bullet;
                        if (clean.startsWith("-")) clean = clean.substring(1).trim();
                        b.append("  \\item ").append(escapeLatex(clean)).append("\n");
                    }
                    b.append("\\end{itemize}\n");
                }

                if (hasDesc) {
                    b.append("\\end{minipage}\\par\\vspace{10pt}\n");
                } else {
                    b.append("\\vspace{6pt}\n");
                }
            }
        }
    }
}