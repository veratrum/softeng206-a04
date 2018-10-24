package namesayer.controller;

import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;


public class ProgressScreenController extends CustomController {

	@FXML
	LineChart<Integer, Integer> progressGraph;


	private boolean progressIsUpdated = false;

	/**
	 * A method which is called when the return to main screen button is clicked - returns the user to the main screen
	 */

	@Override
	public void init() {
		ValueAxis<Integer> xAxis = (ValueAxis<Integer>) progressGraph.getXAxis();
		ValueAxis<Integer> yAxis = (ValueAxis<Integer>) progressGraph.getYAxis();

		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(10);
		xAxis.setTickLength(1);

		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(10);
		yAxis.setTickLength(1);
	}

	@Override
	public void load() {
		updateProgress();
	}

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
				XYChart.Series<Integer, Integer> dataSeries = new XYChart.Series<Integer, Integer>();
				dataSeries.setName("User Performance");


				// we need to get the data to populate the bar graph
				for (int i = 0; i < allUserRatings.size(); i++) {
					dataSeries.getData().add(new XYChart.Data<Integer, Integer>(i, Integer.parseInt(allUserRatings.get(i))));
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
