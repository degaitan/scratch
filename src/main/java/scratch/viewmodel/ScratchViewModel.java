package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.*;
import scratch.model.Character;

import java.util.List;

public class ScratchViewModel {
    private final Program program;

    private List<Instruction> instructions = getInstru();

    // index ligne sélectionnée, -1 si rien n'est sélectionné
    // catalogIndex : index instruction selectionnée dans le catalogue
    // index : index instruction dans le programme (programList)
    private final IntegerProperty index = new SimpleIntegerProperty(-1),
            catalogIndex = new SimpleIntegerProperty(-1);

    //indice de l'instruction en exécution
    private final IntegerProperty indexExec = new SimpleIntegerProperty(-1);

    private final DoubleProperty coordX = new SimpleDoubleProperty(0);
    private final DoubleProperty coordY = new SimpleDoubleProperty(0);
    private final IntegerProperty direction = new SimpleIntegerProperty(0);

    // input provenant du TextField du detailsBox
    private final StringProperty amount = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    // état pour les boutons charger et executer
    private boolean isLoaded = false;
    private boolean isRunning = false;
    private boolean isError = false;

    // signal pour dire qu'on a fait un pas
    private Runnable onStepExecuted;

    private static final double OFFSET = 195.0;

    //boutons principaux
    private final StringProperty leftBtnText = new SimpleStringProperty("Charger");
    private final BooleanProperty leftBtnDisabled = new SimpleBooleanProperty(true);

    private final StringProperty rightBtnText = new SimpleStringProperty("Exécuter");
    private final BooleanProperty rightBtnDisabled = new SimpleBooleanProperty(true);

    public StringProperty leftBtnTextProperty() { return leftBtnText; }
    public BooleanProperty leftBtnDisabledProperty() { return leftBtnDisabled; }
    public StringProperty rightBtnTextProperty() { return rightBtnText; }
    public BooleanProperty rightBtnDisabledProperty() { return rightBtnDisabled; }

    // liste des variables à afficher sur l'écran
    private final ObservableList<Variable> variables = FXCollections.observableArrayList();

    public ObservableList<Variable> getVariables() {
        return variables;
    }

    // notifier la vue que la selection a changé
    public IntegerProperty getIndexProperty(){
        return index;
    }
    public IntegerProperty getCatalogIndexProperty(){
        return catalogIndex;
    }

    public StringProperty getAmountProperty() { return amount; }

    private final StringProperty turtleInfoText = new SimpleStringProperty("");
    public StringProperty turtleInfoTextProperty() { return turtleInfoText; }

    private final ExecutionViewModel executionVM = new ExecutionViewModel(this);
    public ExecutionViewModel getExecutionVM() { return executionVM; }
    private final Character pen;


    public ScratchViewModel(Program program) {
        this.program = program;
        this.pen = program.getPen();
        Coordinates coord = pen.getCoord();
        // synchronise avec la vraie position (195) au démarrage
        this.coordX.set(coord.getX());
        this.coordY.set(coord.getY());
        this.direction.set(pen.getOrientation());

        updateState();
        updateTurtleInfoText();

        // si X, Y ou la direction change, on update les infos
        coordX.addListener((obs, oldVal, newVal) -> updateTurtleInfoText());
        coordY.addListener((obs, oldVal, newVal) -> updateTurtleInfoText());
        direction.addListener((obs, oldVal, newVal) -> updateTurtleInfoText());
    }


    // donne la liste d'instru à gauche
    public ObservableList<Instruction> getCatalogue() {
        return Program.getCatalogue();
    }

    // donne la liste d'instru du program (programList)
    public ObservableList<Instruction> getInstru() {
        return Program.getInstructions();
    }

    public StringProperty getErrorMessage() {return errorMessage;}

    public DoubleProperty coordXProperty() { return coordX; }

    public DoubleProperty coordYProperty() { return coordY; }

    //bouton "Monter" s'active seulement si l'index est plus grand que 0
    public BooleanBinding canUp() {
        return index.greaterThan(0);
    }

