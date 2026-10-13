package scratch.model;

public class Repeat extends Instruction {
    private int loop;  //Compteur de la boucle

    public Repeat(String name, InstructionType type, Value value) {
        super(name, type, value);
        loop = this.getValue().getValue();
    }

    @Override
    public void execute(Character character) {
        if (loop < 0)
            throw new RuntimeException("Erreur : " + this + "\nLe nombre de répétitions doit être positif");

        if (this.getType() == InstructionType.REPEAT) {
            // Reset du compteur
            if (loop == 0)
                loop = getValue().getValue();

            // On décrémente le compteur
            --loop;
        }
    }

    @Override
    public int getLoop() {
        return this.loop;
    }

    @Override
    public void setParameter(Value value) {
        super.setParameter(value);
        loop = this.getValue().getValue();
    }


    @Override
    public Instruction newInstruction() {
        // Par défaut, un bloc "Répéter" est configuré sur 4
        Value def = new Value(4);

        // MAIS si c'est le bloc "Fin Répéter", on le met à 0 car il n'a pas de paramètre visible
        if (this.getType() == InstructionType.END_REPEAT)
            def.setValue(0);

        return new Repeat(this.getName(), this.getType(), def);
    }

    @Override
    public String getNewVarName() {
        return super.getNewVarName();
    }

    @Override
    public Instruction duplicate() {
        return new Repeat(this.getName(), this.getType(), this.getValue());
    }


    @Override
    public String toString() {
        if (this.getType() == InstructionType.REPEAT)
            return this.getName() + (this.getValue().getValue() == 0 ? (this.getParameter() == null ?
                "" : " " + this.getParameter()) :
                " " + this.getValue() ) + " fois";
        else
            return this.getName();
    }
}
