package namesayer;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

public class MainScreenController extends CustomController {
	//Below are the action handlers that change the screen view of the application.
	
	/**
	 * ActionHandler called that opens a new FXML view in the window which generated the action event.
	 * @throws IOException 
	 */
	public void GoToPracticeScreen(ActionEvent event) throws IOException {
		mainListener.goPractice();
	}
	
	public void GoToRecordScreen(ActionEvent event) {
		mainListener.goRecord();
	}
	
	public void GoToProgressScreen(ActionEvent event) {
		mainListener.goProgress();
	}
	
	public void GoToHelpScreen(ActionEvent event) {
		mainListener.goHelp();
	}
	
}
