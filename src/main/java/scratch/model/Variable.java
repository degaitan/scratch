package scratch.model;

/**
 * Une Variable est une Value améliorée
 * Elle contient toujours un chiffre (hérité de Value), mais elle a en plus un "Nom".
 */
public class Variable extends Value{
    private String name;

    public Variable(String name) {
        super(0); // On donne la valeur 0 par défaut au parent (Value)
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // vérifie que le nom de la variable commence par une lettre (ou _)
        // et ne contient pas de caractères bizarres comme @, %, espace, etc.
        if(name.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))  this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
