package namesayer.controller;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.DatabaseLocation;
import namesayer.PlaylistListener;
import namesayer.Recording;
import namesayer.RecordingListener;

public class PracticeScreenController extends CustomController implements RecordingListener { // I need to do some checking to see that the name exists in the database!!!!!!!!!!! - or else ask the user to add it!!!!

	@FXML
	Text previousName;

	@FXML
	Text nextName;

	@FXML 
	Text currentName;

	@FXML
	Button nextButton;

	@FXML
	Button previousButton;

	@FXML
	Button playAttempt;

	private File currentRecordingFile;
	private BasicMediaPlayerController mediaPlayerController;
	private boolean isPlaylistFinished;

	private RecordingModuleController recordingController;
	private File lastRecording;
	

	// We need some counter to know how far we are through the playlist
	private int playlistPositionCounter  = 0;

	private PlaylistListener playlistListener;

	// fields used for user feedback to track their progress
	private Optional<String> userRating;

	@Override
	public void init() {
		initMediaPlayer();
	}

	@Override
	public void load() {
		playlistPositionCounter = 0;
		doGenerateAudio();
		setPreviousAndNextAndCurrentNames();
		setPreviousAndNextButtonProperties();
		previousButton.setDisable(true);
		playAttempt.setDisable(true);
		
	}




	

	// ======================= Below are the event handlers to this controller class ====================================

	public void returnToHome() {
		mainListener.goPlaylist();
	}

	private void initMediaPlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/MediaPlayerPaneBasic.fxml"));
			Pane mediaPlayerPane = loader.load();

			mediaPlayerPane.setLayoutX(165);
			mediaPlayerPane.setLayoutY(146);

			rootPane.getChildren().add(mediaPlayerPane);

