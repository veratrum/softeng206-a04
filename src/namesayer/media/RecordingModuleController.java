package namesayer.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import namesayer.Creation;
import wav.WavFile;

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
	
	private Creation creation;
	private boolean isRecording;
	
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
	
	public void recordClicked() {
		setRecordingState(!isRecording);
		
		if (isRecording) {
			recordAudio();
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
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Thread record = new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					final AudioFormat format = getAudioFormat();
					DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
					final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(format);
					line.start();
					
					int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
					byte buffer[] = new byte[bufferSize];
					
					while (isRecording) {
						int count = line.read(buffer, 0, buffer.length);
						if (count > 0) {
							out.write(buffer, 0, count);
						}
					}
				
					line.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void done() {

				//WavFile wavFile = WavFile.newWavFile(new File(""), 2, dataLine.get, 16, sampleRate);
				
				FileOutputStream file;
				try {
					file = new FileOutputStream("test.wav");
					out.writeTo(file);
					out.close();
					file.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		record.start();
	}
	
	private AudioFormat getAudioFormat() {
		/*float sampleRate = 8000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;*/
		float sampleRate = 48000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
