package namesayer;

import java.util.List;

public interface ImportListener {

	/**
	 * Returns all names separated by newlines, commas, spaces, and hyphens.
	 */
	public void importFinished(List<String> names);
	
	/**
	 * Returns all names separated by commas, spaces, and hyphens. Names are organised by their line.
	 */
	public void importFinishedSorted(List<List<String>> names);
	
	/**
	 * In some cases we want to check that the names are valid and exist in the database.
	 * If false is returned, the user can submit again after.
	 */
	public boolean checkNamesBeforeSubmit(List<String> names);
}
