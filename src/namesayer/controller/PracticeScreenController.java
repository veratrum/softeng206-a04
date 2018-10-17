package namesayer.controller;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import namesayer.Recording;



public class PracticeScreenController extends CustomController { // I need to do some checking to see that the name exists in the database!!!!!!!!!!! - or else ask the user to add it!!!!

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

	private File currentRecordingFile;

	private BasicMediaPlayerController mediaPlayerController;

	@Override
	public void init() {
		initMediaPlayer();
	}

	@Override
	public void load() {
		doGenerateAudio();
		setPreviousAndNextAndCurrentNames();
		previousButton.setDisable(true);
	}

	// We need some counter to know how far we are through the playlist
	private int playlistPositionCounter  = 0;
	private boolean needToCreateAudioFile = true;


	// ======================= Below are the event handelers to this controller class ====================================


	public void returnToHome() {
		mainListener.goMain();
	}

	private void initMediaPlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/MediaPlayerPaneBasic.fxml"));
			Pane mediaPlayerPane = loader.load();

			mediaPlayerPane.setLayoutX(173);
			mediaPlayerPane.setLayoutY(146);

			rootPane.getChildren().add(mediaPlayerPane);

			mediaPlayerController = loader.getController();
		} catch (IOException e) {

		}
	}

	public void nextName() {

	}

	public void previousName() {

	}

	private void doGenerateAudio() {
		// Creating a background task to create the audioFile for the name and play it
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// Creating the audio File
				if (needToCreateAudioFile) {
					createAudioFileForName(); // DONT need to do this every time !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!+++++++++++++++++++++++++++++++++++!!!!!!!!!!!!!!!!
					needToCreateAudioFile = false;
				}

				return null;
			}

			@Override
			protected void done() {
				Platform.runLater(new Task<Void>() {
					@Override
					public Void call() {
						mediaPlayerController.setRecording(currentRecordingFile);
						
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
				clip1 = AudioSystem.getAudioInputStream(new File(creations.getCreationByName(parsedName[0]).getRandomGoodRecording().getFile().toString()));
				AudioSystem.write(clip1 ,AudioFileFormat.Type.WAVE, currentRecordingFile);

			}
			else { // Otherwise Iterating over the parsed name to create the audioFile.

				for (int i = 0; i < parsedName.length - 1; i++) {

					if (i == 0) { // first iteration so we need to get two recordings.
						firstRecording = creations.getCreationByName(parsedName[i]).getRandomGoodRecording();
						clip1 = AudioSystem.getAudioInputStream(new File(firstRecording.getFile().toString()));
					}
					else { // its not the first iteration so the first recording is the output of the last iteration
						clip1 = AudioSystem.getAudioInputStream(new File("Playlist/temp.wav"));
					}

					secondRecording = creations.getCreationByName(parsedName[i+1]).getRandomGoodRecording();
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
				tempFile.delete();
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
}
