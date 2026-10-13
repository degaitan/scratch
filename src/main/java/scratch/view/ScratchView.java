package scratch.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.Instruction;
import scratch.model.InstructionType;
import scratch.viewmodel.ScratchViewModel;

import java.util.ArrayList;
import java.util.List;


// BorderPane car : Gauche (Palette), Centre (Programme), Droite (Scène/Canvas).

public class ScratchView extends BorderPane {

    private ScratchViewModel scratchViewModel;

    // conteneurs principaux
    private final VBox palette = new VBox(8); // Colonne de gauche (catalogues)
    private final VBox program = new VBox(8); // Colonne centrale (instructions)
    private final VBox scene = new VBox(20); // Colonne de droite (dessin et exécution)

    private final Group headTurtle = new Group(); // Groupe représentant la tortue

    // titres sections
    private final Label paletteTitle = new Label("Palette d'actions");
    private final Label programTitle = new Label("Programme");
    private final Label sceneTitle = new Label("Scène");

    // case CheckBox
    private final CheckBox advancedModeCheck = new CheckBox("Mode avancé");

    private final CheckBox tpModeCheck = new CheckBox("afficher la téléportation");

    // listes et boutons
    private final HBox buttonProgram = new HBox(8); // Conteneur pour les boutons du programme

    private final ListView<Instruction> paletteList = new ListView<>(); // Liste des actions possibles
    private final ListView<Instruction> programList = new ListView<>(); // Liste des actions du script

    private final Canvas canvas = new Canvas(400, 400); // Zone de dessin

    private final Button addToProgramButton = new Button("Ajouter au programme");

    private final Button upButton = new Button("Monter");
    private final Button downButton = new Button("Descendre");
    private final Button duplicateButton = new Button("Dupliquer");
    private final Button deleteButton = new Button("Supprimer");
    private final Button emptyButton = new Button("Vider tout");

    // zone détails actions (en bas au centre)
    private final TitledPane detailsAction = new TitledPane(); // Panneau dépliant
    private final HBox content = new HBox(); // Conteneur pour les champs de texte
    private final VBox contentPlusError = new VBox(); // Conteneur pour champs + messages d'erreur

    // Champs de texte pour paramétrer les actions
    private final TextField input = new TextField();
    private final TextField inputAssign1 = new TextField(); // Nom variable (assignation)
    private final TextField inputAssign2 = new TextField(); // Valeur variable (assignation)

    private final TextField inputIncVar = new TextField(); // Nom variable (incrémentation)
    private final TextField inputIncStep = new TextField(); // Pas/Valeur (incrémentation)

    private final TextField inputHauteur = new TextField(); // Valeur Hauteur
    private final TextField inputLargeur = new TextField(); // Valeur Largeur

    private final TextField inputLongueur = new TextField();
    private final TextField inputCote = new TextField();

    private final TextField inputTPX = new TextField();
    private final TextField inputTPY = new TextField();


    // Boutons +/- pour faciliter la saisie des nombres
    private final Button plusButton = new Button("+");
    private final Button minusButton = new Button("-");
    private final Button plusButtonAmount = new Button("+");
    private final Button minusButtonAmount = new Button("-");
    private final Button plusButtonAssign = new Button("+");
    private final Button minusButtonAssign = new Button("-");

    private final Button plusButtonHauteur = new Button("+");
    private final Button minusButtonHauteur = new Button("-");
    private final Button plusButtonLargeur = new Button("+");
    private final Button minusButtonLargeur = new Button("-");

    private final Button plusButtonLongueur = new Button("+");
    private final Button minusButtonLongueur = new Button("-");
    private final Button plusButtonCote = new Button("+");
    private final Button minusButtonCote = new Button("-");

    private final Button plusButtonTPX = new Button("+");
    private final Button minusButtonTPX = new Button("-");
    private final Button plusButtonTPY = new Button("+");
    private final Button minusButtonTPY = new Button("-");


    private ChangeListener<String> inputIncListener;

    private final Label errorMessage = new Label(); // msg d'information

    //on initialise le listener ici pour pouvoir l'ajouter/l'enlever plus facilement
    private ChangeListener<String> inputListener;

    private final Label infoTortue = new Label(); // msg d'information

    private ChangeListener<String> inputAssignListener;

    private ChangeListener<String> inputRectListener;

    private ChangeListener<String> inputPolygonListener;

    private ChangeListener<String> inputTPListener;

    // Liste qui gère en fonction de la checkbox
    private final ObservableList<Instruction> listeEcran = FXCollections.observableArrayList();

    public ObservableList<Instruction> getListeEcran() {
        return listeEcran;
    }

    public CheckBox getAdvancedModeCheck() {
        return advancedModeCheck;
    }

    public CheckBox getTpModeCheck() {
        return tpModeCheck;
    }

