package scratch.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Shape représente le dessin complet (la forme).
 */
public class Shape {
    private final Set<Line> lines = new HashSet<>();

    public void addLine(Line line) {
        lines.add(line);
    }
}
