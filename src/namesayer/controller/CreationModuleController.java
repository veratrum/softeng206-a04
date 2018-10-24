package namesayer.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.CreationListener;
import namesayer.Creations;
import namesayer.DatabaseLocation;

/**
 * This class controls a name creation module that is used in multiple places in the application.
 * It enforces name requirements, and gives feedback to the user when they are not met.
 * There is an option to create a new recording afterwards, which sends back a flag to the listener,
 * allowing it to handle this.
 */
public class CreationModuleController extends CustomController implements Initializable {

	@FXML
	private TextField nameField;
	@FXML
	private CheckBox newRecordingCheckBox;
	@FXML
	private Label requirements1;
	@FXML
	private Label requirements2;
	@FXML
	private Label requirements3;
	@FXML
	private Label requirements4;

	private CreationListener creationListener;
	private DatabaseLocation databaseLocation;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void init() {
		requirements1.setVisible(false);
		requirements2.setVisible(false);
		requirements3.setVisible(false);
		requirements4.setVisible(false);
		newRecordingCheckBox.setSelected(true);
	}

	public void setCreationListener(CreationListener listener) {
		this.creationListener = listener;
	}

	public void setDatabaseLocation(DatabaseLocation databaseLocation) {
		this.databaseLocation = databaseLocation;
	}

	/**
	 * The text that is in the name field to begin with.
	 */
	public void setDefaultText(String defaultText) {
		nameField.setText(defaultText);
	}

	public void okClicked() {
		String name = nameField.getText();
		boolean validName = Creations.isValidName(name);

		// it is impossible to create a new creation in the database but in case we changed it later
		boolean nameExists;
		switch (databaseLocation) {
		case DATABASE:
			nameExists = creations.creationExists(name);
			break;
		case USER_DATABASE:
			nameExists = userCreations.creationExists(name);
			break;
		default:
			nameExists = creations.creationExists(name);
			break;
		}

		if (!validName) {
			requirements1.setVisible(true);
			requirements2.setVisible(true);
			requirements3.setVisible(true);
			requirements4.setVisible(false);
		} else if (nameExists) {
			requirements1.setVisible(false);
			requirements2.setVisible(false);
			requirements3.setVisible(false);
			requirements4.setVisible(true);
		} else {
			boolean doNewRecording = newRecordingCheckBox.isSelected();

			Creation newCreation = new Creation(name, databaseLocation);

			creationListener.creationFinished(newCreation, doNewRecording, databaseLocation);

			closeWindow();
		}
	}

	public void cancelClicked() {
		closeWindow();
	}

	private void closeWindow() {
		// adapted from https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage stage = (Stage) nameField.getScene().getWindow();
				stage.close();
			}
		});
	}

}