    //bouton "Descendre" s'allume seulement si l'index n'est pas le dernier de la liste
    public BooleanBinding canDown() {
        // recuperer la ligne selectionnee et on verifie qu'elle se trouve dans le tableau mais pas a la derniere ligne

        return Bindings.createBooleanBinding(() -> {
            int idx = index.get();
            return idx >= 0 && idx < instructions.size() - 1;
        }, getInstru(), index);

    }

    //bouton "Tout vider" s'active' seulement s'il y a au moins une instru dans la liste
    public BooleanBinding canEmpty() {
        return Bindings.createBooleanBinding(() -> {
            List<Instruction> instr = getInstru();
            return !instr.isEmpty();
        }, getInstru());
    }

    // Le bouton "Dupliquer" s'allume si on a sélectionné une instru et si ce n'est PAS une déclaration/assignation de variable
    public BooleanBinding canDuplicate() {
        return Bindings.createBooleanBinding(() -> {
            int idx = index.get();
            if (idx > -1) {
                List<Instruction> instructions = getInstru();
                Instruction instr = instructions.get(idx);
                return instr.getType() != InstructionType.VAR_DECLARATION
                        && instr.getType() != InstructionType.VAR_ASSIGNMENT;
            }
            return false;
        }, index, getInstru());
    }

    // bouton "Supprimer" s'active si on a sélectionné quelque chose (index > -1)
    public BooleanBinding canDelete() {
        return index.greaterThan(-1);
    }



    // Bouton "Ajouter"
    public void addInstruction() {

        //on recupere l'index selectionee dans le catalogue et l'endroit ou on veut mettre dans le prog, on appelle add et on met l'instruction juste en dessous

        int idx = catalogIndex.get();

        int insertIndex = index.get();
        if (idx >= 0 && idx < getCatalogue().size()) {
            program.addInstruction(idx, insertIndex);

            notifyProgramChanged();

            index.set(insertIndex + 1);
        }
    }

    // Bouton "Monter" (Flèche Haut)
    public void up() {
        int idx = index.get();
        if (idx > 0 && idx < getInstru().size()) {
            program.up(idx);
            notifyProgramChanged();
            index.set(idx - 1); // La sélection suit le bloc qui monte
        }
    }

    // Bouton "Descendre"
    public void down() {
        int idx = index.get();
        if(idx >= 0 && idx < getInstru().size() - 1 ) {
            program.down(idx);
            notifyProgramChanged();
            index.set(idx + 1);
        }
    }

    // Bouton "Supprimer"
    public void removeInstru(){
        int idx = index.get();
        if (idx >= 0 && idx < getInstru().size()){
            program.deleteInstruction(idx);
        }
        notifyProgramChanged();
    }

    // Bouton "Dupliquer"
    public void duplicate() {
        int idx = index.get();
        if(idx >= 0 && idx < getInstru().size()){
            program.duplicate(idx);
        }
        notifyProgramChanged();
    }

    // Bouton "Tout vider"
    public void empty() {
        program.empty();
        isError = false;
        notifyProgramChanged();
    }


    //On recupere l'index de l'instru suivante, si on est pas a la fin du programme
    //pour type end_repeat : on recupere le start correspondant a chaque end, si la boucle n'est pas finie on remet l'index courant sur l'index de start (on reprend la boucle)
    //on appelle execute du programme et on met à jour les coordonnées
    // le tout englobé dans un try et catch qui récupère les erreurs d'exécution (notamment pour les actions de variables)

    public void execute() {
        int nextIndex = indexExec.get() + 1;

        if (nextIndex < instructions.size()) {
            try {
                if (instructions.get(nextIndex).getType() == InstructionType.END_REPEAT) {
                    int loopStart = program.getLoopStart(nextIndex);
                    if (!program.loopEnded(loopStart))
                        nextIndex = loopStart;
                }
                indexExec.set(nextIndex);
                index.set(nextIndex);
                Instruction instru = instructions.get(nextIndex);

                program.execute(instru);

                // Position et direction d'arrivée
                Coordinates end = pen.getCoord();
                coordX.set(end.getX());
                coordY.set(end.getY());
                direction.set(program.getPen().getOrientation());

                //on synchronise le tableau après chaque action au cas où une variable a changé
                syncVariables();

                if (onStepExecuted != null) {
                    onStepExecuted.run();
                }
            } catch (RuntimeException e) {
                 errorMessage.set(e.getMessage());
                 isRunning = false;
                 isError = true;
                 updateState();
            }
        }
    }

