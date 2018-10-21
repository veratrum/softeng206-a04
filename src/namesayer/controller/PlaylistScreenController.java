package namesayer.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.CreationListener;
import namesayer.DatabaseLocation;
import namesayer.ImportListener;
import namesayer.PlaylistListener;
import namesayer.Recording;
import namesayer.RecordingListener;
import namesayer.Utils;


public class PlaylistScreenController extends CustomController implements ImportListener, CreationListener, RecordingListener, PlaylistListener {

	// Declaring the fields used in the controller
	@FXML
	private ListView<Creation> namesList;

	@FXML 
	private ListView<String> playlist;

	@FXML
	private TextField nameToSearchFor;

	@FXML
	private TextField nameToAddToPlaylist;

	private ObservableList<Creation> namesListData;
	private Creation selectedName;

	private CreationModuleController creationController;
	private RecordingModuleController recordingController;


	@Override
	public void init() {
		addListenerToSearchTextField();
		nameToSearchFor.setPromptText("Search for a name");
		
		
	}

	@Override
	public void load() {
		updateNamesList();
	}

	//=== Event handlers for the buttons on the play screen ===//

	public void startPlaylist() {

		// Sending an error message to the user that they need to create a playlist first
		if (getPlaylist().size() == 0 || getPlaylist() == null) {
			Alert alert = new Alert(AlertType.ERROR);
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
			alert.setTitle("Playlist Error");
			alert.setHeaderText("No Playlist Created");
			alert.setContentText("Error you have not yet created a playlist\nplease create a playlist first!");

			alert.showAndWait();
		}
		else {
			// Passing the practice screen the playlistData
			playlistData.clear();
			playlistData.setAll(getPlaylist());
			mainListener.goPractice();
		}
	}
	public void addNameToPlaylist() {

		// Creating a background task to add the name to the playlist
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				String nameToAdd = nameToAddToPlaylist.getText();

				// If the name is not already in the playlist - and only contains characters/spaces/hyphens then add it!
				if(!(playlist.getItems().contains(nameToAdd)) && isValidCompositeName(nameToAdd)) {

					// We need to check if the database or userDatabase has a recording for the names to be added
					String[] nameToAddParsed = nameToAdd.split("[-\\s]"); 

					for (String s : nameToAddParsed) {

						// If there is no creation for that name then we need to have the user make one!
						if (!creations.creationExists(s) && !userCreations.creationExists(s)) {
							Platform.runLater(new Task<Void>() {
								@Override
								public Void call() {
									doNewCreation(s);

									return null;
								}
							});

							return null;
						}
					}



					// Now we need to update the playlist and the text field.
					new Thread(new Runnable() {
						@Override public void run() {
							Platform.runLater(new Runnable() {
								@Override public void run() {

									if (!nameToAdd.equals("")) {
										playlist.getItems().add(nameToAdd);
									}

									nameToAddToPlaylist.clear();
									nameToAddToPlaylist.setPromptText("Please enter a name");
								}
							});
						}
					}).start();
				}
				else { 
					// we need to display an error message to the user that they are adding a name already in the playlist.
					new Thread(new Runnable() {
						@Override public void run() {
							Platform.runLater(new Runnable() {
								@Override public void run() {
									Alert alert = new Alert(AlertType.ERROR);
									DialogPane dialogPane = alert.getDialogPane();
									dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
									alert.setTitle("Name Error");
									alert.setHeaderText("NAME ERROR");
									alert.setContentText("Error the name you are trying to add is already\nin the playlist or contains illegal characters");

									alert.showAndWait();

									// Name is invalid so we are clearing it!
									nameToAddToPlaylist.clear();
									nameToAddToPlaylist.setPromptText("Please enter a name");
								}
							});

						}
					}).start();
				}
				return null;
			}
		};
		new Thread(task).start();

	}

	public void removeNameFromPlaylist() {
		playlist.getItems().remove(playlist.getSelectionModel().getSelectedItem());
	}

	public void clearNameSelection() {
		nameToAddToPlaylist.clear();
		nameToAddToPlaylist.setPromptText("Please enter a name");
	}

	public void addToNameSelection() {


		// Creating the background task that adds the name to the textField 
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// Getting the selected name in the namesListView
				selectedName = namesList.getSelectionModel().getSelectedItem();

				// Getting the value of the textField to be added to the playlist
				String nameToAdd = nameToAddToPlaylist.textProperty().getValue();

				if (selectedName != null) {

					// Checking if we need to add a space between names - if it is the first name or not
					if (nameToAdd.length() == 0) {
						nameToAdd = selectedName.getName();
					}
					else {
						nameToAdd = nameToAdd + " " + selectedName.getName();
					}
				}

				// Now updating the nameToAddToPlaylist textfield if it is below 50 characters - otherwise display an error message.
				if(nameToAdd.length() < 50) {
					nameToAddToPlaylist.setText(nameToAdd);
				}
				else {
					// Otherwise displaying an error message to the user that the name they wish to add is too long!
					new Thread(new Runnable() {
						@Override public void run() {
							Platform.runLater(new Runnable() {
								@Override public void run() {
									Alert alert = new Alert(AlertType.ERROR);
									DialogPane dialogPane = alert.getDialogPane();
									dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
									alert.setTitle("Name Length Error");
									alert.setHeaderText("Name Length Error");
									alert.setContentText("Error the name you are trying to create\nis greater than 50 characters.");

									alert.showAndWait();

								}
							});

						}
					}).start();
				}

				return null;
			}
		};
		new Thread(task).start();
	}

	public void importAPlaylist() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ImportEntryModule.fxml"));
			Pane importModulePane = loader.load();

			Scene importScene = new Scene(importModulePane, 400, 300);

			ImportEntryModuleController controller = loader.getController();
			controller.setScene(importScene);
			controller.setImportListener(this);

			Stage importModule = new Stage();
			importModule.setScene(importScene);
			importModule.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to export the playlist that the user has created (in the listView)
	 * as a text file.
	 */
	public void exportAPlaylist() {
		/* allow the user to choose where to save the file
		modified from https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm */
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Playlist");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setInitialFileName("playlist_" + Utils.getDateFilenameFragment() + ".txt");

		File saveLocation = fileChooser.showSaveDialog(scene.getWindow());

		// user closed save dialog
		if (saveLocation == null) {
			return;
		}

		// getting the playlist to export
		ObservableList<String> playlistToExport = getPlaylist();
		boolean exportSuccesful = false;

		try {
			FileWriter writer = new FileWriter(saveLocation);

			for (int i = 0; i < playlistToExport.size(); i++) {
				String str = playlistToExport.get(i);
				writer.write(str);

				// This prevent creating a blank like at the end of the file
				if(i < playlistToExport.size() - 1) {
					writer.write("\n");
				}
			}
			writer.close();
			exportSuccesful = true;
		}
		catch(Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
			alert.setTitle("Export Error");
			alert.setHeaderText("PLAYLIST EXPORT ERROR");
			alert.setContentText("There was an error exporting your\nplaylist please try again.");

			alert.showAndWait();
		}

		if (exportSuccesful) {
			Alert alert = new Alert(AlertType.INFORMATION);
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
			alert.setTitle("Export Succesful");
			alert.setHeaderText("EXPORT SUCCESFUL");
			alert.setContentText("Your playlist was exported succesfully!");

			alert.showAndWait();
		}
	}

	public void returnToMainScreen() {
		mainListener.goMain();
	}

	private void updateNamesList() { 
		
		// We are displaying both the userRecordings names and the database names in the list view - we must find names in userRecordigs not in the database
		ObservableList<Creation> databaseNames = FXCollections.observableArrayList(creations.getCreations());
		ObservableList<Creation> userCreationNames = FXCollections.observableArrayList(userCreations.getCreations());

		// Storing all the user creations we need to add to the namesListView.
		ArrayList<Creation> userCreationsNotInDatabase = new ArrayList<Creation>();

		// Iterating over the userRecordings and database to find the user recordings we need to add.
		for (Creation currentUserCreation : userCreationNames) {

			if(!databaseNames.contains(currentUserCreation)){
				userCreationsNotInDatabase.add(currentUserCreation);
			}
		}

		// Now we need to merge the database to add and the userRecordings needing to be added.
		ArrayList<Creation> allNamesToAdd = new ArrayList<Creation>();
		allNamesToAdd.addAll(userCreationsNotInDatabase);
		allNamesToAdd.addAll(databaseNames);


		// Populating the namesList on the ListView
		namesListData = FXCollections.observableArrayList(allNamesToAdd);
		namesList.setItems(namesListData);
		namesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


		namesList.refresh();
	}

	public void addListenerToSearchTextField() {
		nameToSearchFor.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {

				// Checking if we should restore the names list to original - or search for newValue
				if (newVal.equals("")) {
					updateNamesList();
					nameToSearchFor.setPromptText("Search for a name");
				}
				else {
					searchForName(newVal);
				}

			}
		});
	}

	public void searchForName(String nameToSearch) {

		// Creating a list to store the found names in and also a list of all the names to search through
		ObservableList<Creation> foundNames = FXCollections.observableArrayList();
		ObservableList<Creation> namesListData = FXCollections.observableArrayList(namesList.getItems());

		// Iterating over all the names to search for what name we have in the textField
		for (Creation currentCreation: namesListData) {
			if( ( currentCreation.toString().toLowerCase() ).contains(nameToSearch.toLowerCase()) ){
				foundNames.add(currentCreation);
			}
		}

		// Updating the namesList with the result
		namesList.setItems(foundNames);
	}

	public ObservableList<String> getPlaylist(){
		return playlist.getItems();
	}

	@Override
	public void importFinished(List<String> names) {
		// do nothing
	}

	@Override
	public void importFinishedSorted(List<List<String>> names) {
		for (List<String> list: names) {
			String name = "";

			for (int i = 0; i < list.size(); i++) {
				name += list.get(i);

				if (i != list.size() - 1) {
					name += " ";
				}
			}

			if (name != "") {
				playlist.getItems().add(name);
			}
		}
	}

	@Override
	public boolean checkNamesBeforeSubmit(List<String> names) {
		for (String name: names) {
			// we cannot add a non-existent name to the database
			if (!creations.creationExists(name) && !userCreations.creationExists(name)) {
				doNewCreation(name);

				return false;
			}
		}

		return true;
	}

	private void doNewCreation(String name) {
		// ask the user if they would like to create the first non-existent name
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
		
		alert.setTitle("Name not found");
		alert.setHeaderText("Name " + name + " was not found in the database or user database.");
		alert.setContentText("Would you like to add it now?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// open a new window allowing the user to add a new name

			try {
				Stage creationStage = new Stage();

				File src = new File("src");
				File namesayer = new File(src, "namesayer");
				File fxml = new File(namesayer, "fxml");
				File loaderPath = new File(fxml, "CreationPane.fxml");
				URL path = loaderPath.toURI().toURL();
				FXMLLoader loader = new FXMLLoader(path);
				Pane creationPane = loader.load();

				creationPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

				Scene creationScene = new Scene(creationPane, 400, 300);

				creationController = loader.getController();
				creationController.init();
				creationController.setCreations(creations);
				creationController.setUserCreations(userCreations);
				creationController.setCreationListener(this);
				creationController.setDatabaseLocation(DatabaseLocation.USER_DATABASE);
				creationController.setDefaultText(name);

				creationStage.setScene(creationScene);
				creationStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doNewRecording(Creation creation) {
		try {
			Stage recordingStage = new Stage();

			File src = new File("src");
			File namesayer = new File(src, "namesayer");
			File fxml = new File(namesayer, "fxml");
			File loaderPath = new File(fxml, "RecordingPane.fxml");
			URL path = loaderPath.toURI().toURL();
			FXMLLoader loader = new FXMLLoader(path);
			Pane recordingPane = loader.load();

			Scene recordingScene = new Scene(recordingPane, 400, 300);

			recordingController = loader.getController();
			recordingController.init();
			recordingController.setCreations(creations);
			recordingController.setUserCreations(userCreations);
			recordingController.setCreation(creation);
			recordingController.setRecordingListener(this);
			recordingController.setSaveLocation(DatabaseLocation.USER_DATABASE);

			recordingStage.setScene(recordingScene);
			recordingStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * User finished creating a new creation that was not on a playlist they tried to import.
	 */
	@Override
	public void creationFinished(Creation creation, boolean newRecording, DatabaseLocation location) {
		userCreations.addCreation(creation);

		if (newRecording) {
			doNewRecording(creation);
		}
	}

	/**
	 * User finished creating a new recording for the new creation that was not on a
	 * playlist they tried to import.
	 */
	@Override
	public void recordingFinished(File recordingFile, Creation creation, DatabaseLocation location) {
		Recording newRecording = new Recording(creation, recordingFile);

		creation.addRecording(newRecording);

		userCreations.saveState();
	}
	
	

	@Override
	public void playlistFinished() {
		playlist.getItems().clear();
	}


	private boolean isValidCompositeName(String name) {
		if (name.length() == 0 || name.length() > 32) {
			return false;
		}

		if (!Character.isLetter(name.charAt(0)) || !Character.isUpperCase(name.charAt(0))) {
			return false;
		}

		for (int i = 0; i < name.length(); i++) {
			char character = name.charAt(i);

			if (!Character.isLetter(character) && !(character == '_') && !(character == ' ') && !(character == '-')) {
				return false;
			}
		}

		return true;
	}

}
