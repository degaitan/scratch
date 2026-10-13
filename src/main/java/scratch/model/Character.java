package scratch.model;

public class Character {
    private int orientation; // L'angle actuel de la tortue (en degrés)
    private Coordinates coord; // La position actuelle (utilise la classe Coordinates)

    // Variables 'final' : elles stockent la position de naissance pour le bouton Reset
    private final int initialOrientation;
    private final double initialX;
    private final double initialY;

    // Constructeur : définit la position de départ
    public Character(int initialOrientation, Coordinates origin) {
        this.orientation = initialOrientation;
        this.coord = origin;

        // On mémorise les valeurs initiales pour pouvoir y revenir plus tard
        this.initialOrientation = initialOrientation;
        this.initialX = origin.getX();
        this.initialY = origin.getY();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    // remet la tortue à sa position et son angle d'origine
    public void reset() {
        this.orientation = initialOrientation;
        this.coord.setX(initialX);
        this.coord.setY(initialY);
    }
}