    public boolean isPenDown() {
        return program.isPenDown();
    }

    public void resetExecution() {
        isError = false;
        indexExec.set(-1); // remet le compteur à zéro
        index.set(-1);

        // remise à 0 des variables dans le moteur (le Program)
        program.resetVariables();

        program.reset(); // Remet la tortue au centre

        program.getVariables().clear();
        errorMessage.set("");

        // màj sur la Vue
        Coordinates coords = pen.getCoord();
        coordX.set(coords.getX());
        coordY.set(coords.getY());
        direction.set(pen.getOrientation());

        syncVariables(); // <-- On met à jour l'affichage du tableau des variables
    }

    public int getOrientation() {
        return program.getPen().getOrientation();
    }

    public void updateState() {
        if (getInstru().isEmpty()) {
            // état 1 (quand on lance le programme) : charger et executer desact
            isLoaded = false;
            isRunning = false;
            leftBtnText.set("Charger");
            leftBtnDisabled.set(true);
            rightBtnText.set("Exécuter");
            rightBtnDisabled.set(true);
        }else if(!program.checkProgramValidity()){
            // Si le code contient une erreur de logique (ex: boucle infinie, variables au mauvais endroit)
            leftBtnText.set("Charger");
            leftBtnDisabled.set(true);
            rightBtnText.set("Exécuter");
            rightBtnDisabled.set(true);
        }
        else if (!isLoaded) {
            // état 2 : charger actif et exec desact
            leftBtnText.set("Charger");
            leftBtnDisabled.set(false);
            rightBtnText.set("Exécuter");
            rightBtnDisabled.set(true);
        }
        else if (isLoaded && !isRunning) {
            // état 3 : charger devient ré-initialiser et executer s'active
            leftBtnText.set("Ré-initialiser");
            leftBtnDisabled.set(false);
            rightBtnText.set("Exécuter");
            rightBtnDisabled.set(false);
        } else if (isRunning) {
            // état 4 : gestion bouton suivant et executer
            leftBtnText.set("Ré-initialiser");
            leftBtnDisabled.set(false);
            if (isError)
                rightBtnDisabled.set(true);

            // on a tout terminé ?
            else if (indexExec.get() >= getInstru().size() - 1) {
                rightBtnText.set("Exécuter");
                rightBtnDisabled.set(true);
            } else {
                rightBtnText.set("Suivant");
                rightBtnDisabled.set(false);
            }
        }
    }

    public void clickLeftButton() {
        if (!isLoaded) {
            isLoaded = true; // qund on clic sur chargé
        } else {
            // On clic sur reset
            isRunning = false;
            executionVM.stopAuto();
            resetExecution();
        }
        updateState(); // update le visuel
    }

    public void clickRightButton() {
        if (executionVM.isAutoModeProperty().get()) {
            executionVM.playAuto(); // lance l'auto
            isRunning = true;
        } else {
            if (!isRunning) {
                isRunning = true; // bouton exec qui devient suivant
                errorMessage.set("");
            } else {
                execute(); // on avance d'un step
            }
        }
        updateState();
    }

    public void notifyProgramChanged() {
        // dès qu'on modifie dans programme, on annule et on doit recharger à nouveau
        isLoaded = false;
        isRunning = false;
        executionVM.stopAuto();
        resetExecution();
        updateState();
    }

    // L'écran demande : "C'est quoi le nom du bloc sur lequel le joueur a cliqué ?"
    public String getNameInstruParIndex() {
        int index = getIndexProperty().get(); // On regarde quelle ligne est sélectionnée
        return instructions.get(index).getName(); // On renvoie le nom (ex: "Avancer", "Tourner")
    }

