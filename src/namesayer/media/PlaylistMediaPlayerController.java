package namesayer.media;

import java.io.File;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import namesayer.Recording;

public class PlaylistMediaPlayerController extends BasicMediaPlayerController {

	@FXML
	private Button backButton;
	@FXML
	private Button forwardButton;
	
	private List<File> playlist;
	private int currentIndex;
	
	public PlaylistMediaPlayerController() {
		super();
		
		playlist = null;
		currentIndex = 0;

		ImageView backImageView = new ImageView();
		backImageView.setImage(new Image("file:img" + File.separator + "back.png"));
		backButton.setGraphic(backImageView);
		ImageView forwardImageView = new ImageView();
		forwardImageView.setImage(new Image("file:img" + File.separator + "forward.png"));
		forwardButton.setGraphic(forwardImageView);
	}
	
	/**
	 * Sets the playlist of all recordings, in the order they should be played.
	 * The starting recording is by default the first.
	 */
	public void setPlaylist(List<File> playlist) {
		this.playlist = playlist;
		
		currentIndex = 0;
		
		this.setRecording(playlist.get(currentIndex));
	}
	
	public void setSelectedRecording(File selectedRecording) {
		for (int i = 0; i < playlist.size(); i++) {
			if (playlist.get(i).getName() == selectedRecording.getName()) {
				currentIndex = i;
			}
		}
		
		setRecording(playlist.get(currentIndex));
	}
	
	public void backClicked() {
		
	}
	
	public void forwardClicked() {
		
	}
}
