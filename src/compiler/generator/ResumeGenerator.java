package compiler.generator;
import compiler.ast.Resume;

public interface ResumeGenerator {
    String generate(Resume resume);
}