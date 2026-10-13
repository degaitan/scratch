package scratch.model;

public class MakeTeleportation extends Instruction {

    private Value nouvelleX;
    private Value nouvelleY;

    public MakeTeleportation(String name, InstructionType type, Value nouvelleX, Value nouvelleY) {
        super(name, type, null);

        this.nouvelleX = nouvelleX;
        this.nouvelleY = nouvelleY;
    }

    @Override
    public Value getNouvelleX() {
        return nouvelleX;
    }

    @Override
    public void setNouvelleX(Value nouvelleX) {
        this.nouvelleX = nouvelleX;
    }

    @Override
    public Value getNouvelleY() {
        return nouvelleY;
    }

    @Override
    public void setNouvelleY(Value nouvelleY) {
        this.nouvelleY = nouvelleY;
    }

    @Override
    public Instruction newInstruction() {
        return new MakeTeleportation("Téléportation", InstructionType.TELEPORTATION, new Value(0), new Value(0));
    }

    @Override
    public Instruction duplicate() {
        return new MakeTeleportation(this.getName(), this.getType(), this.nouvelleX, this.nouvelleY);
    }

    @Override
    public void execute(Character pen) {
        double x_start = pen.getCoord().getX();
        double y_start = pen.getCoord().getY();

        double nouvelleX = this.nouvelleX.getValue() + 195;
        double nouvelleY = 195 - this.nouvelleY.getValue();

        double differenceX = Math.abs(nouvelleX - x_start);
        double differenceY = Math.abs(nouvelleY - y_start);

        if (differenceX < 10 && differenceY < 10) {
            throw new RuntimeException("Runtime Error");
        }
    }

    @Override
    public String toString() {
        return this.getName() + " : " + getNouvelleX() + ", " + getNouvelleY();
    }


}
