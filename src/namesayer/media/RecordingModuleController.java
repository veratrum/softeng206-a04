package namesayer.media;

import java.io.File;
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
import javafx.stage.Stage;
import namesayer.Creation;
import namesayer.Creations;
import namesayer.CustomController;

/**
 * Audio recording code adapted from:
 * https://www.java-tips.org/java-se-tips-100019/120-javax-sound/917-capturing-audio-with-java-sound-api.html
 *
 *
 * TODO: in final version
 * -- add a media player module on this screen after the user records a name
 * that is only enabled when a recording has been done and isn't currently in progress
 * -- add OK/Cancel buttons to supplement this
 */
public class RecordingModuleController extends CustomController implements Initializable {

	@FXML
	private Button recordButton;
	@FXML
	private ProgressBar microphoneLevel;
	@FXML
	private Label recordingText;

	private ImageView recordImageView;

	private Creation creation;
	private volatile boolean isRecording;
	private boolean isDatabaseView;
	
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

	public void setCreation(Creation creation) {
		this.creation = creation;

		recordingText.setText("Press the button below and say the name " + creation.getName() + ".");
	}
	
	public void setRecordingListener(RecordingListener listener) {
		this.recordingListener = listener;
	}
	
	public void setIsDatabaseView(boolean isDatabaseView) {
		this.isDatabaseView = isDatabaseView;
	}

	public void recordClicked() {
		setRecordingState(!isRecording);

		if (isRecording) {
			recordAudio();
		} else {

		}
	}

	public void testMicrophone() {
		Thread sampleAudio = new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				// modified from http://proteo.me.uk/2009/10/sound-level-monitoring-in-java/
				double micSum = 0.0;
				AudioFormat audioFormat = getAudioFormat();

				TargetDataLine targetDataLine;
				try {
					targetDataLine = (TargetDataLine) AudioSystem.getTargetDataLine(audioFormat);

					// Setting up the targetDataLine
					targetDataLine.open();
					targetDataLine.start();


					//byte [] buffer = new byte[2000];
					// using a larger buffer since the sample rate has increased from assignment 3...
					byte [] buffer = new byte[10000];
					for (int i=0; i<25; i++) {
						int bytesRead = targetDataLine.read(buffer,0,buffer.length);

						short max;
						if (bytesRead >=0) {
							max = (short) (buffer[0] + (buffer[1] << 8));
							for (int p=2;p<bytesRead-1;p+=2) {
								short thisValue = (short) (buffer[p] + (buffer[p+1] << 8));
								if (thisValue>max) max=thisValue;
							}
							if(max >= 0 ) {
								double micLevel = max/1000.0; //Note: this calculation for mic volume test is based on an approximation of what I determined to be low/average/loud speaking volume.
								if (micLevel > 1.0) {
									micLevel = 1.0;
								}
								micSum = micSum + micLevel;
							}
						}
					}

					targetDataLine.close();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

				// Setting the progressBar mic Input level
				microphoneLevel.setProgress(micSum/25);

				return null;
			}
		});
		sampleAudio.start();

		// Create a new background thread to reset the mic volume meter after 5 seconds.
		Thread resetMeter = new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Thread.sleep(5000);
				microphoneLevel.setProgress(0.0);

				return null;
			}
		});
		resetMeter.start();
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

			File newFile = null;
			if (isDatabaseView) {
				newFile = new File(new File("database"), creations.generateRecordingFilename(creation.getName()));
			} else {
				newFile = new File(new File("userdata"), userCreations.generateRecordingFilename(creation.getName()));
			}
			
			File outputFile = newFile;

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
						
						recordingListener.recordingFinished(outputFile, creation, isDatabaseView);
						
						closeWindow();
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
	
	private void closeWindow() {
		// adapted from https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage stage = (Stage) recordButton.getScene().getWindow();
				stage.close();
			}
		});
	}
}
