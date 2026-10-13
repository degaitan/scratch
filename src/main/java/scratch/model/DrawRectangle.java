package scratch.model;

public class DrawRectangle extends Instruction {

    private Value hauteur;
    private Value largeur;

    public DrawRectangle(String name, InstructionType type, Value hauteur, Value largeur) {
        super(name, type, null);
        this.hauteur = hauteur;
        this.largeur = largeur;
    }

    @Override
    public Value getHauteur() {
        return hauteur;
    }

    @Override
    public void setHauteur(Value hauteur) {
        this.hauteur = hauteur;
    }

    @Override
    public Value getLargeur() {
        return largeur;
    }

    @Override
    public void setLargeur(Value largeur) {
        this.largeur = largeur;
    }

    @Override
    public Instruction newInstruction() {
        return new DrawRectangle("Rectangle", InstructionType.DRAW_RECTANGLE, new Value(20), new Value(20));
    }

    @Override
    public Instruction duplicate() {
        return new DrawRectangle(this.getName(), this.getType(), this.hauteur, this.largeur);
    }

    @Override
    public void execute(Character pen) {
        double x_start = pen.getCoord().getX();
        double y_start = pen.getCoord().getY();

        int width = this.largeur.getValue();
        int height = this.hauteur.getValue();

        if (width < 20 || height < 20) {
            throw new RuntimeException("Runtime Error");
        }
    }

    @Override
    public String toString() {
        return this.getName() + " : " + getHauteur() + ", " + getLargeur();
    }
}
