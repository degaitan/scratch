package scratch.viewmodel;

import scratch.model.Program;

import java.io.File;

public class MainViewModel {

    private final Program program;

    private final ScratchViewModel scratchViewModel;

    public MainViewModel(Program program) {
        this.program = program;
        this.scratchViewModel = new ScratchViewModel(program);
    }

    public ScratchViewModel getScratchViewModel() {
        return scratchViewModel;
    }

    // sauvegardes

   // enregistrer
    public void saveProgram(File file) {
        if (file != null) {
            program.saveToFile(file);
        }
    }

    // fichier -> ouvrir
    public void loadProgram(File file) {
        if (file != null) {
            program.loadFromFile(file);

            //la vue a changé donc on doit mettre le programme à jour
            scratchViewModel.notifyProgramChanged();
        }
    }
}
