package namesayer;

import java.io.File;
import java.io.IOException;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import namesayer.media.BasicMediaPlayerController;
import namesayer.media.CreationListener;
import namesayer.media.CreationModuleController;
import namesayer.media.RecordingListener;
import namesayer.media.RecordingModuleController;
import javafx.scene.control.Alert.AlertType;

public class RecordScreenController extends CustomController implements CreationListener, RecordingListener {

	@FXML
	private ListView<Creation> creationList;
	@FXML
	private ListView<Recording> recordingList;
	
	private Creation selectedCreation;
	private Recording selectedRecording;

	BasicMediaPlayerController mediaPlayerController;
	RecordingModuleController recordingController;
	CreationModuleController creationController;
	
	@Override
	public void init() {
		updateCreationList();
		updateRecordingList();
		
		setupListeners();
		setupMediaPlayer();

		creationList.getSelectionModel().selectFirst();
		recordingList.getSelectionModel().selectFirst();
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
		ObservableList<Creation> creationDataList = FXCollections.observableArrayList(creations.getCreations());
		
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
					+ File.separator + "MediaPlayerPaneBasic.fxml"));
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
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
					+ File.separator + "CreationPane.fxml"));
			Pane creationPane = loader.load();
			
			Scene creationScene = new Scene(creationPane, 400, 300);
			creationScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			creationController = loader.getController();
			creationController.init();
			creationController.setCreations(creations);
			creationController.setCreationListener(this);
			
			creationStage.setScene(creationScene);
			creationStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteName() {
		// dialog code modified from https://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(AlertType.CONFIRMATION);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
		
		alert.setTitle("Delete selected Names");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete the Name " + selectedCreation.getName()
				+ ", as well as all of its Recordings?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Thread deleteCreation = new Thread(new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					creations.deleteCreation(selectedCreation);

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
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("media"
					+ File.separator + "RecordingPane.fxml"));
			Pane recordingPane = loader.load();
			
			Scene recordingScene = new Scene(recordingPane, 400, 300);
			recordingScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			recordingController = loader.getController();
			recordingController.init();
			recordingController.setCreations(creations);
			recordingController.setCreation(creation);
			recordingController.setRecordingListener(this);
			
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
		dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
		
		alert.setTitle("Delete selected Recordings");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete the selected Recording of Name " + selectedRecording.getCreation().getName() + "?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Thread deleteRecordings = new Thread(new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					selectedRecording.delete();
					selectedRecording.removeSelf();

					creations.saveState();

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
		if (selectedRecording != null) {
			selectedRecording.setBad(!selectedRecording.isBad());
			
			creations.saveState();
		}
		
		updateRecordingList();
	}
	
	public void goMain() {
		mainListener.goMain();
	}

	@Override
	public void recordingFinished(File recordingFile, Creation creation) {
		Recording newRecording = new Recording(creation, recordingFile);
		creation.addRecording(newRecording);
		
		creations.saveState();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				updateRecordingList();
				recordingList.getSelectionModel().select(newRecording);
			}
		});
	}

	@Override
	public void creationFinished(Creation creation, boolean newRecording) {
		creations.addCreation(creation);
		
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
	
}
