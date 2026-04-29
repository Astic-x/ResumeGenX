
<div align="center">

# 🚀 ResumeGenX

**A Full-Stack Compiler-Based Resume Generation Platform**

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![LaTeX](https://img.shields.io/badge/LaTeX-008080?style=for-the-badge&logo=latex&logoColor=white)](https://www.latex-project.org/)

*ResumeGenX is a custom Domain-Specific Language (DSL) compiler equipped with a sleek local Web UI. It parses a custom Resume Definition Language (`.rdl`), performs strict semantic validation, and generates professional, ATS-friendly LaTeX source code and PDF binaries.*

*Developed as an academic project for the B.Tech Computer Science & Engineering program at Graphic Era (Deemed to be) University.*

</div>

---

## 🌟 Key Features

*   **Custom Compiler Pipeline:** Built entirely from scratch in Java, featuring a custom `Lexer`, `Parser`, and `SemanticAnalyzer` to process `.rdl` syntax.
*   **Intelligent Semantic Analysis:** Catches logical gaps (e.g., missing mandatory fields, unusually formatted dates, or brief descriptions) and feeds them directly to the UI's live terminal console.
*   **Dynamic Data Engine:** Utilizes a generic dynamic key parsing algorithm, allowing users to invent any custom data fields without breaking the layout.
*   **Modern Web IDE:** A beautiful, dark-themed frontend running on `localhost:8080` that includes:
    *   A template selection gallery.
    *   An animated, real-time developer terminal for compiler warnings/errors.
    *   Instant Base64 PDF streaming and LaTeX export.
*   **Five Professional Layouts:** Hard-coded generators applying strict "Golden Rules" (unbreakable `minipage` blocks, 12pt/14pt typography). Layouts include: *Clean Minimal, Modern Split, Academic Pro, Centered Elegant, and Creative Bold*.
*   **100% Deterministic Output:** Translates validated Abstract Syntax Trees (AST) into flawless LaTeX, bypassing the hallucinations of LLM-based generators.
*   **Privacy-First & Energy Efficient:** Runs entirely locally on minimal CPU power, providing a sustainable and secure alternative to cloud-based AI tools.

---

## 📝 The `.rdl` Syntax

ResumeGenX uses a clean, intuitive, and line-based syntax designed for rapid data entry and safe typesetting. 
```text
// 1. Forgiving Assignments: Notice how = and : are mixed freely
Name = Jordan Overachiever
Email : jordan.o@example.com
Location = San Francisco, CA
GitHub : github.com/jordan_o

// 2. Structural Hierarchy: Sections containing SubSections
Section: Education
    SubSection = Tech Institute of California
        Degree : B.S. Software Engineering
        ExpectedGraduation = 2025
        Coursework = Distributed Systems, Cloud Computing, Advanced Algorithms
    
    SubSection = Horizon High School
        Degree = High School Diploma
        Graduation : 2021

Section: Experience
    SubSection: MegaCorp
        Role = Backend Engineering Intern
        StartDate : June 1, 2024
        // 3. Multi-line String: The indentation on the second line tells the 
        // Lexer to stitch this together into one continuous sentence.
        Description = Engineered a scalable microservice architecture using
        Spring Boot and Docker.
            Collaborated closely with cross-functional teams to deploy features handling millions of daily requests.
```

---

## 📁 Repository Structure
```text
ResumeGenX/
├── README.md                     # Project documentation
├── frontend/                     # Frontend web application
│   ├── index.html                # Code upload and editor view
│   ├── templates.html            # Template gallery and generation UI
│   └── style.css                 # Dark-theme UI styling
├── src/                          
│   └── compiler/                 # Back-end Java engine 
│       ├── lexer/                # Tokenization logic
│       ├── parser/               # AST generation
│       ├── semantic/             # Semantic analysis and validation
│       ├── generator/            # LaTeX template factories
│       └── ast/                  # Abstract Syntax Tree data models
└── Server.java                   # HTTP Server handling CORS, JSON & pdflatex
```

---

## 🚀 Getting Started

### Prerequisites

Ensure the following tools are installed on your machine:

1.  **Java Development Kit (JDK):** Version 11 or higher.
2.  **LaTeX Engine:** Required to compile the generated `.tex` files into binary PDFs. 
    *   [MiKTeX](https://miktex.org/) (Windows) — *Crucial: Set "Install missing packages on-the-fly" to **Yes** during installation.*
    *   [MacTeX](https://tug.org/mactex/) (macOS)
    *   TeX Live (Linux via `sudo apt install texlive-full`)
3.  **Environment Variables:** Ensure the `bin` folder of your LaTeX distribution (e.g., `...\miktex\bin\x64`) is added to your system's `PATH`.

---

## ⚙️ How to Build and Run

**1. Clone the Repository**
```bash
git clone <repository_url>
cd ResumeGenX
```

**2. Compile the Java Source Files**
```bash
javac -d bin src/compiler/**/*.java Server.java
```

**3. Launch the Application Server**
```bash
java -cp bin Server
```

**4. Access the Web IDE**
Open your preferred web browser (e.g., Brave) and navigate to:
👉 **`http://localhost:8080`**

---

## 👥 Team: Charlie Squad

**Project Developers**

<table>
  <tr>
    <td align="center"><a href="https://github.com/Astic-x"><img src="https://github.com/Astic-x.png" width="100px;" alt=""/><br /><sub><b>Ankush Malik</b></sub></a></td>
    <td align="center"><a href="https://github.com/vishalsingh21xyz"><img src="https://github.com/vishalsingh21xyz.png" width="100px;" alt=""/><br /><sub><b>Vishal Vijay Singh</b></sub></a></td>
    <td align="center"><a href="https://github.com/SIDHARTH-PUNDIR"><img src="https://github.com/SIDHARTH-PUNDIR.png" width="100px;" alt=""/><br /><sub><b>Sidharth Pundir</b></sub></a></td>
    <td align="center"><a href="https://github.com/akshatbansal13"><img src="https://github.com/akshatbansal13.png" width="100px;" alt=""/><br /><sub><b>Akshat Bansal</b></sub></a></td>
  </tr>
</table>