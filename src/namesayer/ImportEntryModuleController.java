package namesayer;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ImportEntryModuleController extends CustomController {

	@FXML
	private TextArea importText;
	
	private ImportListener importListener;
	
	public void setImportListener(ImportListener listener) {
		this.importListener = listener;
	}
	
	public void okClicked() {
		String input = importText.getText();
		
		// https://stackoverflow.com/questions/454908/split-java-string-by-new-line
		String[] newlines = input.split("\\r?\\n");
		String[] spaces = new String[0];
		for (String newline: newlines) {
			// split by space
			String[] fragment = newline.split(" ");
			
			spaces = Utils.concatenate(spaces, fragment);
		}

		String[] commas = new String[0];
		for (String space: spaces) {
			// split by comma
			String[] fragment = space.split(",");
			
			commas = Utils.concatenate(commas, fragment);
		}
		
		List<String> names = new ArrayList<String>();
		for (String name: commas) {
			if (!Creations.isValidName(name)) {
				continue;
			}
			
			names.add(name);
		}
		
		importListener.importFinished(names);
		
		closeWindow();
	}
	
	public void cancelClicked() {
		closeWindow();
	}
	
	private void closeWindow() {
		// adapted from https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Stage stage = (Stage) importText.getScene().getWindow();
				stage.close();
			}
		});
	}
}
