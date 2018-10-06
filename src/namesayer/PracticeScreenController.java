package namesayer;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class PracticeScreenController extends CustomController {

	@FXML
	private ListView<RadioButton> firstNameList;
	@FXML
	private ListView<RadioButton> lastNameList;
	@FXML 
	private ListView<String> playlist;


	// Setting up the toggle groups for the radio buttons
	private ToggleGroup firstNameGroup = new ToggleGroup();
	private ToggleGroup lastNameGroup = new ToggleGroup();

	// Setting up the radioButtons on the listViews
	public static final ObservableList<RadioButton> firstNameRadioButtons = FXCollections.observableArrayList();
	public static final ObservableList<RadioButton> lastNameRadioButtons = FXCollections.observableArrayList();

	// fields used for user feedback to track their progress
	private Optional<String> userRating;




	@Override
	public void init() {
		updateFirstNameList();
		updateLastNameList();
	}



	// code modified from: https://stackoverflow.com/questions/47757368/javafx-radio-buttons-inside-listview-selectionmodel?rq=1
	private void updateFirstNameList() {
		ObservableList<Creation> firstNameDataList = FXCollections.observableArrayList(creations.getCreations());


		for (Creation firstName : firstNameDataList)
		{
			firstNameRadioButtons.add(new RadioButton(firstName.toString()));
		}

		firstNameGroup.getToggles().addAll(firstNameRadioButtons);
		firstNameList.setItems(firstNameRadioButtons);

		firstNameGroup.selectedToggleProperty().addListener((obs, oldSel, newSel) -> {
			firstNameList.getSelectionModel().select((RadioButton) newSel);
			firstNameList.getFocusModel().focus(firstNameList.getSelectionModel().getSelectedIndex());
		});

		firstNameList.setCellFactory(param -> new RadioListCell());
		firstNameList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
		{
			if (newSel != null)
			{
				RadioButton tempRadioButton = (RadioButton) newSel;
				tempRadioButton.setSelected(true);
			}
			if (oldSel != null)
			{
				RadioButton tempRadioButton = (RadioButton) oldSel;
				tempRadioButton.setSelected(false);
			}
		});


		firstNameList.refresh();
	}

	// code modified from: https://stackoverflow.com/questions/47757368/javafx-radio-buttons-inside-listview-selectionmodel?rq=1
	private void updateLastNameList() {
		ObservableList<Creation> lastNameDataList = FXCollections.observableArrayList(creations.getCreations());


		for (Creation lastName : lastNameDataList)
		{
			lastNameRadioButtons.add(new RadioButton(lastName.toString()));
		}

		lastNameGroup.getToggles().addAll(lastNameRadioButtons);
		lastNameList.setItems(lastNameRadioButtons);

		lastNameGroup.selectedToggleProperty().addListener((obs, oldSel, newSel) -> {
			lastNameList.getSelectionModel().select((RadioButton) newSel);
			lastNameList.getFocusModel().focus(lastNameList.getSelectionModel().getSelectedIndex());
		});

		lastNameList.setCellFactory(param -> new RadioListCell());
		lastNameList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
		{
			if (newSel != null)
			{
				RadioButton tempRadioButton = (RadioButton) newSel;
				tempRadioButton.setSelected(true);
			}
			if (oldSel != null)
			{
				RadioButton tempRadioButton = (RadioButton) oldSel;
				tempRadioButton.setSelected(false);
			}
		});


		lastNameList.refresh();
	}



	public void addNameToPlaylist() {

		boolean isFirstNameSelected = false;
		String firstNameSelected = "";

		// Getting the selected toggle button and parsing it to get the firstName selected.
		if (firstNameGroup.getSelectedToggle() != null) {
			Toggle firstNameToggleSelection = firstNameGroup.getSelectedToggle();
			String[] firstNameParsedString = firstNameToggleSelection.toString().split("'");
			firstNameSelected = firstNameParsedString[1];

			isFirstNameSelected = true;
		}

		boolean isLastNameSelected = false;
		String lastNameSelected = "";

		// Getting the selected toggle button and parsing it to get the lastName selected.
		if (lastNameGroup.getSelectedToggle() != null) {
			Toggle lastNameToggleSelection = lastNameGroup.getSelectedToggle();
			String[] lastNameParsedString = lastNameToggleSelection.toString().split("'");
			lastNameSelected = lastNameParsedString[1];

			isLastNameSelected = true;
		}


		// Creating the name the we need to add to the playlist.
		String nameToAdd = "";
		if (isFirstNameSelected == true) {
			nameToAdd = firstNameSelected;
			if (isLastNameSelected == true) {
				nameToAdd = nameToAdd + " " + lastNameSelected;
			}
		}
		else if (isLastNameSelected == true) {
			nameToAdd = lastNameSelected;
		}

		// We need to check if the name that the user has selected has already been added to the playlist
		boolean okayToAdd = true;
		ObservableList<String> playlistNames = playlist.getItems();
		for (String name : playlistNames) {
			if (nameToAdd.equals(name)) {
				okayToAdd = false;
			}
		}

		// Adding the name to the playlist if either a first or last name has been chosen to add to playlist - and if it is not a repeated name.
		if (okayToAdd && (isFirstNameSelected == true || isLastNameSelected == true)) {
			playlist.getItems().add(nameToAdd);
		}



	}

	/**
	 * This method is called when the remove button is clicked on the practice screen. It removes the selected name from the playlist
	 */
	public void removeNameFromPlayList() {
		playlist.getItems().remove(playlist.getSelectionModel().getSelectedItem());
	}


	/**
	 * This method is called when the user has selected to play a single name from the playlist that they have created.
	 */
	public void playSelectedName() {

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				playName(playlist.getSelectionModel().getSelectedItem());
				return null;
			}
		};
		new Thread(task).start();
	}



	/**
	 * This method is called when the user presses the play playlist button in the practice screen of the GUI
	 */
	public void playPlaylist(){

		if (playlist.getItems().size() > 1) {
			// ask the user if they want to randomise the order when there is more than one name in the playlist
			Alert confirmation = new Alert(AlertType.CONFIRMATION);
			
			DialogPane dialogPane = confirmation.getDialogPane();
			dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
			
			confirmation.setTitle("Play Recordings");
			confirmation.setHeaderText("Play Multiple Recordings");
			confirmation.setContentText("You have selected to play multuple recordings\ndo you wish to shuffle the order the selected\nrecordings are played in?");
			ButtonType buttonYes = new ButtonType("Yes");
			ButtonType buttonNo = new ButtonType("No");
			confirmation.getButtonTypes().setAll(buttonYes, buttonNo);
			Optional<ButtonType> result = confirmation.showAndWait();

			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					// Getting the playlist 
					ObservableList<String> playList = playlist.getItems();
					ArrayList<String> namesToPlay = new ArrayList<String>();

					// Changing the type from an Observable list
					for ( String name : playList) {
						namesToPlay.add(name);
					}

					if (result.get() == buttonYes){
						Collections.shuffle(namesToPlay);
					}

					for(String names : namesToPlay) {
						playName(names);
					}

					askUserForRating();

					return null;
				}
			};
			Thread thread = new Thread(task);
			thread.start();




		}
		// Otherwise don't prompt the user to shuffle as there is no need
		else if (playlist.getItems().size() != 0) {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {

					// Getting the playlist 
					ObservableList<String> playList = playlist.getItems();

					for(String names : playList) {
						playName(names);
					}

					askUserForRating();

					return null;
				}
			};
			new Thread(task).start();

		}






	}


	/**
	 * This method is used to get the users rating on how good they beleive their performance for the last playlist was
	 */
	public void askUserForRating() {
		new Thread(new Runnable() {
			@Override public void run() {
				Platform.runLater(new Runnable() {
					@Override public void run() {
						// Now we need to ask the user how they think they did in their last attempt so we can track their progress.
						List<String> choices = new ArrayList<>();
						choices.add("1");choices.add("2");choices.add("3");choices.add("4");choices.add("5");choices.add("6");choices.add("7");choices.add("8");choices.add("9");choices.add("10");


						ChoiceDialog<String> dialog = new ChoiceDialog<>("1", choices);
						
						DialogPane dialogPane = dialog.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("dialog.css").toExternalForm());
						
						
						dialog.setTitle("Rate your progress");
						dialog.setHeaderText("How do you think you performed for your playlist?");
						dialog.setContentText("Rating:");

						// Getting the users self rating.
						userRating = dialog.showAndWait();

						// Adding the user rating to the array which stores all past user ratings - if it is present
						if(userRating.isPresent()) {
							allUserRatings.add(userRating.get());
						}

					}
				});

			}
		}).start();
	}

	/**
	 * A method which is called when the return to main screen button is clicked - returns the user to the main screen
	 */
	public void GoToMainScreen(ActionEvent event) {
		mainListener.goMain();
	}





	/**
	 * A function which plays a name from the playlist
	 */
	public void playName(String selectedName) {
		String firstName;
		String lastName;

		// Now we need to parse the name to see if there is a first and/or last name in the selected name.

		// If the name contains a space then there is a first and last name.
		boolean isLastName = false;
		if (selectedName.contains(" ")) {
			String[] parsedName = selectedName.split(" ");

			firstName = parsedName[0];
			lastName = parsedName[1];

			isLastName = true;
		}
		else {
			firstName = selectedName;
			lastName = "";
		}

		// Now we need to play the recording

		// Only one name so we don't need to merge the audio recordings so just play the one .wav file.
		if (isLastName == false) {
			PlayAudio(creations.getCreationByName(firstName).getRandomGoodRecording().getFile());

		}
		// Else we need to merge the audio recordings and play it
		else {
			mergeAndPlayFirstAndLastNames(firstName, lastName);
		}
	}






	/**
	 * This function is used to play a name within the playlist of the GUI that has two componenets and therefore needs to be merged before it is played.
	 * 
	 * modified from https://stackoverflow.com/questions/653861/join-two-wav-files-from-java
	 */
	public void mergeAndPlayFirstAndLastNames(String firstName, String lastName) {
		Recording firstNameRecording = creations.getCreationByName(firstName).getRandomGoodRecording();
		Recording lastNameRecording = creations.getCreationByName(lastName).getRandomGoodRecording();

		try {

			// Creating an audio stream from the recordings
			AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(firstNameRecording.getFile().toString()));
			AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(lastNameRecording.getFile().toString()));

			// Merging the audio files
			AudioInputStream appendedFiles = new AudioInputStream(new SequenceInputStream(clip1, clip2),clip1.getFormat(),clip1.getFrameLength() + clip2.getFrameLength());

			// Creating a playlist directory to store the .wav file we will create.
			createPlaylistDirectory();

			File meregedRecordingFile = new File("Playlist/" + firstName + "_" + lastName + ".wav");
			AudioSystem.write(appendedFiles,AudioFileFormat.Type.WAVE, meregedRecordingFile);

			// Now we need to play the merged .wav file
			PlayAudio(meregedRecordingFile);

			// Finally we must delete the file!
			meregedRecordingFile.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	/**
	 *  This helper function creates a PlayList directory to store the temporary data associated with the current playlist
	 */
	public void createPlaylistDirectory() {
		File playlistDir = new File("Playlist");

		if (!playlistDir.exists()) {
			playlistDir.mkdir();
		}
	}




	/**
	 * Helper function that given a recording of a creation which plays it.
	 * @param recording
	 */
	public void PlayAudio(File recordingToPlay) {


		Clip clip;
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(recordingToPlay));
			clip.start();
			//Sleep the thread so that the recordings don't all play at once.
			Thread.sleep(clip.getMicrosecondLength()/1000);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}




	/**
	 * This method is called when the user presses the associated button to clear the radioButton selections.
	 */
	public void clearNameSelection() {

		
		// If a toggle is selected - then clear it
		if(firstNameGroup.getSelectedToggle() != null) {
			firstNameGroup.getSelectedToggle().setSelected(false);
		}
		
		// If a toggle is selected - then clear it
		if(lastNameGroup.getSelectedToggle() != null) {
			lastNameGroup.getSelectedToggle().setSelected(false);
		}
	}
	// code modified from: https://stackoverflow.com/questions/47757368/javafx-radio-buttons-inside-listview-selectionmodel?rq=1
	private class RadioListCell extends ListCell<RadioButton>
	{

		@Override
		public void updateItem(RadioButton obj, boolean empty)
		{
			super.updateItem(obj, empty);
			if (empty)
			{
				setText(null);
				setGraphic(null);
			}
			else
			{
				setGraphic(obj);
			}
		}

	}

}
