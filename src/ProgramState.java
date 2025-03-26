import javafx.scene.layout.Region;

/**
 * The ProgramState class exists for organizational purposes.
 * It is intended to by extended by subclasses with their own unique code
 * to represent different states, or "screens", that the software runs in.
 */

public abstract class ProgramState {
    /*
    StateController reference so that we can switch states while within a state.
    Useful for buttons and the like.
     */
    protected StateController controller;

    public ProgramState(StateController controller){
        this.controller = controller;
    }

    //This is where all the actual "screen" stuff happens.
    public abstract Region createContent();
}
