package scratch.model;

public class Translation extends Movement{
    // La distance spécifique à la translation (en pixels)
    private int distance;

    public Translation(String name, InstructionType type, Value direction, int distance) {
        super(name, type, direction);
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    //Création d'une instruction par avec les valeurs par défaut
    @Override
    public Instruction newInstruction() {
        return new Translation("Avancer de", InstructionType.MOVE_FORWARD, new Value(30), 30);
    }

    public void setDistance(Value value) {
        int amount = value.getValue();
        this.distance = amount;

        /*if(amount>0 && amount<=100) {
            this.distance = amount;
        }else{
            throw new RuntimeException("Erreur: La distance doit être comprise entre 1 et 100.");
        }*/
    }

    @Override
    public void execute(Character pen){
        // On récupère l'orientation de la tortue
        int direction = pen.getOrientation();
        Coordinates origin = pen.getCoord();

        int currentDistance = this.getParameter().getValue();

        if(currentDistance<=0 || currentDistance>100){
            throw new RuntimeException("Erreur : " + this + "\nLa distance doit être comprise entre 1 et 100");
        }


        double x = Math.cos(Math.toRadians(direction)) * currentDistance;
        double y = Math.sin(Math.toRadians(direction)) * currentDistance;

        //On ajoute le déplacement calculé à la position actuelle de la tortue
        double x_in = origin.getX();
        double y_in = origin.getY();

        origin.setX(x_in + (int)x);
        origin.setY(y_in + (int)y);
    }


    @Override
    public String toString() {
        Value p = getParameter();
        String msg = (p == null) ? "" : p.toString();
        return super.toString() + msg;
    }


    @Override
    public Instruction duplicate() {
        return new Translation(this.getName(), this.getType(), this.getValue(), this.getDistance());

    }


    @Override
    public void setParameter(Value value) {
        super.setParameter(value);
        if (value != null) {
            setDistance(value);
        }
    }

}
