package compiler.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SubSection {
    public String title;
    public Map<String, String> keyValues = new LinkedHashMap<>();
    public List<String> bullets = new ArrayList<>();

    public SubSection(String title) {
        this.title = title;
    }
}