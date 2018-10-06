package namesayer.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import namesayer.Creation;
import namesayer.Creations;

/**
 * Audio recording code adapted from:
 * https://www.java-tips.org/java-se-tips-100019/120-javax-sound/917-capturing-audio-with-java-sound-api.html
 *
 */
public class RecordingModuleController implements Initializable {

	@FXML
	private Button recordButton;
	@FXML
	private ProgressBar microphoneLevel;
	@FXML
	private Label recordingText;

	private ImageView recordImageView;

	private Creations creations;
	private Creation creation;
	private volatile boolean isRecording;

	private TargetDataLine line;
	
	private RecordingListener recordingListener;

	public RecordingModuleController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void init() {
		recordImageView = new ImageView();
		recordButton.setGraphic(recordImageView);

		setRecordingState(false);
	}

	public void setCreations(Creations creations) {
		this.creations = creations;
	}

	public void setCreation(Creation creation) {
		this.creation = creation;

		recordingText.setText("Press the button below and say the name " + creation.getName() + ".");
	}
	
	public void setRecordingListener(RecordingListener listener) {
		this.recordingListener = listener;
	}

	public void recordClicked() {
		setRecordingState(!isRecording);

		if (isRecording) {
			recordAudio();
		} else {

		}
	}

	public void testMicrophone() {

	}

	private void setRecordingState(boolean recording) {
		this.isRecording = recording;

		Image buttonImage;
		if (isRecording) {
			buttonImage = new Image("file:img" + File.separator + "recording.png");
		} else {
			buttonImage = new Image("file:img" + File.separator + "record.png");
		}

		recordImageView.setImage(buttonImage);
	}

	private void recordAudio() {
		try {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			String filename = "userdata" + File.separator +
					creations.generateRecordingFilename(creation.getName());
			File outputFile = new File(filename);

			/*
			 * having two threads might seem a bit unintuitive
			 * however, if either thread is removed, the application hangs after pressing the record button
			 * this is because AudioSystem.write blocks the main thread until the AudioInputStream is closed
			 */
			Thread record = new Thread(new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					AudioInputStream ais = new AudioInputStream(line);
					AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
					
					return null;
				}
			});

			Thread stopRecording = new Thread(new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					try {
						while (isRecording) {
							Thread.sleep(100);
						}

						line.stop();
						line.close();
						
						recordingListener.recordingFinished(outputFile, creation);
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			});


			stopRecording.start();
			record.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AudioFormat getAudioFormat() {
		/*float sampleRate = 8000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;*/
		float sampleRate = 44100;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
