package namesayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HelpScreenController extends CustomController {
	
	@FXML
	TextArea helpText;
	
	/**
	 * A method which is called when the return to main screen button is clicked - returns the user to the main screen
	 */
	public void GoToMainScreen(ActionEvent event) {
		mainListener.goMain();
	}
	
	@Override
	public void init() {
		helpText.setText("\n" + 
				"There are three different screens for the Namesayer application\n" + 
				"\n" + 
				"1) The Practice Screen\n" + 
				"\n" + 
				"	The practice screen is used to practice your playlist of names. To add names \n" + 
				"	to a playlist you click your selection of first and last names and then click \n" + 
				"	the \"Add to Playlist Button\" \n" + 
				"\n" + 
				"	If you wish to clear your selected names you need to press the \"Clear Name \n" + 
				"	Selection\" button.\n" + 
				"\n" + 
				"	Once you have added the names you wish to practice to your playlist you can \n" + 
				"	play the playlist by clicking the \"Play All\" button, if there are multiple names\n" + 
				"	in the playlist you will be given the option to shuffle the playback order. After \n" + 
				"	it plays back the playlist you will be asked for a rating of how you well you \n" + 
				"	think you pronounced the names, this will be used to track your progress in the\n" + 
				" 	Progress Screen.\n" + 
				"\n" + 
				"	You can also click on a single name in the playlist and press the \"Play Selected\"\n" + 
				"	button to play it or the \"Remove\" button to remove the name from the playlist.\n" + 
				"\n" + 
				"	The back button returns you to the main screen of Namesayer.\n" + 
				"\n" + 
				"2) The Recordings Manager Screen\n" + 
				"	\n" + 
				"	The Recording manager screen displays a list of names. You can either add a \n" + 
				"	new name or delete a name from the list of names by pressing the \n" + 
				"	corresponding buttons below the list.\n" + 
				"\n" + 
				"	The Recordings list displays the current recordings for the selected name from \n" + 
				"	the names list. You can rate a recording as Good/Bad (this will prevent it from \n" + 
				"	being played in your playlist if there are good recordings available). Or you\n" + 
				" 	can delete or add a new recording.\n" + 
				"\n" + 
				"	The back button returns you to the main screen of Namesayer.\n" + 
				"\n" + 
				"3) The Progress Screen\n" + 
				"	\n" + 
				"	The progress screen displays a line graph of your 10 most recent self ratings after\n" + 
				"	you have practiced the playlist you have created so that you can track how well \n" + 
				"	you are improving at pronouncing the names.\n" + 
				"\n" + 
				"	The back button returns you to the main screen of Namesayer.");
	}
}
