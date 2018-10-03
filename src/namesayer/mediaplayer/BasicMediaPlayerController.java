package namesayer.mediaplayer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import namesayer.Recording;

public class BasicMediaPlayerController implements Initializable {

	@FXML
	private Button playButton;
	private ImageView buttonImageView;
	private MediaPlayer mediaPlayer;
	private Recording recording;
	
	private boolean isPlaying;
	
	public void playClicked() {
		setButtonState(!isPlaying);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonImageView = new ImageView();
		mediaPlayer = null;
		recording = null;
		
		playButton.setGraphic(buttonImageView);
		
		setButtonState(false);
	}
	
	public void setRecording(Recording recording) {
		this.recording = recording;
		
		if (recording == null) {
			return;
		}
		
		Media media = new Media(recording.getFile().toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}
	
	private void setButtonState(boolean playing) {
		isPlaying = playing;

		Image playImage;
		
		if (isPlaying) {
			playImage = new Image("file:img\\pause.png");
		} else {
			playImage = new Image("file:img\\play.png");
		}
		
		buttonImageView.setImage(playImage);
		
		updatePlayerState();
	}
	
	private void updatePlayerState() {System.out.println(isPlaying);
		if (mediaPlayer == null) {
			return;
		}
		
		if (isPlaying) {
			mediaPlayer.play();
		} else {
			mediaPlayer.pause();
		}
	}
}
