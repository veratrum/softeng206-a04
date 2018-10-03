package namesayer;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


public class Main extends Application implements MainListener {
	
	private Stage stage;

	private Scene mainScene;
	private Scene recordScene;
	private Scene practiceScene;
	private Scene helpScene;
	private Scene importScene;
	
	private Creations creations;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.stage = primaryStage;
			
			loadCreations();
			loadScenes();
			
			stage.setScene(mainScene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void loadCreations() {
		creations = new Creations();
	}
	
	private void loadScenes() throws IOException {
		mainScene = loadScene("MainScreen.fxml", 800, 600);
		mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		recordScene = loadScene("RecordScreen.fxml", 800, 600);
		recordScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		practiceScene = loadScene("PracticeScreen.fxml", 800, 600);
		practiceScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		helpScene = loadScene("MainScreen.fxml", 800, 600);
		helpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		importScene = loadScene("MainScreen.fxml", 800, 600);
		importScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	}
	
	private Scene loadScene(String fxmlPath, int width, int height) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		Pane root = loader.load();
		CustomController controller = loader.getController();
		controller.setMainListener(this);
		controller.setCreations(creations);
		controller.init();
		Scene scene = new Scene(root, width, height);
		
		return scene;
	}

	@Override
	public void goRecord() {
		stage.setScene(recordScene);
	}

	@Override
	public void goPractice() {
		stage.setScene(practiceScene);
	}

	@Override
	public void goHelp() {
		stage.setScene(helpScene);
	}

	@Override
	public void goImport() {
		stage.setScene(importScene);
	}
}
