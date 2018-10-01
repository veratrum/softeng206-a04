package namesayer;

import java.io.IOException;

import javafx.event.ActionEvent;

public class MainScreenController {
	
	// A field used to change the screen in the applicaton window.
	private ScreenChanger screenChanger = new ScreenChanger();
	
	//Below are the action handlers that change the screen view of the application.
	
	/**
	 * ActionHandler called that opens a new FXML view in the window which generated the action event.
	 * @throws IOException 
	 */
	public void GoToPracticeScreen(ActionEvent event) throws IOException {
		screenChanger.ChangeScreen("PracticeScreen.fxml", event);
	}
	
}
