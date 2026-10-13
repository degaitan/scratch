package scratch.model;

public abstract class Instruction {
    private final String name; // Le nom affiché à l'écran (ex: "Avancé de")
    private final InstructionType type; // Le mot-clé officiel du dictionnaire (ex: MOVE_FORWARD)

    private Value value; // Le paramètre (le chiffre 30, ou la variable "ma_var")

    public Instruction(String name, InstructionType type, Value value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public InstructionType getType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    public void execute(Character character) {
    }

    @Override
    public String toString() {
        return this.getName() + " ";
    }

    public abstract Instruction newInstruction();

    public abstract Instruction duplicate();

    public void setParameter(Value value) {
        this.value = value;
    }

    public Value getParameter() {
        return this.value;
    }

    public Value getValue() {
        return value;
    }



    public boolean changePenStatus(boolean currentStatus){
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public String getNewVarName() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setNewVarName(String newVarName) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void increment(Variable var, Value diff) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public int getLoop() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void assign(Variable var, Value value){
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public String getVarName(){
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");

    }
    public int getDifference() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");

    }

    public Value getAssignedValue() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setVarName(String varName) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");

    }

    public void setAssignedValue(Value assignedValue) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setDifference(Value difference) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getHauteur() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setHauteur(Value hauteur) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getLargeur() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setLargeur(Value largeur) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getLongueur() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setLongueur(Value longueur) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getCote() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setCote(Value cote) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getNouvelleX() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setNouvelleX(Value nouvelleX) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public Value getNouvelleY() {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

    public void setNouvelleY(Value nouvelleY) {
        throw new UnsupportedOperationException("Action interdite pour ce type d'instruction");
    }

}
