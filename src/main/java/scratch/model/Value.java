package scratch.model;

/**
 * Une classe simple qui enveloppe un nombre entier.
 * Elle sert de base pour pouvoir manipuler des nombres ou des variables de la même façon.
 */
public class Value {
    private int value; // La valeur numérique stockée

    public Value(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
