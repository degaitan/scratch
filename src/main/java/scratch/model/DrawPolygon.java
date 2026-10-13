package scratch.model;

public class DrawPolygon extends Instruction {

    private Value longueur;
    private Value cote;

    public DrawPolygon(String name, InstructionType type, Value longueur, Value cote) {
        super(name, type, null);

        this.longueur = longueur;
        this.cote = cote;
    }

    @Override
    public Value getLongueur() {
        return longueur;
    }

    @Override
    public void setLongueur(Value longueur) {
        this.longueur = longueur;
    }

    @Override
    public Value getCote() {
        return cote;
    }

    @Override
    public void setCote(Value cote) {
        this.cote = cote;
    }

    @Override
    public Instruction newInstruction() {
        return new DrawPolygon("Polygone", InstructionType.DRAW_POLYGON, new Value(20), new Value(6));
    }

    @Override
    public Instruction duplicate() {
        return new DrawPolygon(this.getName(), this.getType(), this.longueur, this.cote);
    }

    @Override
    public void execute(Character pen) {
        double x_start = pen.getCoord().getX();
        double y_start = pen.getCoord().getY();

        int longueur = this.longueur.getValue();
        int cote = this.cote.getValue();

        if (longueur < 3 || cote < 3) {
            throw new RuntimeException("Runtime Error");
        }
    }

    @Override
    public String toString() {
        return this.getName() + " : " + getLongueur() + ", " + getCote();
    }
}
