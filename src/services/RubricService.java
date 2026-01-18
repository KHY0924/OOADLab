package services;

import java.util.Arrays;
import java.util.List;

public class RubricService {

    public List<String> loadActiveRubrics() {
        return Arrays.asList("Problem Clarity", "Methodology", "Results", "Presentation");
    }
}
