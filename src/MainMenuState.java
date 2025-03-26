import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.IOException;

/**
 * This ProgramState represents the main menu.
 * For now, it basically either ends the program or moves it into the MapState.
 */
public class MainMenuState extends ProgramState{

    public MainMenuState(StateController stateController){
        super(stateController);
    }

    public Region createContent(){
        // Assigned an anchor pane, so it just isn't null if it fails.
        AnchorPane root = new AnchorPane();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("res/main-menu.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }

        return root;
    }

    @FXML
    private void exitButtonAction(){
        System.exit(0);
    }

    @FXML
    private void mapButtonAction(){
        controller.setActive("map");
    }
}
