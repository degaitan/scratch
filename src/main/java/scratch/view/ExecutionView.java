package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.viewmodel.ExecutionViewModel;
import scratch.viewmodel.ScratchViewModel;

public class ExecutionView extends VBox {

    public ExecutionView(ScratchViewModel scratchVM, Runnable onResetCanvas) {
        // On récupère le "chronomètre" depuis le VM
        ExecutionViewModel execVM = scratchVM.getExecutionVM();

        // LES BOUTONS RADIO (Mode Auto / Mode Manuel)
        RadioButton manualRadio = new RadioButton("Exécution manuelle");
        RadioButton autoRadio = new RadioButton("Exécution automatique");

        // On les met dans un groupe pour qu'on ne puisse en cocher qu'un seul à la fois
        ToggleGroup group = new ToggleGroup();
        manualRadio.setToggleGroup(group);
        autoRadio.setToggleGroup(group);
        manualRadio.setSelected(true); // Par défaut, on est en manuel

        // on branche le vm
        // Si le bouton "auto" est coché, la variable 'isAutoMode' du ViewModel devient VRAIE.
        execVM.isAutoModeProperty().bind(autoRadio.selectedProperty());

        // On les aligne horizontalement (HBox = Horizontal Box)
        HBox radioBox = new HBox(15, autoRadio, manualRadio);
        radioBox.setAlignment(Pos.CENTER);

        // Boutons d'action
        Button reinitButton = new Button("Ré-initialiser");
        Button nextButton = new Button("Suivant");
        Button stopButton = new Button("Arrêter");

        // Binding des textes et désactivations vers le ViewModel qui le controle
        reinitButton.textProperty().bind(scratchVM.leftBtnTextProperty());
        reinitButton.disableProperty().bind(scratchVM.leftBtnDisabledProperty());

        nextButton.textProperty().bind(scratchVM.rightBtnTextProperty());
        nextButton.disableProperty().bind(scratchVM.rightBtnDisabledProperty());

        // Le bouton Stop s'allume seulement si l'animation est lancée
        stopButton.disableProperty().bind(execVM.stopBtnDisabledProperty());
        // Ces deux lignes le rendent invisible si on n'est pas en mode "Auto"
        stopButton.visibleProperty().bind(execVM.isAutoModeProperty());
        stopButton.managedProperty().bind(execVM.isAutoModeProperty());

        // Actions des boutons (quand on clique)
        reinitButton.setOnAction(e -> {
            scratchVM.clickLeftButton(); // On donne l'ordre au cerveau
            if (onResetCanvas != null) onResetCanvas.run(); // Nettoie le canvas de ScratchView
        });

        nextButton.setOnAction(e -> scratchVM.clickRightButton());
        stopButton.setOnAction(e -> execVM.stopAuto());

        HBox buttonBox = new HBox(10, reinitButton, nextButton, stopButton);
        buttonBox.setAlignment(Pos.CENTER);

        // (Valeur minimum : 0.01s, Valeur max : 5s, Valeur par défaut : 1s)
        Slider speedSlider = new Slider(0.01, 5, 1);
        speedSlider.setShowTickMarks(true); // Affiche les petits traits
        speedSlider.setShowTickLabels(true); // Affiche les chiffres sous les traits
        speedSlider.setMajorTickUnit(1); // Un trait tous les 1 unité

        // Bidirectional = Si le curseur bouge, le chrono change. Si le chrono change, le curseur bouge.
        speedSlider.valueProperty().bindBidirectional(execVM.speedDelayProperty());

        // Le curseur est invisible si on n'est pas en mode Auto
        speedSlider.visibleProperty().bind(execVM.isAutoModeProperty());
        speedSlider.managedProperty().bind(execVM.isAutoModeProperty());

        // Assemblage de l'ExecutionView
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(radioBox, buttonBox, speedSlider);
    }
}