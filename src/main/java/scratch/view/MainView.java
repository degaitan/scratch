package scratch.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import scratch.model.Instruction;
import scratch.model.InstructionType;
import scratch.viewmodel.MainViewModel;
import scratch.viewmodel.ScratchViewModel;

import java.io.File;

// BorderPane = Un conteneur avec 5 zones (Haut, Bas, Gauche, Droite, Centre)
public class MainView extends BorderPane {
    private MainViewModel mainViewModel;

    private ScratchView scratchView;

    private ScratchViewModel scratchViewModel;

    public MainView(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;

        // récupère le ViewModel de la ScratchView et on l'instancie
        this.scratchViewModel = mainViewModel.getScratchViewModel();

        // On crée la grosse interface du jeu
        scratchView = new ScratchView(scratchViewModel);

        // --- Creation du menu du haut ---
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Charger un programme");
        MenuItem saveItem = new MenuItem("Sauvegarder le programme");
        MenuItem advancedItem = new MenuItem("Advanced mode");
        MenuItem basicItem = new MenuItem("Basic mode");
        fileMenu.getItems().addAll(loadItem, saveItem, advancedItem);
        menuBar.getMenus().add(fileMenu);

        // Action : Quand on clique sur "Sauvegarder"
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le programme");
            File file = fileChooser.showSaveDialog(this.getScene().getWindow()); // Ouvre la fenêtre Windows/Mac classique
            mainViewModel.saveProgram(file); // On donne l'ordre au grand patron
        });

        // Action : Quand on clique sur "Charger"
        loadItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Charger un programme");
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            mainViewModel.loadProgram(file);
        });

        // Action : Quand on clique sur "Advanced mode"
        advancedItem.setOnAction(e -> {
            fileMenu.getItems().clear();
            fileMenu.getItems().addAll(loadItem, saveItem, basicItem);

            scratchView.getListeEcran().clear();
            scratchView.getListeEcran().addAll(scratchViewModel.getCatalogue()); // remet tout dedans

            if (!scratchView.getAdvancedModeCheck().isSelected()) {
                // si la case n'est PAS cochée, on supprime le rectangle de la liste
                scratchView.getListeEcran().removeIf(i -> i.getType() == InstructionType.DRAW_RECTANGLE);
            }

            scratchView.setAdvancedMode(true);
        });

        // Action : Quand on clique sur "Basic mode"
        basicItem.setOnAction(e -> {
            fileMenu.getItems().clear();
            fileMenu.getItems().addAll(loadItem, saveItem, advancedItem);

            scratchView.getListeEcran().clear();
            scratchView.getListeEcran().addAll(scratchViewModel.getCatalogue()); // remet tout dedans
            scratchView.getListeEcran().removeIf(i -> i.getType() == InstructionType.DRAW_POLYGON); // puis retire le polygone

            if (!scratchView.getAdvancedModeCheck().isSelected()) {
                // si la case n'est PAS cochée, on supprime le rectangle de la liste
                scratchView.getListeEcran().removeIf(i -> i.getType() == InstructionType.DRAW_RECTANGLE);
            }

            scratchView.setAdvancedMode(false);
        });

        // On place le menu tout en haut (Top)
        this.setTop(menuBar);

        // On la place au centre
        this.setCenter(scratchView);
    }
}
