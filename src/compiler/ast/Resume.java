package compiler.ast;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Resume {
    // LinkedHashMap keeps the keys in the exact order the user typed them
    public Map<String, String> headerInfo = new LinkedHashMap<>();
    public List<Section> sections = new ArrayList<>();
}