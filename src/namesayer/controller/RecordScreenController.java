package namesayer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.CreationListener;
import namesayer.Creations;
import namesayer.DatabaseLocation;
import namesayer.Recording;
import namesayer.RecordingListener;

/**
 * Allows the user to view, play and modify (user database only) Recording and Name entries
 * in the database and user recordings.
 */
public class RecordScreenController extends CustomController implements CreationListener, RecordingListener {

	@FXML
	private ListView<Creation> creationList;
	@FXML
	private ListView<Recording> recordingList;
	@FXML
	private Label whichViewLabel;
	@FXML
	private Button newNameButton;
	@FXML
	private Button deleteNameButton;
	@FXML
	private Button newRecordingButton;
	@FXML
	private Button deleteRecordingButton;
	@FXML
	private Button rateRecordingButton;
	@FXML
	private Button swapListButton;

	private Creation selectedCreation;
	private Recording selectedRecording;

	private BasicMediaPlayerController mediaPlayerController;
	private RecordingModuleController recordingController;
	private CreationModuleController creationController;

	private DatabaseLocation databaseLocation;

	@Override
	public void init() {
		databaseLocation = DatabaseLocation.DATABASE;

		updateCreationList();
		updateRecordingList();

		setupListeners();
		setupMediaPlayer();

		updateWhichList();
	}

	@Override
	public void load() {
		updateCreationList();
		updateRecordingList();

		if (selectedRecording != null) {
			mediaPlayerController.setRecording(selectedRecording.getFile());
		}
	}

	@Override
	public void dispose() {

	}

	private void updateCreationList() {
		Creations whichCreations;
		switch(databaseLocation) {
		case DATABASE:
			whichCreations = creations;
			break;
		case USER_DATABASE:
			whichCreations = userCreations;
			break;
		default:
			whichCreations = creations;
			break;
		}

		ObservableList<Creation> creationDataList = FXCollections.observableArrayList(whichCreations.getCreations());

		creationList.setItems(creationDataList);

		creationList.refresh();
	}

	private void updateRecordingList() {
		ObservableList<Recording> recordingDataList;

		if (selectedCreation == null) {
			recordingDataList = FXCollections.emptyObservableList();
		} else {
			recordingDataList = FXCollections.observableArrayList(selectedCreation.getRecordings());
		}

		recordingList.setItems(recordingDataList);

		recordingList.refresh();
	}

	private void setupListeners() {
		creationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Creation>() {

			@Override
			public void changed(ObservableValue<? extends Creation> observable, Creation oldValue, Creation newValue) {
				selectedCreation = newValue;

				updateRecordingList();
				recordingList.getSelectionModel().clearSelection();
				recordingList.getSelectionModel().selectFirst();
			}

		});

