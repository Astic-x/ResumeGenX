package compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class Section {
    public String title;
    public List<SubSection> subSections = new ArrayList<>();

    public Section(String title) {
        this.title = title;
    }
}