    public void changeValue() {
        int index = getIndexProperty().get();
        Instruction instru = instructions.get(index);

        try {
            // recup le texte envoyé par la vue
            String val = amount.get();

            if (val == null || val.trim().isEmpty()) {
                throw new RuntimeException("Erreur valeur");
            }

            program.changeValue(instru, amount.get());
            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();

            indexExec.set(-1); // remet l'exécution à zéro
            program.reset(); // replace la tortue
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());
        } catch(RuntimeException e) {
            errorMessage.set(e.getMessage());
        }
    }

    /*
    si c'est une declaration on affiche le nom de la nouvelle variable (newVarName)
    si c'est une autre instruction, on affiche le parametre (soit nom de la variable soit un entier) sousforme de string
     */
    public String getParameterInstru() {
        int index = getIndexProperty().get();
        Instruction instru = instructions.get(index);

        if( instru.getType() == InstructionType.VAR_DECLARATION) {
            return instru.getNewVarName(); // On renvoie le nom de la variable (ex: "Score")
        }

        Value param = instru.getParameter();
        return param == null ? "" : param.toString();
    }

    public boolean isPenAction() {
        int index = getIndexProperty().get();
        Instruction instru = instructions.get(index);

        return instru.getType() == InstructionType.PEN_UP || instru.getType() == InstructionType.PEN_DOWN;
    }

    private void updateTurtleInfoText() {
        double x = coordX.get() - OFFSET;
        double y = OFFSET - coordY.get();
        int direction = getOrientation();

        // On compense pour l'affichage (-90 + 90 = 0)
        double displayDirection = -(direction + 90);

        // Petite sécurité pour garder l'angle affiché entre -180° et 180°
        if (displayDirection > 180) {
            displayDirection -= 360;
        }

        turtleInfoText.set(String.format(java.util.Locale.FRENCH, "Tortue : x = %.1f, y = %.1f, direction = %.1f °", x, y, (double) displayDirection));
    }

    public boolean executeStep() {
        if (indexExec.get() < getInstru().size() - 1) {
            execute();
            if (isError)
                return false;
            updateState();
            return true; // le programme continue
        }
        return false; // le programme est fini
    }

    public void setOnStepExecuted(Runnable onStepExecuted) {
        this.onStepExecuted = onStepExecuted;
    }

    public void changeAssignment(String input1, String input2){
        int index = getIndexProperty().get();
        if (index < 0 || index >= getInstru().size()) {
            return;
        }

        Instruction instru = getInstru().get(index);

        try {
            if (input1 == null || input1.isEmpty()) {
                throw  new RuntimeException("Nom de variable invalide");
            }
            if( input2 == null || input2.isEmpty()) {
                throw new RuntimeException("Valeur invalide");
            }

            // On donne l'ordre au moteur de changer la variable
            program.assignVariable(instru, input1, input2);

            // Comme le code a changé, on éteint les alarmes et on force le jeu à faire un Reset !
            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();

            indexExec.set(-1); // remet l'exécution à zéro
            program.reset(); // replace la tortue
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());
        } catch (RuntimeException e){
            errorMessage.set(e.getMessage()); // S'il y a une erreur, on l'affiche
        }
    }

    // Quand le joueur modifie un bloc "Ajouter [Valeur] à [Variable]"
    // (C'est exactement la même logique que la méthode juste au-dessus)
    public void changeIncrement(String varName, String stepStr){
        int index = getIndexProperty().get();
        if (index < 0 || index >= getInstru().size()) return;

        Instruction instru = getInstru().get(index);

        try {
            if (varName == null || varName.isEmpty()) throw new RuntimeException("Nom de variable invalide");
            if (stepStr == null || stepStr.isEmpty()) throw new RuntimeException("Valeur invalide");

            program.incrementVariable(instru, varName.trim(), stepStr.trim());

            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();
            indexExec.set(-1);
            program.reset();
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());
        } catch (RuntimeException e){
            errorMessage.set(e.getMessage());
        }
    }

    public String getIncrementVarName() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.INCREMENT_VARIABLE) return instru.getVarName();
        return "";
    }

    public String getIncrementStep() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.INCREMENT_VARIABLE) {
            int step = instru.getDifference();
            return step == 0 ? "" : String.valueOf(step);
        }
        return "";
    }

    // synchronise le tableau avec les variables du programme
    private void syncVariables() {
        variables.clear();
        // on récupère toutes les variables stockées dans la Map du Program
        variables.addAll(program.getVariables().values());
    }


    // Cette méthode calcule combien d'espaces on doit mettre à gauche d'un bloc.
    // L'idée : Si on est dans une boucle "Répéter", on décale le bloc vers la droite.
    public int getTabLevel(int index) {
        int level = 0; // Au début, aucun décalage (niveau 0)

        if (index < 0 || index >= instructions.size()) {
            return 0; // Si le bloc n'existe pas, pas de décalage
        }

        /*
        On regarde le nombre de boucles ouvertes avant l'instruction courante
         */
        for(int i = 0; i<index; i++){
            if(instructions.get(i).getType() == InstructionType.REPEAT){
                level++; // Si on croise un "Répéter", on ajoute un décalage (+1)
            }else if(instructions.get(i).getType() == InstructionType.END_REPEAT){
                level--; // Si on croise un "Fin Répéter", on enlève un décalage (-1)
            }
        }

        //On verifie si l'instru courante est une end_repeat, sinon le end_repeat est indente comme le reste des instructions dans le repeat
        Instruction current = instructions.get(index);
        if(current.getType() == InstructionType.END_REPEAT){
            level--; // Il doit être aligné avec le "Répéter" du début, donc on lui enlève un décalage
        }

        //si jamais le level descend en dessous de 0, on return 0 donc on prend le max entre le level et 0
        return Math.max(level, 0);

    }

    public String getAssignmentVar(){
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.VAR_ASSIGNMENT) {
            return instru.getVarName();
        }
        return "";
    }

    public String getAssignmentValue(){
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.VAR_ASSIGNMENT) {
            Value val = instru.getValue();
            return val == null ? "" : val.toString();
        }
        return "";
    }

    // Récupère la hauteur pour l'afficher dans la case
    public String getRectangleHauteur() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.DRAW_RECTANGLE) {
            Value h = instru.getHauteur();
            return h == null ? "" : h.toString();
        }
        return "";
    }

    // Récupère la largeur pour l'afficher dans la case
    public String getRectangleLargeur() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.DRAW_RECTANGLE) {
            Value l = instru.getLargeur();
            return l == null ? "" : l.toString();
        }
        return "";
    }

    public void changeRectangle(String hauteurStr, String largeurStr) {
        int index = getIndexProperty().get();
        if (index < 0 || index >= getInstru().size()) return;
        Instruction instru = getInstru().get(index);

        try {
            // "evaluate" du program transforme String -> Value (vu qu'on accepte variable et valeur)
            Value valHauteur = program.evaluate(hauteurStr);
            Value valLargeur = program.evaluate(largeurStr);

            if (valHauteur == null) throw new RuntimeException("Hauteur invalide");
            if (valLargeur == null) throw new RuntimeException("Largeur invalide");

            // Si l'utilisateur tape un vrai chiffre (pas une variable) et qu'il est < 20 : Erreur
            try {
                int h = Integer.parseInt(hauteurStr.trim());
                if (h < 20) throw new RuntimeException("La hauteur doit être >= 20");
            } catch (NumberFormatException ignored) {} // Si c'est du texte (ex: nb), on l'ignore. Ça plantera à l'exécution
            try {
                int l = Integer.parseInt(largeurStr.trim());
                if (l < 20) throw new RuntimeException("La largeur doit être >= 20");
            } catch (NumberFormatException ignored) {}

            // si tout est bon, on met à jour le modèle
            instru.setHauteur(valHauteur);
            instru.setLargeur(valLargeur);

            // On réinitialise l'état (pour forcer à relancer depuis le début)
            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();

            indexExec.set(-1);
            program.reset();
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());

        } catch (RuntimeException e) {
            errorMessage.set(e.getMessage());
        }
    }

    // Récupère la longueur du polygone pour l'afficher dans la case
    public String getPolygoneLongueur() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.DRAW_POLYGON) {
            Value h = instru.getLongueur();
            return h == null ? "" : h.toString();
        }
        return "";
    }

    // Récupère le nombre de côté du polygone pour l'afficher dans la case
    public String getPolygoneCote() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.DRAW_POLYGON) {
            Value h = instru.getCote();
            return h == null ? "" : h.toString();
        }
        return "";
    }

    public void changePolygone(String longueurStr, String coteStr) {
        int index = getIndexProperty().get();
        if (index < 0 || index >= getInstru().size()) return;
        Instruction instru = getInstru().get(index);

        try {
            // "evaluate" du program transforme String -> Value (vu qu'on accepte variable et valeur)
            Value valLongueur = program.evaluate(longueurStr);
            Value valCote = program.evaluate(coteStr);

            if (valLongueur == null) throw new RuntimeException("Longueur invalide");
            if (valCote == null) throw new RuntimeException("Nombre de côtés invalide");

            // Si l'utilisateur tape un vrai chiffre (pas une variable) et qu'il est < 3 : Erreur
            try {
                int h = Integer.parseInt(longueurStr.trim());
                if (h < 3) throw new RuntimeException("La longueur doit être >= 3");
            } catch (NumberFormatException ignored) {} // Si c'est du texte (ex: nb), on l'ignore. Ça plantera à l'exécution
            try {
                int l = Integer.parseInt(coteStr.trim());
                if (l < 3) throw new RuntimeException("Le nombre de côtés doit être >= 3");
            } catch (NumberFormatException ignored) {}

            // si tout est bon, on met à jour le modèle
            instru.setLongueur(valLongueur);
            instru.setCote(valCote);

            // On réinitialise l'état (pour forcer à relancer depuis le début)
            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();

            indexExec.set(-1);
            program.reset();
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());

        } catch (RuntimeException e) {
            errorMessage.set(e.getMessage());
        }
    }

    // méthode qui fait le pont entre le modele et la vue
    public List<double[]> getPolygonLines(double x, double y, double angle, int size, int num) {
        return Program.getPolygonLines(x, y, angle, size, num);
    }

    public void makeTeleportation(String nouvelleX, String nouvelleY) {
        int index = getIndexProperty().get();
        if (index < 0 || index >= getInstru().size()) return;
        Instruction instru = getInstru().get(index);

        try {
            // "evaluate" du program transforme String -> Value (vu qu'on accepte variable et valeur)
            Value valNouvelleX = program.evaluate(nouvelleX);
            Value valNouvelleY = program.evaluate(nouvelleY);

            if (valNouvelleX == null) throw new RuntimeException("Erreur valeur");
            if (valNouvelleY == null) throw new RuntimeException("Erreur valeur");

            // si tout est bon, on met à jour le modèle
            instru.setNouvelleX(valNouvelleX);
            instru.setNouvelleY(valNouvelleY);

            // On réinitialise l'état (pour forcer à relancer depuis le début)
            errorMessage.set("");
            isRunning = false;
            isLoaded = false;
            updateState();

            indexExec.set(-1);
            program.reset();
            Coordinates coords = pen.getCoord();
            coordX.set(coords.getX());
            coordY.set(coords.getY());

        } catch (RuntimeException e) {
            errorMessage.set(e.getMessage());
        }
    }

    public String getTPX() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.TELEPORTATION) {
            Value h = instru.getNouvelleX();
            return h == null ? "" : h.toString();
        }
        return "";
    }

    public String getTPY() {
        Instruction instru = instructions.get(getIndexProperty().get());
        if (instru.getType() == InstructionType.TELEPORTATION) {
            Value h = instru.getNouvelleY();
            return h == null ? "" : h.toString();
        }
        return "";
    }

}