    // liaison entre les differentes vues
    private boolean isAdvancedMode = false;

    public void setAdvancedMode(boolean advanced) {
        this.isAdvancedMode = advanced;
    }

    private boolean isTpMode = false;

    public void setTpModeCheck(boolean advanced) {
        this.isTpMode = advanced;
    }

    // sauvegarde dans une List les coordonnées de téléportation
    private final List<double[]> teleportLines = new ArrayList<>();

    // méthode qui redessine toutes les TP si je coche après la case
    public void redrawTeleportLines() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // sauvegarde la position actuelle de la tortue
        double turtleX = headTurtle.getLayoutX();
        double turtleY = headTurtle.getLayoutY();
        double turtleAngle = headTurtle.getRotate();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // EFFACE TOUT
        configCanvas(); // REMET LA GRILLE

        // restaure la position de la tortue
        headTurtle.setLayoutX(turtleX);
        headTurtle.setLayoutY(turtleY);
        headTurtle.setRotate(turtleAngle);

        // redessine les traits normaux
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for (double[] l : drawnLines) {
            gc.strokeLine(l[0], l[1], l[2], l[3]);
        }

        // si la case est cochée, on go mettre les pointillés
        if (isTpMode) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.setLineDashes(10);
            for (double[] l : teleportLines) {
                gc.strokeLine(l[0], l[1], l[2], l[3]);
            }
            gc.setLineDashes(0);
        }
    }

    private final List<double[]> drawnLines = new ArrayList<>();




    public ScratchView(ScratchViewModel scratchViewModel) {
        this.scratchViewModel = scratchViewModel;

        // Configuration des 3 zones principales
        configPaletteBox();
        configProgramBox();
        configSceneBox();

        // Placement des zones dans le BorderPane
        this.setLeft(palette);
        this.setCenter(program);

        // emballe la scene dans un ScrollPane pour pouvoir scroller (si l'écran est trop petit)
        ScrollPane rightScroll = new ScrollPane(scene);
        rightScroll.setFitToWidth(true); // pour que le contenu s'étire bien en largeur
        rightScroll.setStyle("-fx-background-color: transparent;"); // enlève le fond gris moche
        rightScroll.setBorder(null);

        this.setRight(scene);

        // Chargement des données et configuration des comportements
        loadData();
        configSelection();
        configActions();
        configButtonsDisabling();
        beautify();

        // Lien avec l'exécution : quand une étape est jouée, on met à jour le dessin
        this.scratchViewModel.setOnStepExecuted(() -> updateCanvas());
    }

    /**
     * Configure la colonne de gauche
     */
    private void configPaletteBox() {
        palette.getChildren().addAll(paletteTitle, paletteList, addToProgramButton, advancedModeCheck);

        // couleurs selon le type
        paletteList.setCellFactory(lv -> new ListCell<Instruction>() {
            @Override
            protected void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("● " + item.getName()); // or item.getLabel() // Affiche le nom avec une puce

                    // Applique une couleur de texte différente selon la catégorie de l'instruction
                    switch (item.getType()) {
                        case MOVE_FORWARD:
                            setStyle("-fx-text-fill: blue;");
                            break;

                        case TURN_LEFT, TURN_RIGHT:
                            setStyle("-fx-text-fill: red;");
                            break;

                        case PEN_UP, PEN_DOWN:
                            setStyle("-fx-text-fill: green;");
                            break;

                        case VAR_DECLARATION, VAR_ASSIGNMENT, INCREMENT_VARIABLE, REPEAT, END_REPEAT:
                            setStyle("-fx-text-fill: lightseagreen;");
                            break;

                        case DRAW_RECTANGLE, DRAW_POLYGON, TELEPORTATION:
                            setStyle("-fx-text-fill: orange;");
                            break;

                        default:
                            setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        // gestion checkbox
        advancedModeCheck.setOnAction(e -> {
            listeEcran.clear(); // vide la liste
            listeEcran.addAll(scratchViewModel.getCatalogue()); // remet tout dedans

            // Si la case n'est PAS cochée, on retire le rectangle
            if (!advancedModeCheck.isSelected()) {
                // si la case n'est PAS cochée, on supprime le rectangle de la liste
                listeEcran.removeIf(i -> i.getType() == InstructionType.DRAW_RECTANGLE);
            }

            // Si on n'est PAS en mode advanced, on retire le polygone
            if (!isAdvancedMode) {
                listeEcran.removeIf(i -> i.getType() == InstructionType.DRAW_POLYGON);
            }
        });

    }

    /**
     * Rassemble les boutons d'édition du programme dans un conteneur horizontal.
     */
    private void configButtonsProg() {
        buttonProgram.getChildren().addAll(upButton, downButton, duplicateButton, deleteButton, emptyButton);
    }

    /**
     * Configure la colonne centrale (Programme en cours de création).
     */
    private void configProgramBox(){
        configButtonsProg();
        program.getChildren().addAll(programTitle, programList, buttonProgram);

        // Personnalisation de l'affichage des instructions du programme
        programList.setCellFactory(lv -> new ListCell<Instruction>() {
            @Override
            protected void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    //setText(item.toString()); // or item.getLabel()

                    // Gestion de l'indentation pour les boucles REPEAT
                    int index = getIndex();
                    int tabAmount = scratchViewModel.getTabLevel(index);
                    String spaces = "      ".repeat(tabAmount);
                    setText(spaces + "● " + item.toString());

                    // Même code couleur que pour la palette
                    switch (item.getType()) {
                        case MOVE_FORWARD:
                            setStyle("-fx-text-fill: blue;");
                            break;

                        case TURN_LEFT, TURN_RIGHT:
                            setStyle("-fx-text-fill: red;");
                            break;

                        case PEN_UP, PEN_DOWN:
                            setStyle("-fx-text-fill: green;");
                            break;

                        case VAR_DECLARATION, VAR_ASSIGNMENT, INCREMENT_VARIABLE, REPEAT, END_REPEAT:
                            setStyle("-fx-text-fill: lightseagreen;");
                            break;

                        case DRAW_RECTANGLE, DRAW_POLYGON, TELEPORTATION:
                            setStyle("-fx-text-fill: orange;");
                            break;

                        default:
                            setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        detailsAction.setText("Détails de l'action");
        program.getChildren().add(detailsAction);

        errorMessage.setTextFill(Color.RED);

        // init des champs de saisie pour les différents types
        configInputIncrement();
        updateDetails();
        configInputAmount();
        configInputAssign();
        configInputRectangle();
        configInputPolygone();
        configInputTP();
    }

    /**
     * Configure la colonne de droite (Canvas, variables, et contrôles d'exécution).
     */
    private void configSceneBox() {
        Pane canvasContainer = new Pane();

        canvasContainer.setMinSize(400, 400);
        canvasContainer.setMaxSize(400, 400);

        canvasContainer.getChildren().addAll(canvas, headTurtle); // Superpose la tortue sur le canvas

        VariablesView variablesView = new VariablesView(scratchViewModel, this);
        infoTortue.textProperty().bind(scratchViewModel.turtleInfoTextProperty()); // Lie le texte aux infos du ViewModel

        VBox infosVariablesBox = new VBox(5);
        infosVariablesBox.getChildren().addAll(infoTortue, variablesView);

        // on lui dit quoi faire avec le canvas quand on clique sur "Ré-initialiser"
        ExecutionView executionView = new ExecutionView(scratchViewModel, () -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Efface tout
            teleportLines.clear();
            drawnLines.clear();
            configCanvas(); // Redessine la grille et replace la tortue
        });

        // on rassemble la zone du bas (Infos tortue, Variables, et notre bloc d'Exécution)
        VBox bottomControls = new VBox(10, infosVariablesBox, executionView);

        scene.getChildren().addAll(sceneTitle, canvasContainer, bottomControls);
        configCanvas(); // Dessine l'état initial
    }

    /**
     * Gère les sélections dans les listes (clic de l'utilisateur).
     */
    private void configSelection() {
        // Quand on clique dans la palette, on prévient le ViewModel
        this.paletteList.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    int visualIndex = newValue.intValue();
                    if (visualIndex >= 0 && visualIndex < listeEcran.size()) {
                        Instruction selected = listeEcran.get(visualIndex);
                        int realIndex = scratchViewModel.getCatalogue().indexOf(selected);
                        scratchViewModel.getCatalogIndexProperty().set(realIndex);
                    }
                } );

        // Quand on clique dans le programme, on prévient le ViewModel et on met à jour le panneau de détails
        this.programList.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    this.scratchViewModel.getIndexProperty().set(newValue.intValue());
                    updateDetails();
                } );

        // Si le ViewModel change l'index sélectionné, on met à jour la sélection dans la vue (Synchronisation)
        this.scratchViewModel.getIndexProperty().addListener((observable, oldValue, newValue) -> {
            this.programList.getSelectionModel().select(newValue.intValue());
        });

    }

    /**
     * Dessine la grille de fond et crée la forme de la tortue.
     */
    private void configCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.fillRect(0, 0, w, h); // Fond blanc

        // Dessin de la grille
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.2);

        // Lignes verticales
        for (int i = 15; i < w; i += 30) {
            gc.strokeLine(i, 0, i, h);
        }

        gc.setStroke(Color.BLACK);

        // Lignes horizontales
        for (int i = 15; i < h; i += 30) {
            gc.strokeLine(0, i, w, i);
        }

        // Dessin de la tortue (Triangle bleu + point vert)
        // le triangle bleu
        Polygon body = new Polygon(
                -7.5, -5.0,
                7.5, 0.0,
                -7.5, 5.0
        );
        body.setFill(Color.DEEPSKYBLUE);
        body.setStroke(Color.BLACK);

        // le petit point vert (pour indiquer l'avant de la tortue)
        Circle dot = new Circle(7.5, 0.0, 2.5);
        dot.setFill(Color.GREEN);
        dot.setStroke(Color.BLACK);

        // assemblage dans le groupe
        headTurtle.getChildren().addAll(body, dot);

        // coordonnée au centre initial (400/2 = 200, ajusté avec -5)
        headTurtle.setLayoutX(195);
        headTurtle.setLayoutY(195);

        // angle initial au demarrage
        headTurtle.setRotate(scratchViewModel.getOrientation());
    }

    /**
     * Associe les boutons et événements de clics aux méthodes du ViewModel.
     */
    private void configActions() {
        addToProgramButton.setOnAction(e -> scratchViewModel.addInstruction());

        // Double-clic sur un élément de la palette = ajout direct au programme
        this.paletteList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2)
                scratchViewModel.addInstruction();
        });

        upButton.setOnAction(e -> scratchViewModel.up());
        downButton.setOnAction(e -> scratchViewModel.down());
        duplicateButton.setOnAction(e -> scratchViewModel.duplicate());
        deleteButton.setOnAction(e -> scratchViewModel.removeInstru());
        emptyButton.setOnAction(e -> scratchViewModel.empty());
    }

    /**
     * Désactive dynamiquement les boutons si l'action n'est pas possible
     * (Ex: On ne peut pas "Monter" si on est déjà tout en haut).
     */
    private void configButtonsDisabling() {
        upButton.disableProperty().bind(scratchViewModel.canUp().not());
        downButton.disableProperty().bind(scratchViewModel.canDown().not());
        duplicateButton.disableProperty().bind(scratchViewModel.canDuplicate().not());
        deleteButton.disableProperty().bind(scratchViewModel.canDelete().not());
        emptyButton.disableProperty().bind(scratchViewModel.canEmpty().not());
    }

    /**
     * Configure le champ de texte classique (ex: avancer de X pixels, répéter X fois).
     * Gère les clics sur les boutons + et -.
     */
    private void configInputAmount() {
        errorMessage.textProperty().bind(scratchViewModel.getErrorMessage()); // Lie l'erreur au ViewModel

        // Listener appelé à chaque fois qu'on tape au clavier dans ce champ
        inputListener = (observable, oldValue, newValue) -> {

            // envoie le texte brut au ViewModel (meme si c'est une case vide)
            this.scratchViewModel.getAmountProperty().set(newValue);
            this.scratchViewModel.changeValue();
            this.programList.refresh();
            canShowPlusMinusButtonsAmount(input.getText());
        };
        input.textProperty().addListener(inputListener);
        input.setOnAction(e -> {
            this.programList.refresh();
        });

        // Bouton +
        plusButtonAmount.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait +1
                int val = Integer.parseInt(input.getText());
                input.setText(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                input.setText("1"); // valeur par défaut si c'était vide
            }
        });

        // Bouton -
        minusButtonAmount.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait -1
                int val = Integer.parseInt(input.getText());
                input.setText(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                input.setText("1"); // valeur par défaut si c'était vide
            }
        });
    }

    /**
     * Configure les champs de texte pour l'assignation de variable (Var = Valeur).
     */
    private void configInputAssign() {
        inputAssignListener = (observable, oldValue, newValue) -> {
            scratchViewModel.changeAssignment(
                    inputAssign1.getText(), // Nom de la variable
                    inputAssign2.getText() // Valeur
            );
            programList.refresh();
            canShowPlusMinusButtonsAssign(inputAssign2.getText()); // Check si la valeur est un nombre
        };
        inputAssign1.textProperty().addListener(inputAssignListener);
        inputAssign1.setOnAction(e -> {
            this.programList.refresh();
        });
        inputAssign2.textProperty().addListener(inputAssignListener);
        inputAssign2.setOnAction(e -> {
            this.programList.refresh();
        });
        plusButtonAssign.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait +1
                int val = Integer.parseInt(inputAssign2.getText());
                inputAssign2.setText(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                inputAssign2.setText("1"); // valeur par défaut si c'était vide
            }
        });

        minusButtonAssign.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait -1
                int val = Integer.parseInt(inputAssign2.getText());
                inputAssign2.setText(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                inputAssign2.setText("1"); // valeur par défaut si c'était vide
            }
        });

    }

    /**
     * Relie les listes visuelles aux listes de données contenues dans le ViewModel.
     */
    private void loadData() {
        // On copie le catalogue, et on enlève le rectangle pour commencer
        listeEcran.addAll(scratchViewModel.getCatalogue());
        listeEcran.removeIf(i -> i.getType() == InstructionType.DRAW_RECTANGLE);
        listeEcran.removeIf(i -> i.getType() == InstructionType.DRAW_POLYGON);

        // On dit à l'écran d'afficher notre liste
        paletteList.setItems(listeEcran);
        programList.setItems(scratchViewModel.getInstru());
    }

    /**
     * Applique les styles graphiques de base (marges, dimensions, alignements).
     */
    private void beautify() {
        palette.setPrefWidth(250);
        scene.setPrefWidth(500);
        program.setPrefWidth(300);

        BorderPane.setAlignment(palette, Pos.CENTER_LEFT);
        BorderPane.setAlignment(program, Pos.CENTER);
        BorderPane.setAlignment(scene, Pos.CENTER_RIGHT);

        this.setPadding(new Insets(2));
        this.program.setPadding(new Insets(2));
        this.palette.setPadding(new Insets(2));
        this.scene.setPadding(new Insets(2));

        BorderPane.setMargin(palette, new Insets(2));
        BorderPane.setMargin(program, new Insets(2));
        BorderPane.setMargin(scene, new Insets(2));

        // Paramétrage des zones de saisie
        input.setMaxWidth(50);
        input.setPrefColumnCount(3);
        input.setAlignment(Pos.CENTER);

        this.content.setSpacing(5);

        inputIncVar.setMaxWidth(50);
        inputIncStep.setMaxWidth(50);
        inputIncVar.setAlignment(Pos.CENTER);
        inputIncStep.setAlignment(Pos.CENTER);
    }

    /**
     * Dessine le déplacement de la tortue à l'écran après l'exécution d'une instruction.
     */
    private void updateCanvas() {
        // mémorise l'ancienne position de la tortue
        double oldX = headTurtle.getLayoutX();
        double oldY = headTurtle.getLayoutY();

        double angle = headTurtle.getRotate();

        // récupère la nouvelle position calculée
        double newX = scratchViewModel.coordXProperty().get();
        double newY = scratchViewModel.coordYProperty().get();

        int currentIndex = scratchViewModel.getIndexProperty().get();
        Instruction currentInstru = scratchViewModel.getInstru().get(currentIndex);

        if (currentInstru.getType() == InstructionType.DRAW_RECTANGLE && scratchViewModel.isPenDown() || currentInstru.getType() == InstructionType.DRAW_RECTANGLE && !scratchViewModel.isPenDown()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);

            double w = currentInstru.getLargeur().getValue();
            double h = currentInstru.getHauteur().getValue();

            double endX = oldX + w;
            double endY = oldY - h;

            gc.strokeLine(oldX, oldY, oldX, endY);
            gc.strokeLine(oldX, endY, endX, endY);
            gc.strokeLine(endX, endY, endX, oldY);
            gc.strokeLine(endX, oldY, oldX, oldY);
        } else if (currentInstru.getType() == InstructionType.DRAW_POLYGON && scratchViewModel.isPenDown() || currentInstru.getType() == InstructionType.DRAW_POLYGON && !scratchViewModel.isPenDown()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);

            int size = currentInstru.getLongueur().getValue();
            int num = currentInstru.getCote().getValue();

            List<double[]> lines = scratchViewModel.getPolygonLines(oldX, oldY, angle, size, num);

            for (double[] l : lines) {
                gc.strokeLine(l[0], l[1], l[2], l[3]);
            }

        } else if ((currentInstru.getType() == InstructionType.TELEPORTATION && scratchViewModel.isPenDown() || currentInstru.getType() == InstructionType.TELEPORTATION && !scratchViewModel.isPenDown())) {
            double endX = currentInstru.getNouvelleX().getValue() + 195;
            double endY = 195 - currentInstru.getNouvelleY().getValue();

            teleportLines.add(new double[]{oldX, oldY, endX, endY}); // sauvegarde les téléportations

            if (isTpMode) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.setLineDashes(10);

                gc.strokeLine(oldX, oldY, endX, endY);

                gc.setLineDashes(0);
            }
        }

        // dessine la ligne rouge (si pen baissé)
        else if (scratchViewModel.isPenDown()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeLine(oldX, oldY, newX, newY); // Trace un trait de l'ancien point au nouveau
            drawnLines.add(new double[]{oldX, oldY, newX, newY}); // sauvegarde
        }

        // déplace la tortue à sa nouvelle position
        headTurtle.setLayoutX(newX);
        headTurtle.setLayoutY(newY);
        // Modifie son orientation (rotation)
        headTurtle.setRotate(scratchViewModel.getOrientation());
    }

    private void updateDetails(){
        int index = scratchViewModel.getIndexProperty().get();
        if(index == -1){
            Label empty = new Label("(aucune action selectionnée)");
            detailsAction.setContent(empty);

        }else {
            Label text = new Label(scratchViewModel.getNameInstruParIndex());
            Instruction currentInstru = scratchViewModel.getInstru().get(index);

            //si l'instruction est un pen_up ou un pen_down le textfield doit etre desactive et pas d'erreur possible
            if (scratchViewModel.isPenAction() || currentInstru.getType() == InstructionType.END_REPEAT) {
                input.textProperty().removeListener(inputListener);
                input.setText("0");
                input.textProperty().addListener(inputListener);
                input.setDisable(true);
                content.getChildren().clear();
                content.getChildren().addAll(text, input);
                contentPlusError.getChildren().clear();
                contentPlusError.getChildren().addAll(content, errorMessage);
                detailsAction.setContent(contentPlusError);
            }


            //pour les autres instructions
            else {
                // récupère l'instruction pour connaitre son type
                boolean isRotation = currentInstru.getType() == InstructionType.TURN_LEFT || currentInstru.getType() == InstructionType.TURN_RIGHT;
                Label text2 = new Label(isRotation ? "degrés" : "pixels");

                if (currentInstru.getType() == InstructionType.REPEAT)
                    text2.setText("fois");

                if (currentInstru.getType() == InstructionType.VAR_DECLARATION) {
                    text.setText("Déclaration de la variable");
                    text2.setText(""); // enlève le mot pixels a la fin
                }

                if (currentInstru.getType() == InstructionType.VAR_ASSIGNMENT) {
                    text.setText("Assignation de la variable ");
                    text2.setText(" valeur : ");
                }

                String param = scratchViewModel.getParameterInstru();

                input.setDisable(false);

                //on enleve le listener, on attend que l'user introduise l'input, on remet le listener pour notifier le vm
                input.textProperty().removeListener(inputListener);
                input.setText(param);
                input.textProperty().addListener(inputListener);
                content.getChildren().clear();

                if (text2.getText().isEmpty()) {
                    content.getChildren().addAll(text, input);
                } else if(currentInstru.getType() == InstructionType.VAR_ASSIGNMENT){
                    inputAssign1.textProperty().removeListener(inputAssignListener);
                    inputAssign2.textProperty().removeListener(inputAssignListener);

                    inputAssign1.setText(scratchViewModel.getAssignmentVar());
                    inputAssign2.setText(scratchViewModel.getAssignmentValue());

                    inputAssign1.textProperty().addListener(inputAssignListener);
                    inputAssign2.textProperty().addListener(inputAssignListener);


                    content.getChildren().addAll(text, inputAssign1, text2, inputAssign2);
                    canShowPlusMinusButtonsAssign(inputAssign2.getText());

                } else if(currentInstru.getType() == InstructionType.INCREMENT_VARIABLE) {
                    text.setText("Incrémentation de la variable");
                    text2.setText(" de ");

                    inputIncVar.textProperty().removeListener(inputIncListener);
                    inputIncStep.textProperty().removeListener(inputIncListener);

                    inputIncVar.setText(scratchViewModel.getIncrementVarName());
                    inputIncStep.setText(scratchViewModel.getIncrementStep());

                    inputIncVar.textProperty().addListener(inputIncListener);
                    inputIncStep.textProperty().addListener(inputIncListener);

                    content.getChildren().addAll(text, inputIncVar, text2, inputIncStep);

                    canShowPlusMinusButtonsIncrement(inputIncStep.getText());

                } else if (currentInstru.getType() == InstructionType.VAR_DECLARATION) {
                    content.getChildren().addAll(text, input, text2);

                } else if (currentInstru.getType() == InstructionType.DRAW_RECTANGLE) {
                    text.setText("Affiche un rectangle de hauteur ");
                    text2.setText(" et de longueur ");

                    inputHauteur.textProperty().removeListener(inputRectListener);
                    inputLargeur.textProperty().removeListener(inputRectListener);

                    inputHauteur.setText(scratchViewModel.getRectangleHauteur());
                    inputLargeur.setText(scratchViewModel.getRectangleLargeur());

                    inputHauteur.textProperty().addListener(inputRectListener);
                    inputLargeur.textProperty().addListener(inputRectListener);

                    content.getChildren().addAll(text, inputHauteur);
                    canShowPlusMinusButtonsRectangle1(inputHauteur.getText());

                    content.getChildren().addAll(text2, inputLargeur);
                    canShowPlusMinusButtonsRectangle2(inputLargeur.getText());

                } else if (currentInstru.getType() == InstructionType.DRAW_POLYGON) {
                    text.setText("Affiche un polygone de côté ");
                    text2.setText(" nombre de côtés ");

                    inputLongueur.textProperty().removeListener(inputPolygonListener);
                    inputCote.textProperty().removeListener(inputPolygonListener);

                    inputLongueur.setText(scratchViewModel.getPolygoneLongueur());
                    inputCote.setText(scratchViewModel.getPolygoneCote());

                    inputLongueur.textProperty().addListener(inputPolygonListener);
                    inputCote.textProperty().addListener(inputPolygonListener);

                    content.getChildren().addAll(text, inputLongueur);
                    canShowPlusMinusButtonsPolygone1(inputLongueur.getText());

                    content.getChildren().addAll(text2, inputCote);
                    canShowPlusMinusButtonsPolygone2(inputCote.getText());
                } else if (currentInstru.getType() == InstructionType.TELEPORTATION) {
                    text.setText("Téléporter vers (x,y) ");
                    text2.setText(" , ");

                    inputTPX.textProperty().removeListener(inputTPListener);
                    inputTPY.textProperty().removeListener(inputTPListener);

                    inputTPX.setText(scratchViewModel.getTPX());
                    inputTPY.setText(scratchViewModel.getTPY());

                    inputTPX.textProperty().addListener(inputTPListener);
                    inputTPY.textProperty().addListener(inputTPListener);

                    content.getChildren().addAll(text, inputTPX);
                    canShowPlusMinusButtonsTP1(inputTPX.getText());

                    content.getChildren().addAll(text2, inputTPY);
                    canShowPlusMinusButtonsTP2(inputTPY.getText());
                }
                else {
                    content.getChildren().addAll(text, input, text2);
                    canShowPlusMinusButtonsAmount(input.getText());
                }


                contentPlusError.getChildren().clear();
                contentPlusError.getChildren().addAll(content, errorMessage);
                detailsAction.setContent(contentPlusError);
            }
        }
    }

    private void configInputRectangle() {
        // action déclenché quand on tape au clavier
        inputRectListener = (observable, oldValue, newValue) -> {
            scratchViewModel.changeRectangle(inputHauteur.getText(), inputLargeur.getText());
            programList.refresh();
            canShowPlusMinusButtonsRectangle1(inputHauteur.getText());
            canShowPlusMinusButtonsRectangle2(inputLargeur.getText());
        };

        // attache l'action aux deux boîtes
        inputHauteur.textProperty().addListener(inputRectListener);
        inputLargeur.textProperty().addListener(inputRectListener);

        plusButtonHauteur.setOnAction(e -> {
            try { inputHauteur.setText(String.valueOf(Integer.parseInt(inputHauteur.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonHauteur.setOnAction(e -> {
            try { inputHauteur.setText(String.valueOf(Integer.parseInt(inputHauteur.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

        plusButtonLargeur.setOnAction(e -> {
            try { inputLargeur.setText(String.valueOf(Integer.parseInt(inputLargeur.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonLargeur.setOnAction(e -> {
            try { inputLargeur.setText(String.valueOf(Integer.parseInt(inputLargeur.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

    }

    private void configInputPolygone() {
        // action déclenché quand on tape au clavier
        inputPolygonListener = (observable, oldValue, newValue) -> {
            scratchViewModel.changePolygone(inputLongueur.getText(), inputCote.getText());
            programList.refresh();
            canShowPlusMinusButtonsPolygone1(inputLongueur.getText());
            canShowPlusMinusButtonsPolygone2(inputCote.getText());
        };

        // attache l'action aux deux boîtes
        inputLongueur.textProperty().addListener(inputPolygonListener);
        inputCote.textProperty().addListener(inputPolygonListener);

        plusButtonLongueur.setOnAction(e -> {
            try { inputLongueur.setText(String.valueOf(Integer.parseInt(inputLongueur.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonLongueur.setOnAction(e -> {
            try { inputLongueur.setText(String.valueOf(Integer.parseInt(inputLongueur.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

        plusButtonCote.setOnAction(e -> {
            try { inputCote.setText(String.valueOf(Integer.parseInt(inputCote.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonCote.setOnAction(e -> {
            try { inputCote.setText(String.valueOf(Integer.parseInt(inputCote.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

    }

    private void configInputTP() {
        // action déclenché quand on tape au clavier
        inputTPListener = (observable, oldValue, newValue) -> {
            scratchViewModel.makeTeleportation(inputTPX.getText(), inputTPY.getText());
            programList.refresh();
            canShowPlusMinusButtonsTP1(inputTPX.getText());
            canShowPlusMinusButtonsTP2(inputTPY.getText());
        };

        // attache l'action aux deux boîtes
        inputTPX.textProperty().addListener(inputTPListener);
        inputTPY.textProperty().addListener(inputTPListener);

        plusButtonTPX.setOnAction(e -> {
            try { inputTPX.setText(String.valueOf(Integer.parseInt(inputTPX.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonTPX.setOnAction(e -> {
            try { inputTPX.setText(String.valueOf(Integer.parseInt(inputTPX.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

        plusButtonTPY.setOnAction(e -> {
            try { inputTPY.setText(String.valueOf(Integer.parseInt(inputTPY.getText()) + 1)); }
            catch (NumberFormatException ex) {}
        });
        minusButtonTPY.setOnAction(e -> {
            try { inputTPY.setText(String.valueOf(Integer.parseInt(inputTPY.getText()) - 1)); }
            catch (NumberFormatException ex) {}
        });

    }

    /**
     * Configure les champs et boutons pour l'action "Incrémenter une variable".
     */
    private void configInputIncrement() {
        inputIncListener = (observable, oldValue, newValue) -> {
            scratchViewModel.changeIncrement(inputIncVar.getText(), inputIncStep.getText());
            programList.refresh();
            canShowPlusMinusButtonsIncrement(inputIncStep.getText());
        };
        inputIncVar.textProperty().addListener(inputIncListener);
        inputIncVar.setOnAction(e -> programList.refresh());

        inputIncStep.textProperty().addListener(inputIncListener);
        inputIncStep.setOnAction(e -> programList.refresh());

        plusButton.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait +1
                int val = Integer.parseInt(inputIncStep.getText());
                inputIncStep.setText(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                inputIncStep.setText("1"); // valeur par défaut si c'était vide
            }
        });

        minusButton.setOnAction(e -> {
            try {
                // on récupère le nombre actuel et on fait -1
                int val = Integer.parseInt(inputIncStep.getText());
                inputIncStep.setText(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                inputIncStep.setText("-1"); // valeur par défaut si c'était vide
            }
        });
    }

    /**
     * Affiche ou masque les boutons +/- d'incrémentation selon si l'utilisateur
     * a bien rentré un nombre valide dans le champ.
     */
    public void canShowPlusMinusButtonsIncrement(String input){
        content.getChildren().removeAll(minusButton, plusButton);
        try {
            Integer.parseInt(input);
            content.getChildren().addAll(minusButton, plusButton);
        } catch (NumberFormatException _){

        }
    }

    // idem pour tout ce qui est pixels, degrés etc..
    public void canShowPlusMinusButtonsAmount(String input){
        content.getChildren().removeAll(minusButtonAmount, plusButtonAmount);
        try {
            Integer.parseInt(input);
            content.getChildren().addAll(minusButtonAmount, plusButtonAmount);
        } catch (NumberFormatException _){

        }
    }

    // idem pour le champ "Valeur" d'assignation variable
    public void canShowPlusMinusButtonsAssign(String input){
        content.getChildren().removeAll(minusButtonAssign, plusButtonAssign);
        try {
            Integer.parseInt(input);
            content.getChildren().addAll(minusButtonAssign, plusButtonAssign);
        } catch (NumberFormatException _){

        }
    }

    public void canShowPlusMinusButtonsRectangle1(String hauteur) {
        content.getChildren().removeAll(minusButtonHauteur, plusButtonHauteur);

        try {
            Integer.parseInt(hauteur);
            content.getChildren().addAll(plusButtonHauteur, minusButtonHauteur);
        } catch (NumberFormatException ignored) {} // Si c'est une variable, on ignore !
    }

    public void canShowPlusMinusButtonsRectangle2(String largeur) {
        content.getChildren().removeAll(minusButtonLargeur, plusButtonLargeur);

        try {
            Integer.parseInt(largeur);
            content.getChildren().addAll(plusButtonLargeur, minusButtonLargeur);
        } catch (NumberFormatException ignored) {}

    }

    public void canShowPlusMinusButtonsPolygone1(String longueur) {
        content.getChildren().removeAll(minusButtonLongueur, plusButtonLongueur);

        try {
            Integer.parseInt(longueur);
            content.getChildren().addAll(plusButtonLongueur, minusButtonLongueur);
        } catch (NumberFormatException ignored) {} // Si c'est une variable, on ignore !
    }

    public void canShowPlusMinusButtonsPolygone2(String cote) {
        content.getChildren().removeAll(minusButtonCote, plusButtonCote);

        try {
            Integer.parseInt(cote);
            content.getChildren().addAll(plusButtonCote, minusButtonCote);
        } catch (NumberFormatException ignored) {} // Si c'est une variable, on ignore !
    }

    public void canShowPlusMinusButtonsTP1(String longueur) {
        content.getChildren().removeAll(minusButtonTPX, plusButtonTPX);

        try {
            Integer.parseInt(longueur);
            content.getChildren().addAll(plusButtonTPX, minusButtonTPX);
        } catch (NumberFormatException ignored) {} // Si c'est une variable, on ignore !
    }

    public void canShowPlusMinusButtonsTP2(String cote) {
        content.getChildren().removeAll(minusButtonTPY, plusButtonTPY);

        try {
            Integer.parseInt(cote);
            content.getChildren().addAll(plusButtonTPY, minusButtonTPY);
        } catch (NumberFormatException ignored) {} // Si c'est une variable, on ignore !
    }


}
