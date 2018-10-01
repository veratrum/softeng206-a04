package namesayer;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenChanger {


	// A function which given an ActionEvent and a FXML file changes the window which generated 
	// the action event to display the view of the FXML file.
	public void ChangeScreen(String FXMLfile, ActionEvent event) throws IOException {

		Parent manageRentals = FXMLLoader.load(getClass().getClassLoader().getResource(FXMLfile));
		Scene manageRentalsScene = new Scene(manageRentals);

		//Get the Stage information
		Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

		window.setScene(manageRentalsScene);
		window.show();
	}


}
