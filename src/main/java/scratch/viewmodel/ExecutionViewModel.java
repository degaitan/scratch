package scratch.viewmodel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;

public class ExecutionViewModel {

    private final ScratchViewModel scratchVM;

    private final BooleanProperty isAutoMode = new SimpleBooleanProperty(false);
    private final DoubleProperty speedDelay = new SimpleDoubleProperty(1.0); // vitesse (1.0 = 1 seconde)
    private final BooleanProperty stopBtnDisabled = new SimpleBooleanProperty(true); // bouton "Arrêter" désactivé ou pas


    private Timeline timeline;

    public ExecutionViewModel(ScratchViewModel scratchVM) {
        this.scratchVM = scratchVM;

        speedDelay.addListener((obs, oldVal, newVal) -> {
            if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
                playAuto();
            }
        });

        isAutoMode.addListener((obs, oldVal, newVal) -> {
            if (!newVal) stopAuto();
        });
    }

    public BooleanProperty isAutoModeProperty() { return isAutoMode; }
    public DoubleProperty speedDelayProperty() { return speedDelay; }
    public BooleanProperty stopBtnDisabledProperty() { return stopBtnDisabled; }

    // Lancer la lecture automatique (Le bouton "Play")
    public void playAuto() {
        // S'il y avait déjà un chrono, on l'arrête pour en recréer un proprement
        if (timeline != null) timeline.stop();

        // On crée un nouveau chrono réglé sur la vitesse actuelle (speedDelay)
        timeline = new Timeline(new KeyFrame(Duration.seconds(speedDelay.get()), e -> {
            boolean canContinue = scratchVM.executeStep();

            // di erreur ou fin de programme, on arrête
            if (!canContinue) {
                stopAuto();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // on active le bouton Stop
        stopBtnDisabled.set(false);

        // desactive le bouton Suivant/Exécuter
        scratchVM.rightBtnDisabledProperty().set(true);
    }

    // Arrêter la lecture automatique (bouton "Stop")
    public void stopAuto() {
        if (timeline != null) timeline.stop();

        // on désactive le bouton Stop
        stopBtnDisabled.set(true);

        //le vm remet ses boutons dans le bon état
        scratchVM.updateState();
    }
}