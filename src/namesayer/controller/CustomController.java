package namesayer.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import namesayer.Creations;
import namesayer.MainListener;
import namesayer.Progress;

/**
 * Basic controller.
 * Has access to Main (for changing scenes), Creations,
 * and some of its own JavaFX-related classes
 *
 */
public class CustomController {

	protected MainListener mainListener;
	protected Creations creations;
	protected Creations userCreations;
	protected Scene scene;
	protected Pane rootPane;
	
	//protected List<String> allUserRatings = new ArrayList<String>(); // Note: this is package visibility as we need to access this in the ProgressScreenController class!
	protected Progress progress;
	
	public CustomController() {
		
	}
	
	/**
	 * Called at the start of the application
	 */
	public void init() {
		
	}
	
	/**
	 * Called whenever the scene is entered
	 */
	public void load() {
		
	}
	
	/**
	 * Called whenever the scene is exited
	 */
	public void dispose() {
		
	}
	
	public void setMainListener(MainListener listener) {
		this.mainListener = listener;
	}
	
	public void setCreations(Creations creations) {
		this.creations = creations;
	}
	
	public void setUserCreations(Creations userCreations) {
		this.userCreations = userCreations;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void setRootPane(Pane rootPane) {
		this.rootPane = rootPane;
	}
	
	public void setProgress(Progress progress) {
		this.progress = progress;
	}
	
}
