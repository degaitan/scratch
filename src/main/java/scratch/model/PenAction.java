package scratch.model;

/**
 * Gère le stylo : Lever le stylo (PEN_UP) ou Abaisser le stylo (PEN_DOWN)
 */
public class PenAction extends Instruction {

    public PenAction(String name, InstructionType type) {
        super(name, type, null);
    }

    @Override
    public Instruction newInstruction() {
        return new PenAction(this.getName(), this.getType());
    }

    /**
     * Cette méthode calcule si le stylo doit toucher la feuille ou non
     */

    @Override
    public boolean changePenStatus(boolean status) {
        // Si le stylo est DEJA en bas (status = true) ET qu'on dit "Baisse le stylo" (PEN_DOWN)
        // OU si le stylo est DEJA en haut (status = false) ET qu'on dit "Lève le stylo" (PEN_UP)
        if (status && this.getType() == InstructionType.PEN_DOWN || !status && this.getType() == InstructionType.PEN_UP)
            return status; // On ne change rien, on garde le statut actuel

        // Dans tous les autres cas (ex: il était en haut et on dit "Baisse"), on inverse le statut
        return !status;
    }

    @Override
    public Instruction duplicate() {
        return new PenAction(this.getName(), this.getType());
    }
}
