package namesayer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import namesayer.Creations;
import namesayer.MainListener;
import namesayer.Progress;

/**
 * Basic controller class that is extended by almost all scenes.
 * Has access to Main (for changing scenes), Creations for both databases, progress data, playlist data,
 * and some of its own JavaFX-related classes
 */
public class CustomController {

	protected MainListener mainListener;
	protected Creations creations;
	protected Creations userCreations;
	protected Scene scene;
	protected Pane rootPane;

	//protected List<String> allUserRatings = new ArrayList<String>(); // Note: this is package visibility as we need to access this in the ProgressScreenController class!
	protected Progress progress;

	// Creating a field to store the playlist data so that it can be accessed between create a playlist and play a playlist screens.
	protected ObservableList<String> playlistData = FXCollections.observableArrayList();

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

	public void setPlaylist(ObservableList<String> playlistData) {
		this.playlistData = playlistData;
	}

}
