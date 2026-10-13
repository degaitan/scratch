package scratch.model;

/**
 * Gère deux choses :
 * 1 : Mettre une valeur précise dans la variable (VAR_ASSIGNMENT) -> ex: "score = 10"
 * 2 : Ajouter une valeur à la variable (INCREMENT_VARIABLE) -> ex: "score de 1" (pour faire +1)
 */
public class VarAssignment extends VarAction {
    private String varName = "";
    private Value difference = new Value(0); // Pour l'incrémentation (+X)
    private Value varValue;

    private Value assignedValue = new Value(0); // Pour l'assignation (=X)

    public VarAssignment(String name, InstructionType type) {
        super(name, type);
    }

    public VarAssignment(String name, InstructionType type, Value val) {
        super(name, type);
        this.setParameter(val);
    }

    public Instruction newInstruction(){
        return new VarAssignment(this.getName(), this.getType());
    }

    @Override
    public Instruction duplicate() {
        if (this.getType() == InstructionType.INCREMENT_VARIABLE)
            return new VarAssignment(this.getName(), this.getType(), this.getParameter());
        return this;
    }


    @Override
    public void assign(Variable var, Value value){
        // on stocke la valeur dans l'instruction pour le moment de l'exécution
        this.setVarName(var.getName());
        this.assignedValue = value;
    }

    @Override
    public void increment(Variable var, Value diff) {
        this.setVarName(var.getName());
        this.setDifference(diff);
    }

    @Override
    public String getVarName() {
        return varName;
    }

    @Override
    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public int getDifference() {
       return difference.getValue();
    }

    @Override
    public void setDifference(Value difference) {
        this.difference = difference;
    }

    @Override
    public Value getAssignedValue() { return assignedValue; }

    @Override
    public void setAssignedValue(Value assignedValue) {
        this.assignedValue = assignedValue;
    }


    @Override
    public String toString() {
        /*return this.getName() + " : " + this.getVarName();*//*(this.getVariable() == null ? "" : this.getVariable().getName()
                + (this.getType() == InstructionType.VAR_ASSIGNMENT ? " = " + this.getVariable().getValue()
                : " de " + this.getDifference()));*/

        String instruName = this.getName();
        String msg = "";

        if(this.getType() == InstructionType.VAR_ASSIGNMENT){
            msg = " : " + this.getVarName() + " = " + this.assignedValue;
        }
        else if (this.getType() == InstructionType.INCREMENT_VARIABLE){
            if(difference == null){
                msg = " : " + this.getVarName() + " de ";
            }else {
                msg = " : " + this.getVarName() + " de " + this.difference;
            }
        }
        return instruName + " " + msg;
    }
}
