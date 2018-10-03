package namesayer;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * Basic scene.
 * Has access to Main (for changing scenes), and Creations
 *
 */
public class CustomController {

	protected MainListener mainListener;
	protected Creations creations;
	protected Scene scene;
	protected Pane rootPane;
	
	public CustomController() {
		
	}
	
	public void init() {
		
	}
	
	public void setMainListener(MainListener listener) {
		this.mainListener = listener;
	}
	
	public void setCreations(Creations creations) {
		this.creations = creations;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void setRootPane(Pane rootPane) {
		this.rootPane = rootPane;
	}
	
}
