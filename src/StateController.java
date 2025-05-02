import javafx.scene.Scene;

import java.util.Hashtable;

/**
 * The StateController class organizes and switches between ProgramStates.
 * The main purpose of this class is organization and ease of mind.
 * The class is essentially a HashMap with the added feature of a "main" or "active" element.
 */
public class StateController {

    private Hashtable<String, ProgramState> states;
    private Scene mainScene;

    public StateController(Scene mainScene){
        this.mainScene = mainScene;
        states = new Hashtable<>();
    }

    public void addState(String name, ProgramState p){
        states.put(name, p);
    }

    public void removeState(String name){
        states.remove(name);
    }

    public void setActive(String name){
        mainScene.setRoot(states.get(name).createContent());
    }

}
