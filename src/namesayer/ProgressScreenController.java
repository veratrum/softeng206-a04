package namesayer;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

public class ProgressScreenController extends CustomController {

	// Only need to add one method for the return to home button
	
	/**
	 * A method which is called when the return to main screen button is clicked - returns the user to the main screen
	 */
	public void GoToMainScreen(ActionEvent event) {
		mainListener.goMain();
	}
	
	public void updateProgress() {

		// We need to create a background task that sets the progress of the user in the progress screen.
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				
				// we need to get the data to populate the bar graph
				//if (allUserRatings.size()) {
					
				//}
				return null;
			}
		};
		new Thread(task).start();
	}
}
