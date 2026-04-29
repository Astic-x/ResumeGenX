package compiler.ast;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Resume {
    // Preserves the order of user-defined keys
    public Map<String, String> headerInfo = new LinkedHashMap<>();
    public List<Section> sections = new ArrayList<>();
}