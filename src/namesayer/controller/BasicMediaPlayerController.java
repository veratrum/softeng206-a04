package namesayer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BasicMediaPlayerController implements Initializable {

	@FXML
	private Button playButton;
	@FXML
	private Label recordingName;
	
	private ImageView buttonImageView;
	private Clip clip;
	private File recording;
	
	private boolean isPlaying;
	
	public void playClicked() {
		if (clip == null || recording == null) {
			return;
		}
		
		setButtonState(!isPlaying);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonImageView = new ImageView();
		clip = null;
		recording = null;
		
		playButton.setGraphic(buttonImageView);
		recordingName.setText("");
		
		setButtonState(false);
	}
	
	public void setRecording(File newRecording) {
		recording = newRecording;
		
		if (recording == null) {
			return;
		}
		
		// stop any existing recording
		setButtonState(false);
		
		recordingName.setText(recording.getName());

		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(recording));
			
			// reset the state of the clip and button when the end of the clip is finished
			clip.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					if (event.getType() == Type.STOP
							&& clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
						setButtonState(false);
						clip.setFramePosition(0);
					}
				}
				
			});
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		setButtonState(false);
	}
	
	private void setButtonState(boolean playing) {
		isPlaying = playing;

		Image playImage;
		
		if (isPlaying) {
			playImage = new Image("file:img" + File.separator + "pause.png");
		} else {
			playImage = new Image("file:img" + File.separator + "play.png");
		}
		
		buttonImageView.setImage(playImage);
		
		updatePlayerState();
	}
	
	private void updatePlayerState() {
		if (clip == null || recording == null) {
			return;
		}
		
		if (isPlaying) {
			clip.start();
		} else {
			clip.stop();
		}
	}
}