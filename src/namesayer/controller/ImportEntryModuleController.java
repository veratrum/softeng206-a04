package namesayer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import namesayer.Creations;
import namesayer.ImportListener;
import namesayer.Utils;

/**
 * A generic module used in multiple parts of the application.
 * It allows the user to enter text directly into a text box, and submit it.
 * The user can also load a text file which fills in the text box with the file's content.
 * The text box data may be optionally verified by the ImportListener's implementation of checkNamesBeforeSubmit.
 * 
 * The resulting data is returned in two different ways detailed in a comment below.
 * It is optional which of these the ImportListener chooses to use.
 */
public class ImportEntryModuleController extends CustomController {

	@FXML
	private TextArea importText;

	private ImportListener importListener;

	public void setImportListener(ImportListener listener) {
		this.importListener = listener;
	}

	public void okClicked() {
		String input = importText.getText();

		/*
		 * Sorts into two different versions
		 * 1) sorted by newline, i.e. 2-depth Lists
		 * 2) unsorted simple List
		 * 
		 * The first version is used for example to import a playlist, with one multi-name entry on each line.
		 * The second version is to import a database template where individual entries are created in the database.
		 */

		List<List<String>> sortedNamesFinal = new ArrayList<List<String>>();
		List<String> unsortedNamesFinal = new ArrayList<String>();

		List<List<String>> sortedNames = new ArrayList<List<String>>();
		List<String> unsortedNames = new ArrayList<String>();

		String[] newlines = input.split("\\r?\\n");
		for (String newline: newlines) {
			List<String> splitNewline = Utils.multisplit(newline, new String[] {",", " ", "-"});
			sortedNames.add(splitNewline);

			unsortedNames.addAll(splitNewline);
		}

		for (String name: unsortedNames) {
			if (!Creations.isValidName(name)) {
				continue;
			}

			unsortedNamesFinal.add(name);
		}

		for (List<String> newline: sortedNames) {
			List<String> newlineFinal = new ArrayList<String>();

			for (String name: newline) {
				if (!Creations.isValidName(name)) {
					continue;
				}

				newlineFinal.add(name);
			}

			sortedNamesFinal.add(newlineFinal);
		}

		if (importListener.checkNamesBeforeSubmit(unsortedNamesFinal)) {
			importListener.importFinishedSorted(sortedNamesFinal);
			importListener.importFinished(unsortedNamesFinal);

			closeWindow();
		}
	}

	public void cancelClicked() {
		closeWindow();
	}

	public void loadFileClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load list");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Text files", "*.txt"));

		File chosenFile = fileChooser.showOpenDialog(scene.getWindow());

		// user closed dialog
		if (chosenFile == null) {
			return;
		}

		try {
			String text = Utils.readFile(chosenFile, StandardCharsets.UTF_8);

			importText.setText(text);
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Unable to load list.");

			alert.showAndWait();
		}
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
