package namesayer;

import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;


public class ProgressScreenController extends CustomController {

	@FXML
	LineChart<String, Integer> progressGraph;
	

	private boolean progressIsUpdated = false;
	
	/**
	 * A method which is called when the return to main screen button is clicked - returns the user to the main screen
	 */

	public void GoToMainScreen(ActionEvent event) {
		mainListener.goMain();
		progressIsUpdated = false;
		progressGraph.getData().clear();
		
	}

	public void updateProgress() {
		List<String> allUserRatings = progress.getRecentRatings();

		// We need to create a background task that gets the progress of the user and creates a data series of it.
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// Creating the dataSeries to populate the bar Chart
				XYChart.Series<String, Integer> dataSeries = new XYChart.Series<String, Integer>();
				dataSeries.setName("User Performance");


				// we need to get the data to populate the bar graph
				for (int i = 0; i < allUserRatings.size(); i++) {
					dataSeries.getData().add(new XYChart.Data<String, Integer>(Integer.toString(i), Integer.parseInt(allUserRatings.get(i))));
				}


				// Now we need to update the GUI bar chart from a thread that is allowed to do this
				new Thread(new Runnable() {
					@Override public void run() {
						Platform.runLater(new Runnable() {
							@Override public void run() {

								if (progressIsUpdated == false) {					
									progressGraph.getData().add(dataSeries);
									progressIsUpdated = true;
								}
							}
						});

					}
				}).start();
				return null;
			}
		};
		new Thread(task).start();
	}
}
