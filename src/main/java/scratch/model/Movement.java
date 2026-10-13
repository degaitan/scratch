package scratch.model;

public abstract class Movement extends Instruction {
    private int direction;

    public Movement(String name, InstructionType type, Value value) {
        super(name, type, value);

        if(value != null) this.direction = value.getValue();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
