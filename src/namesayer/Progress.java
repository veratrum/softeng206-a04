package namesayer;

import java.util.ArrayList;
import java.util.List;

public class Progress {

	private List<String> allUserRatings;

	private ProgressLoader loader;
	
	public Progress() {
		allUserRatings = new ArrayList<String>();
		
		loader = new ProgressLoader(this);
		
		loader.loadProgress();
	}
	
	public void addRating(String rating) {
		allUserRatings.add(rating);
		
		loader.saveProgress();
	}
	
	/**
	 * Use this when adding many ratings at once and saving them repeatedly would be a waste of time.
	 */
	public void addRatingWithoutSaving(String rating) {
		allUserRatings.add(rating);
	}
	
	public List<String> getAllRatings() {
		return allUserRatings;
	}
	
	/**
	 * Returns the 10 most recent ratings.
	 */
	public List<String> getRecentRatings() {
		int size = allUserRatings.size();
		if (size <= 10) {
			return allUserRatings;
		} else {
			List<String> newList = new ArrayList<String>();
			
			for (int i = size - 10; i < size; i++) {
				newList.add(allUserRatings.get(i));
			}
			
			return newList;
		}
	}
}
