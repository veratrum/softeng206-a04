package namesayer;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;


public class PlaylistScreenController extends CustomController {

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
		mainListener.goPractice();

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				playlistData.addAll(getPlaylist());
				return null;
			}
		};
		new Thread(task).start();
	}
	public void addNameToPlaylist() {

		// Creating a background task to add the name to the playlist
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				String nameToAdd = nameToAddToPlaylist.getText();

				// If the name is not already in the playlist then add it!
				if( !(playlist.getItems().contains(nameToAdd))) {
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
									alert.setHeaderText("That name has already been added");
									alert.setContentText("Error the name you are trying to add\nis already in the playlist");

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

	}

	public void exportAPlaylist() {

	}

	public void returnToMainScreen() {
		mainListener.goMain();
	}





	//=== Below are the helper functions for the event handlers ===//

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

}
