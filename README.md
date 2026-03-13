# ResumeGenX 🚀

**A Compiler-Based Resume Generation System**

ResumeGenX is a **Domain-Specific Language (DSL) compiler** equipped with a **Java Swing GUI**. It parses a custom **Resume Description Language (`.rdl`)**, performs strict semantic validation (e.g., date logic, mandatory fields), and generates professional, **ATS-friendly LaTeX (`.tex`) source code**.

Developed as an academic project for the **B.Tech Computer Science and Engineering program at Graphic Era (Deemed to be) University**.

---

# 🌟 Key Features

### Custom Parser Engine

Built from scratch using **Recursive Descent Parsing** to process `.rdl` files.

### Semantic Analysis

Validates logical constraints such as ensuring the **End Date does not precede the Start Date**, along with other structural checks.

### Visual IDE (GUI)

A built-in **Java Swing interface** that includes:

* Split-screen code editor
* Real-time error console
* Visual template previewer

### 100% Deterministic Output

Translates validated **Abstract Syntax Trees (AST)** into flawless **LaTeX code**, bypassing the unpredictability and hallucinations of **LLM-based generators**.

### Energy Efficient

Runs locally on minimal CPU power, providing a **sustainable alternative to cloud-based generative AI solutions**.

---

# 📁 Repository Structure

```text
ResumeGenX/
├── README.md                     # Project documentation
├── src/
│   ├── gui/                      # Front-end Swing application (MainGUI, TemplateDialog)
│   └── compiler/                 # Back-end engine (lexer, parser, ast, semantic, generator)
├── assets/                       # GUI assets (template previews, icons)
└── workspace/                    # Active user directory for .rdl inputs and .tex outputs
```

---

# 📝 The `.rdl` Syntax Example

ResumeGenX uses a **clean and structured syntax** designed specifically for safe and reliable typesetting.

```text
RESUME {
    HEADER {
        name: "Ankush Malik";
        email: "ankush@example.com";
    }

    SECTION "Education" {
        ENTRY {
            title: "B.Tech Computer Science";
            date: "2023 - 2027";
        }
    }
}
```

---

# 🚀 Getting Started

## Prerequisites

Ensure the following tools are installed:

### Java Development Kit (JDK)

Version **11 or higher**

### LaTeX Engine

One of the following must be installed and added to your system **PATH**:

* MiKTeX
* TeX Live
* Tectonic

These tools are required to compile generated `.tex` files into **PDF resumes**.

---

# ⚙️ How to Build and Run

### 1️⃣ Clone the Repository

```bash
git clone <repository_url>
cd ResumeGenX
```

---

### 2️⃣ Compile the Java Source Files

```bash
javac -d bin src/gui/*.java src/compiler/**/*.java
```

---

### 3️⃣ Launch the Application

```bash
java -cp bin gui.MainGUI
```

After launching, the **ResumeGenX IDE window** will open with the code editor and console.

---

# 👥 Team: Charlie Squad

**Project Developers**

* Ankush Malik
* Vishal Vijay Singh
* Sidharth Pundir
* Akshat Bansal
