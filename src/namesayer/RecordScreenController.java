package namesayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import namesayer.mediaplayer.BasicMediaPlayerController;
import javafx.scene.control.Alert.AlertType;

public class RecordScreenController extends CustomController {

	@FXML
	private ListView<Creation> creationList;
	@FXML
	private ListView<Recording> recordingList;
	
	private Creation selectedCreation;
	private Recording selectedRecording;
	
	BasicMediaPlayerController mediaPlayerController;
	
	@Override
	public void init() {
		updateCreationList();
		updateRecordingList();
		
		setupListeners();
		setupMediaPlayer();
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
				
				mediaPlayerController.setRecording(null);
			}
			
		});
		
		recordingList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Recording>() {

			@Override
			public void changed(ObservableValue<? extends Recording> observable, Recording oldValue, Recording newValue) {
				selectedRecording = newValue;
				
				mediaPlayerController.setRecording(selectedRecording);
			}
			
		});
	}
	
	private void setupMediaPlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("mediaplayer"
					+ File.separator + "MediaPlayerPaneBasic.fxml"));
			Pane mediaPlayerPane = loader.load();
			
			mediaPlayerController = loader.getController();

			mediaPlayerPane.setLayoutX(250);
			mediaPlayerPane.setLayoutY(375);
			rootPane.getChildren().add(mediaPlayerPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newName() {
		
	}
	
	public void deleteName() {
		// dialog code modified from https://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(AlertType.CONFIRMATION);
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
		
	}
	
	public void deleteRecording() {
		if (selectedRecording == null) {
			return;
		}
		
		// dialog code modified from https://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(AlertType.CONFIRMATION);
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
	
}