		recordingList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Recording>() {

			@Override
			public void changed(ObservableValue<? extends Recording> observable, Recording oldValue, Recording newValue) {
				selectedRecording = newValue;

				if (selectedRecording == null) {
					mediaPlayerController.setRecording(null);
				} else {
					mediaPlayerController.setRecording(selectedRecording.getFile());
				}
			}

		});
	}

	private void setupMediaPlayer() {
		try {
			File src = new File("src");
			File namesayer = new File(src, "namesayer");
			File fxml = new File(namesayer, "fxml");
			File loaderPath = new File(fxml, "MediaPlayerPaneBasic.fxml");
			URL path = loaderPath.toURI().toURL();
			FXMLLoader loader = new FXMLLoader(path);
			/*FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
			+ File.separator + "MediaPlayerPaneBasic.fxml"));*/
			Pane mediaPlayerPane = loader.load();

			mediaPlayerController = loader.getController();

			//mediaPlayerPane.setLayoutX(250);
			//mediaPlayerPane.setLayoutY(375);
			mediaPlayerPane.setLayoutX(317);
			mediaPlayerPane.setLayoutY(461);
			rootPane.getChildren().add(mediaPlayerPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newName() {
		try {
			Stage creationStage = new Stage();

			File src = new File("src");
			File namesayer = new File(src, "namesayer");
			File fxml = new File(namesayer, "fxml");
			File loaderPath = new File(fxml, "CreationPane.fxml");
			URL path = loaderPath.toURI().toURL();
			FXMLLoader loader = new FXMLLoader(path);
			/*FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
					+ File.separator + "CreationPane.fxml"));*/
			Pane creationPane = loader.load();

			creationPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			Scene creationScene = new Scene(creationPane, 400, 300);

			creationController = loader.getController();
			creationController.init();
			creationController.setCreations(creations);
			creationController.setUserCreations(userCreations);
			creationController.setCreationListener(this);
			creationController.setDatabaseLocation(databaseLocation);

			creationStage.setScene(creationScene);
			creationStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteName() {
		if (selectedCreation == null) {
			return;
		}

		// dialog code modified from https://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(AlertType.CONFIRMATION);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("../fxml/dialog.css").toExternalForm());

		alert.setTitle("Delete selected Name");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete the Name " + selectedCreation.getName()
		+ ", as well as all of its Recordings?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Thread deleteCreation = new Thread(new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					switch (databaseLocation) {
					case DATABASE:
						creations.deleteCreation(selectedCreation);
						break;
					case USER_DATABASE:
						userCreations.deleteCreation(selectedCreation);
						break;
					default:
						break;
					}

					selectedCreation.delete();

					return null;
				}

				@Override
				protected void done() {
					// must update ui from 'edt' of javafx
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							updateCreationList();
							creationList.getSelectionModel().clearSelection();
							creationList.getSelectionModel().selectFirst();
							updateRecordingList();
						}
					});
				}

			});
			deleteCreation.start();
		}
	}

	public void newRecording() {
		if (selectedCreation == null) {
			return;
		}

		doNewRecording(selectedCreation);
	}

	/**
	 * Helper function used by both newCreation and newRecording buttons
	 */
	private void doNewRecording(Creation creation) {
		try {
			Stage recordingStage = new Stage();

			File src = new File("src");
			File namesayer = new File(src, "namesayer");
			File fxml = new File(namesayer, "fxml");
			File loaderPath = new File(fxml, "RecordingPane.fxml");
			URL path = loaderPath.toURI().toURL();
			FXMLLoader loader = new FXMLLoader(path);
			/*FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
					+ File.separator + "RecordingPane.fxml"));*/
			Pane recordingPane = loader.load();

			Scene recordingScene = new Scene(recordingPane, 400, 300);

			recordingController = loader.getController();
			recordingController.init();
			recordingController.setCreations(creations);
			recordingController.setUserCreations(userCreations);
			recordingController.setCreation(creation);
			recordingController.setRecordingListener(this);
			recordingController.setSaveLocation(databaseLocation);

			recordingStage.setScene(recordingScene);
			recordingStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteRecording() {
		if (selectedRecording == null) {
			return;
		}

		// dialog code modified from https://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(AlertType.CONFIRMATION);


		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("../fxml/dialog.css").toExternalForm());

		alert.setTitle("Delete selected Recordings");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete " + selectedRecording.getFile().getName() +
				" from the Name " + selectedRecording.getCreation().getName() + "?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Thread deleteRecordings = new Thread(new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					selectedRecording.delete();
					selectedRecording.removeSelf();

					switch (databaseLocation) {
					case DATABASE:
						creations.saveState();
						break;
					case USER_DATABASE:
						userCreations.saveState();
						break;
					default:
						break;
					}

					return null;
				}

				@Override
				protected void done() {
					// must update ui from 'edt' of javafx
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							updateRecordingList();
							recordingList.getSelectionModel().clearSelection();
							recordingList.getSelectionModel().selectFirst();
						}
					});
				}

			});
			deleteRecordings.start();
		}
	}

	public void rateRecording() {
		if (selectedRecording == null) {
			return;
		}

		selectedRecording.setBad(!selectedRecording.isBad());

		creations.saveState();

		updateRecordingList();
	}

	public void goMain() {
		mainListener.goMain();
	}

	@Override
	public void recordingFinished(File recordingFile, Creation creation, DatabaseLocation location) {
		Recording newRecording = new Recording(creation, recordingFile);

		creation.addRecording(newRecording);

		switch (databaseLocation) {
		case DATABASE:
			creations.saveState();
			break;
		case USER_DATABASE:
			userCreations.saveState();
			break;
		default:
			break;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateRecordingList();
				recordingList.getSelectionModel().select(newRecording);
			}
		});
	}

	@Override
	public void creationFinished(Creation creation, boolean newRecording, DatabaseLocation location) {
		switch (databaseLocation) {
		case DATABASE:
			creations.addCreation(creation);
			break;
		case USER_DATABASE:
			userCreations.addCreation(creation);
			break;
		default:
			break;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateCreationList();
				creationList.getSelectionModel().select(creation);
			}
		});

		// if the user wanted to create a new recording for the new creation
		if (newRecording) {
			doNewRecording(creation);
		}
	}

	public void swapLists() {
		switch (databaseLocation) {
		case DATABASE:
			databaseLocation = DatabaseLocation.USER_DATABASE;
			break;
		case USER_DATABASE:
			databaseLocation = DatabaseLocation.DATABASE;
			break;
		default:
			break;
		}

		selectedRecording = null;
		selectedCreation = null;

		updateWhichList();
	}

	private void updateWhichList() {
		switch (databaseLocation) {
		case DATABASE:
			whichViewLabel.setText("Database Recordings Manager");
			swapListButton.setText("Swap to User Recordings");
			newNameButton.setDisable(true);
			deleteNameButton.setDisable(true);
			newRecordingButton.setDisable(true);
			deleteRecordingButton.setDisable(true);
			break;
		case USER_DATABASE:
			whichViewLabel.setText("User Recordings Manager");
			swapListButton.setText("Swap to Database Recordings");
			newNameButton.setDisable(false);
			deleteNameButton.setDisable(false);
			newRecordingButton.setDisable(false);
			deleteRecordingButton.setDisable(false);
			break;
		default:
			break;
		}

		updateCreationList();
		updateRecordingList();

		creationList.getSelectionModel().clearSelection();
		creationList.getSelectionModel().selectFirst();
		recordingList.getSelectionModel().clearSelection();
		recordingList.getSelectionModel().selectFirst();
	}

}
