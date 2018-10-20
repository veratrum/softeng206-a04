package namesayer;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import namesayer.controller.CustomController;


public class Main extends Application implements MainListener {

	private Stage stage;

	private Scene splashScene;
	private Scene mainScene;
	private Scene recordScene;
	private Scene practiceScene;
	private Scene helpScene;
	private Scene importScene;
	private Scene progressScene;
	private Scene playlistScene;

	private CustomController splashController;
	private CustomController mainController;
	private CustomController recordController;
	private CustomController practiceController;
	private CustomController helpController;
	private CustomController importController;
	private CustomController progressController;
	private CustomController playlistController;

	/* the currently selected scene's controller. we need this to call its dispose method
	before changing to a different scene */
	private CustomController selectedController;

	private Creations creations;
	private Creations userCreations;
	private Progress progress;

	// Creating a field to store the playlistData so that it can be accessed between create a playlist and play a playlist screens.
	private ObservableList<String> playlistData  = FXCollections.observableArrayList();;

	private boolean secondPassed;

	/**
	 * Allows us to return both scene and controller from the helper function
	 * Avoids code reuse
	 */
	private class ScreenResult {
		public Scene scene;
		public CustomController controller;

		public ScreenResult(Scene scene, CustomController controller) {
			this.scene = scene;
			this.controller = controller;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;

		ScreenResult result = loadScene("SplashScreen.fxml", 800, 600);
		splashScene = result.scene;
		splashController = result.controller;

		stage.setScene(splashScene);
		stage.show();
		selectedController = splashController;

		secondPassed = false;

		new Thread(new Task<Void>() {
			@Override
			public Void call() {
				loadCreations();
				loadProgress();
				loadScenes();

				return null;
			}

			@Override
			public void done() {
				Platform.runLater(new Task<Void>() {
					@Override
					public Void call() {
						if (secondPassed) {
							goMain();
						} else {
							secondPassed = true;
						}

						return null;
					}
				});
			}
		}).start();

		new Thread(new Task<Void>() {
			@Override
			public Void call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void done() {
				Platform.runLater(new Task<Void>() {
					@Override
					public Void call() {
						if (secondPassed) {
							goMain();
						} else {
							secondPassed = true;
						}

						return null;
					}
				});
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void loadCreations() {
		creations = new Creations(DatabaseLocation.DATABASE, "database", "metadata.xml");
		userCreations = new Creations(DatabaseLocation.USER_DATABASE, "userdata", "metadata.xml");
	}

	private void loadProgress() {
		progress = new Progress();
	}

	private void loadScenes() {
		ScreenResult result = loadScene("MainScreen.fxml", 800, 600);
		mainScene = result.scene;
		mainController = result.controller;

		result = loadScene("RecordScreen.fxml", 800, 600);
		recordScene = result.scene;
		recordController = result.controller;

		result = loadScene("PracticeScreen.fxml", 800, 600);
		practiceScene = result.scene;
		practiceController = result.controller;

		result = loadScene("HelpScreen.fxml", 800, 600);
		helpScene = result.scene;
		helpController = result.controller;

		result = loadScene("ImportScreen.fxml", 800, 600);
		importScene = result.scene;
		importController = result.controller;

		result = loadScene("ProgressScreen.fxml", 800, 600);
		progressScene = result.scene;
		progressController = result.controller;

		result = loadScene("PlaylistScreen.fxml", 800, 600);
		playlistScene = result.scene;
		playlistController = result.controller;
	}

	private ScreenResult loadScene(String fxmlPath, int width, int height) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml" + File.separator + fxmlPath));
		Pane root = null;

		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		CustomController controller = loader.getController();
		Scene scene = new Scene(root, width, height);

		controller.setScene(scene);
		controller.setRootPane(root);
		controller.setMainListener(this);
		controller.setCreations(creations);
		controller.setUserCreations(userCreations);
		controller.setProgress(progress);
		controller.setPlaylist(playlistData);
		controller.init();

		return new ScreenResult(scene, controller);
	}

	@Override
	public void goMain() {
		selectedController.dispose();
		stage.setScene(mainScene);
		selectedController = mainController;
		selectedController.load();
	}

	@Override
	public void goRecord() {
		selectedController.dispose();
		stage.setScene(recordScene);
		selectedController = recordController;
		selectedController.load();
	}

	@Override
	public void goPractice() {
		selectedController.dispose();
		stage.setScene(practiceScene);
		selectedController = practiceController;
		selectedController.load();
	}

	@Override
	public void goHelp() {
		selectedController.dispose();
		stage.setScene(helpScene);
		selectedController = helpController;
		selectedController.load();
	}

	@Override
	public void goImport() {
		selectedController.dispose();
		stage.setScene(importScene);
		selectedController = importController;
		selectedController.load();
	}

	@Override
	public void goProgress() {
		selectedController.dispose();
		stage.setScene(progressScene);
		selectedController = progressController;
		selectedController.load();
	}

	@Override
	public void goPlaylist() {
		selectedController.dispose();
		stage.setScene(playlistScene);
		selectedController = playlistController;
		selectedController.load();
	}


}
