package namesayer;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class PracticeScreenController extends CustomController {
	
	@FXML
	private ListView<Creation> firstNameList;
	@FXML
	private ListView<Recording> lastNameList;
	
	private Creation selectedCreation;
	private Recording selectedRecording;
	
}
