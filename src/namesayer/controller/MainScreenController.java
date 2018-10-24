package namesayer.controller;

import java.io.IOException;

import javafx.event.ActionEvent;

public class MainScreenController extends CustomController {
	//Below are the action handlers that change the screen view of the application.

	public void GoToPracticeScreen(ActionEvent event) throws IOException {
		mainListener.goPlaylist();
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

	public void GoToImportScreen(ActionEvent event) {
		mainListener.goImport();
	}

	public void exit() {
		System.exit(0);
	}

}
