package namesayer.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.ImportListener;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;


public class PlaylistScreenController extends CustomController implements ImportListener {

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


	@Override
	public void init() {
		updateNamesList();
		addListenerToSearchTextField();
		nameToSearchFor.setPromptText("Search for a name");
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
			playlistData.addAll(getPlaylist());
			mainListener.goPractice();

			// Clearing the playlist listView
			playlist.getItems().clear();
		}
	}
	public void addNameToPlaylist() {

		// Creating a background task to add the name to the playlist
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				String nameToAdd = nameToAddToPlaylist.getText();

				// If the name is not already in the playlist - and only contains characters/spaces/hyphens then add it!
				if( !(playlist.getItems().contains(nameToAdd)) && isNameMadeOfAcceptableCharacters(nameToAdd)) {

					// We need to check if the database or userDatabase has a recording for the names to be added
					String[] nameToAddParsed = nameToAdd.split("[-\\s]"); 

					for (String s : nameToAddParsed) {

						// If there is no creation for that name then we need to have the user make one!
						if ((creations.getCreationByName(s) == null) && (userCreations.getCreationByName(s) == null)) {


							/**
							 * INSERT DIALOG BOX ASKING THE USER TO CREATE A CREATION FOR THIS NAME
							 * IN THE USERCREATIONS DIRECTORY HERE!!!!!!!! - make sure that the user
							 * cannot exit the create a creation for this name screen/dialog box without
							 * having made a recording otherwise this will create an error in the playlist
							 * screen
							 * 
							 * make it a method so that it can be called again when we are importing from a 
							 * text file so that we can check that the creations exist in that situation too!
							 */
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
						nameToAdd = selectedName.toString();
					}
					else {
						nameToAdd = nameToAdd + " " + selectedName.toString();
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

		// getting the playlist to export
		ObservableList<String> playlistToExport = getPlaylist();
		boolean exportSuccesful = false;

		// Asking the user for the directory where they want to store the playlist.
		DirectoryChooser dirChooser = new DirectoryChooser();
		File chosenDir = dirChooser.showDialog(scene.getWindow());
		
		try {
			FileWriter writer = new FileWriter(chosenDir + "/Playlist" + ".txt");
			
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





	//=== Below are the helper functions for the event handlers ===//

	public boolean isNameMadeOfAcceptableCharacters(String name) {
		char[] characters = name.toCharArray();

		for (char c : characters) {
			// If the character is not a letter, space or hyphen return false!
			if( !Character.isLetter(c) && !(c == ' ') && !(c == '-') ) {
				return false;
			}
		}
		return true;
	}


	private void updateNamesList() { // NEED TO CHECK THAT THIS WORKS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
			public void changed(ObservableValue observable, String oldVal, String newVal) {

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

}
