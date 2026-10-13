package scratch.model;

public class Rotation extends Movement {


    public Rotation(String name, InstructionType type, Value direction) {
        super(name, type, direction);
    }


    public void setDirection(Value value) {
        int newDirection = value.getValue();
        // Règle imposée : on ne peut pas tourner de moins de 1 ou de plus de 180 d'un coup
        if (newDirection < 1 || newDirection > 180)
            throw new RuntimeException("Erreur: L'angle doit être compris entre 1 et 180 degrés.");

        super.setDirection(newDirection);
    }


    @Override
    public void execute(Character pen) {
        // On regarde vers où la tortue regarde actuellement
        int initial_angle = pen.getOrientation();

        // On récupère la valeur de la rotation
        int modif = this.getParameter().getValue();

        if(modif<1 || modif>180){
            throw new RuntimeException("Erreur: " + this + "\nL'angle doit être compris entre 1 et 180 degrés.");
        }

        // Dans notre projet, si le type est TURN_LEFT, on soustrait l'angle (on tourne vers les négatifs)
        if (this.getType() == InstructionType.TURN_LEFT)
            modif = -modif;


        int newAngle = initial_angle + modif;
        pen.setOrientation(newAngle);


    }


    @Override
    public Instruction newInstruction() {
        String name = "Tourner à " + (this.getType() == InstructionType.TURN_LEFT ? "gauche de" : "droite de");
        return new Rotation(name, this.getType(), new Value(90));

    }

    @Override
    public String toString() {
        Value p = getParameter();
        String msg = (p == null) ? "" : p.toString();
        return super.toString() + msg;
    }


    @Override
    public Instruction duplicate() {
        return new Rotation(this.getName(), this.getType(), this.getValue());
    }


    public void setParameter(Value value) {
        super.setParameter(value);
        if (value != null) {
            setDirection(value.getValue());
        }
    }
}