			mediaPlayerController = loader.getController();
		} catch (IOException e) {

		}
	}

	public void nextName() {

		// We need to delete the current audio file
		File audioToDelete = new File("Playlist/" + playlistData.get(playlistPositionCounter) + ".wav");
		audioToDelete.delete();

		// We need to update the counter and generate the audio file
		if(playlistPositionCounter != playlistData.size() - 1) {
			playlistPositionCounter++;
		}

		// If playlist is finished then the button should open the progress dialog box and return user to main screen.
		if (isPlaylistFinished) {
			askUserForRating();
			playlistListener.playlistFinished();
			playlistData.clear();
			mainListener.goMain();
			
		}
		else {
			setPreviousAndNextAndCurrentNames();
			doGenerateAudio();
		}

		setPreviousAndNextButtonProperties();

	}

	public void previousName() {
		// We need to delete the current audio file
		File audioToDelete = new File("Playlist/" + playlistData.get(playlistPositionCounter) + ".wav");
		audioToDelete.delete();

		// We need to update the counter and generate the audio file
		if(playlistPositionCounter != 0) {
			playlistPositionCounter--;
		}
		setPreviousAndNextAndCurrentNames();
		doGenerateAudio();

		setPreviousAndNextButtonProperties();
	}

	public void recordAttempt() {
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
			// use dummy creation to pass name
			recordingController.setCreation(new Creation(currentRecordingFile.getName().replaceFirst("[.][^.]+$", ""),
					DatabaseLocation.TEMP));
			recordingController.setRecordingListener(this);
			recordingController.setSaveLocation(DatabaseLocation.TEMP);

			recordingStage.setScene(recordingScene);
			recordingStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listenAttempt() {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(lastRecording));
			clip.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	private void doGenerateAudio() {
		// Creating a background task to create the audioFile for the name and play it
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				createAudioFileForName(); // DONT need to do this every time !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!+++++++++++++++++++++++++++++++++++!!!!!!!!!!!!!!!!

				return null;
			}

			@Override
			protected void done() {
				Platform.runLater(new Task<Void>() {
					@Override
					public Void call() {
						mediaPlayerController.setRecording(currentRecordingFile);
						mediaPlayerController.setButtonState(true);

						return null;
					}
				});
			}
		};
		new Thread(task).start();
	}

	public void createAudioFileForName() {

		String currentName = playlistData.get(playlistPositionCounter);

		// Now we need to parse the name to see how many recordings we need to merge together.
		String[] currentNameParsed = currentName.split("[-\\s]");

		mergeAudioFiles(currentNameParsed, currentName);

	}

	public void mergeAudioFiles(String[] parsedName, String currentName) {

		createPlaylistDirectory();

		try {

			Recording firstRecording;
			Recording secondRecording;

			AudioInputStream clip1;
			AudioInputStream clip2;
			AudioInputStream appendedFiles = null;
			File tempFile = null;

			// If there is only one file then just return that audio file.
			if(parsedName.length == 1) {
				currentRecordingFile = new File("Playlist/" + currentName + ".wav");

				Creation selectedCreation = null;
				if (creations.creationExists(parsedName[0])) {
					selectedCreation = creations.getCreationByName(parsedName[0]);
				} else if (userCreations.creationExists(parsedName[0])) {
					selectedCreation = userCreations.getCreationByName(parsedName[0]);
				} else {
					// a non-existent creation slipped past the checks. not good
				}
				
				clip1 = AudioSystem.getAudioInputStream(new File(selectedCreation.getRandomGoodRecording().getFile().toString()));
				AudioSystem.write(clip1 ,AudioFileFormat.Type.WAVE, currentRecordingFile);

			}
			else { // Otherwise Iterating over the parsed name to create the audioFile.

				for (int i = 0; i < parsedName.length - 1; i++) {
					Creation firstCreation = null;
					if (creations.creationExists(parsedName[i])) {
						firstCreation = creations.getCreationByName(parsedName[i]);
					} else if (userCreations.creationExists(parsedName[i])) {
						firstCreation = userCreations.getCreationByName(parsedName[i]);
					} else {
						// a non-existent creation slipped past the checks. not good
					}

					if (i == 0) { // first iteration so we need to get two recordings.
						firstRecording = firstCreation.getRandomGoodRecording();
						clip1 = AudioSystem.getAudioInputStream(new File(firstRecording.getFile().toString()));
					}
					else { // its not the first iteration so the first recording is the output of the last iteration
						clip1 = AudioSystem.getAudioInputStream(new File("Playlist/temp.wav"));
					}

					Creation secondCreation = null;
					if (creations.creationExists(parsedName[i + 1])) {
						secondCreation = creations.getCreationByName(parsedName[i + 1]);
					} else if (userCreations.creationExists(parsedName[i + 1])) {
						secondCreation = userCreations.getCreationByName(parsedName[i + 1]);
					} else {
						// a non-existent creation slipped past the checks. not good
					}
					
					secondRecording = secondCreation.getRandomGoodRecording();
					clip2 = AudioSystem.getAudioInputStream(new File(secondRecording.getFile().toString()));



					// Merging the audio files from clip1 and clip2.
					appendedFiles = new AudioInputStream(new SequenceInputStream(clip1, clip2),clip1.getFormat(),clip1.getFrameLength() + clip2.getFrameLength());

					if(i == parsedName.length - 2) { // then this is the last iteration so we need to save the file.
						currentRecordingFile = new File("Playlist/" + currentName + ".wav");
						AudioSystem.write(appendedFiles,AudioFileFormat.Type.WAVE, currentRecordingFile);
					}
					else { // save it as a temporary file
						tempFile = new File("Playlist/temp.wav");
						AudioSystem.write(appendedFiles,AudioFileFormat.Type.WAVE, tempFile);
					}
				}

				if(tempFile != null) {
					tempFile.delete();
				}
			}
		}
		catch(Exception e) {
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

	public void setPreviousAndNextAndCurrentNames() {

		if (playlistPositionCounter != playlistData.size() - 1) {
			nextName.setText(playlistData.get(playlistPositionCounter + 1).toString());
		}
		else {
			nextName.setText("No next name");
		}


		// Setting the previousName textField
		if (playlistPositionCounter != 0) {
			previousName.setText(playlistData.get(playlistPositionCounter - 1).toString());
		}
		else {
			previousName.setText("No previous Name");
		}

		currentName.setText(playlistData.get(playlistPositionCounter).toString());

	}

	/**
	 * This method sets the properties of the next and previous button as we scroll through
	 * the playlist. ie it sets them to be disabled/enabled and changes their text when 
	 * needed. It also sets the value of the text field beneath the buttons to be the 
	 * name of the next/previous names in the playlist. 
	 */
	public void setPreviousAndNextButtonProperties() {
		// Now we need to set the buttons to be enabled/disabled if needed
		if (playlistPositionCounter == playlistData.size() - 1) {
			nextButton.setText("Finish");
			nextName.setVisible(false);
			isPlaylistFinished = true;
		}
		else {
			nextButton.setDisable(false);
			nextButton.setText("Next");
			nextName.setVisible(true);
			isPlaylistFinished = false;
		}

		if (playlistPositionCounter == 0) {
			previousButton.setDisable(true);
		}
		else {
			previousButton.setDisable(false);
		}
		
		playAttempt.setDisable(true);
	}

	/**
	 * This method is used to get the users rating on how good they beleive their performance for the last playlist was
	 */
	public void askUserForRating() {

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
			progress.addRating(userRating.get());
		}
	}

	@Override
	public void recordingFinished(File recording, Creation creation, DatabaseLocation location) {
		lastRecording = recording;

		playAttempt.setDisable(false);
	}
	
	public void addPlaylistListener(PlaylistListener playlistListener) {
		this.playlistListener= playlistListener;
	}
}
