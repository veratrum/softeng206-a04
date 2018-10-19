package namesayer.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import namesayer.Creation;
import namesayer.ImportListener;
import namesayer.Recording;
import namesayer.Utils;
import javafx.stage.Stage;

public class ImportScreenController extends CustomController implements ImportListener {

	private File database;
	
	public ImportScreenController() {
		this.database = new File("database");
	}
	
	/**
	 * Packages every indexed file in the database directory into a zip file along with metadata.xml.
	 * Prompts the user to save it on their computer.
	 */
	public void saveDatabase() {
		// all indexed recordings. there could be others in the database directory, but we don't care about them
		List<Recording> allRecordings = creations.getAllRecordings();

		List<File> allFiles = new ArrayList<File>();

		allFiles.add(new File(database, "metadata.xml"));
		for (Recording recording: allRecordings) {
			allFiles.add(recording.getFile());
		}

		/* allow the user to choose where to save the file
		modified from https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm */
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Database");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		String defaultZipFilename = generateExportZipFilename();
		fileChooser.setInitialFileName(defaultZipFilename);

		File saveLocation = fileChooser.showSaveDialog(scene.getWindow());

		// user closed save dialog
		if (saveLocation == null) {
			return;
		}

		/* modified from
		https://examples.javacodegeeks.com/core-java/util/zip/create-zip-file-from-directory-with-zipoutputstream/ */
		try {
			FileOutputStream fos = new FileOutputStream(saveLocation);
			ZipOutputStream zos = new ZipOutputStream(fos);

			byte[] buffer = new byte[1024];

			for (File file: allFiles) {
				FileInputStream fis = new FileInputStream(file);

				zos.putNextEntry(new ZipEntry(file.getName()));

				int length;
				// read the file 1024 bytes at a time
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();
				fis.close();
			}

			zos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Save Database");
		alert.setHeaderText(null);
		alert.setContentText("Saved database successfully.");

		alert.show();
	}

	/**
	 * Loads a packaged archive saved previously using saveDatabase.
	 * Overwrites the existing data. (is this actually how it should work. idk)
	 * Adapted from https://stackoverflow.com/questions/40050270/java-unzip-and-progress-bar
	 */
	public void loadDatabase() {

		// ask the user to find the database they want to load
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Database");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Zip Archives", "*.zip"));

		File loadLocation = fileChooser.showOpenDialog(scene.getWindow());
		
		// user closed dialog
		if (loadLocation == null) {
			return;
		}
		
		database.mkdir();

		doClearDatabase();
		
		// delete the zero-node metadata created by doClearDatabase()
		File metadata = new File(database, "metadata.xml");
		if (metadata.exists()) {
			metadata.delete();
		}

		boolean successful = true;
		
		try {
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(new FileInputStream(loadLocation)));
			ZipEntry entry = null;
			try {
				while ((entry = zis.getNextEntry()) != null) {
					File file = new File(database, entry.getName());
					OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
					try {
						try {
							final byte[] buf = new byte[1024];
							int bytesRead;

							while (-1 != (bytesRead = zis.read(buf))){
								fos.write(buf, 0, bytesRead);
							}
						} finally {
							fos.close();
						}
					} catch (final IOException e) {
						file.delete();
						successful = false;
						
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				successful = false;
				e.printStackTrace();
			} finally {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			successful = false;
			
			e.printStackTrace();
		}
		
		if (!successful) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Importing Database");
			alert.setHeaderText("Could not import database.");
			alert.setContentText("Perhaps you did something wrong.");

			alert.show();
		} else {
			creations.reloadData();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Import Database");
			alert.setHeaderText(null);
			alert.setContentText("Imported database successfully.");

			alert.show();
		}
	}

	/**
	 * Deletes all Name/Recording/Progress data from the application.
	 * Deletes all files associated with this data e.g. recording files.
	 */
	public void clearDatabase() {
		doClearDatabase();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Clear Database");
		alert.setHeaderText(null);
		alert.setContentText("Database cleared successfully.");

		alert.showAndWait();
	}
	
	private void doClearDatabase() {
		// i have no idea why this works
		System.gc();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.gc();
		
		File metadata = new File(database, "metadata.xml");
		metadata.delete();
		File ratings = new File(database, "ratings.txt");
		ratings.delete();

		creations.deleteAll();
	}

	/**
	 * Imports empty names from a text list e.g. class roll.
	 * First/last names are indiscriminately added to the database.
	 * They can be separated by commas, spaces, newlines, doesn't matter.
	 * Overwrites the existing data. (is this actually how it should work. idk)
	 */
	public void importDatabase() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/ImportEntryModule.fxml"));
			Pane importModulePane = loader.load();
			
			Scene importScene = new Scene(importModulePane, 400, 300);
			
			ImportEntryModuleController controller = loader.getController();
			controller.setScene(importScene);
			controller.setImportListener(this);
			
			Stage importModule = new Stage();
			importModule.setScene(importScene);
			importModule.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void goMain() {
		mainListener.goMain();
	}

	private String generateExportZipFilename() {
		return "namesayer_export_" + Utils.getDateFilenameFragment() + ".zip";
	}

	@Override
	public void importFinished(List<String> names) {
		doClearDatabase();
		
		for (String name: names) {
			Creation creation = new Creation(name);
			creations.addCreationWithoutSaving(creation);
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Import Database From Text");
		alert.setHeaderText(null);
		alert.setContentText("Database imported successfully.");

		alert.showAndWait();
	}

	@Override
	public void importFinishedSorted(List<List<String>> names) {
		
	}
}
