package namesayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Creations {

	private List<Creation> creations;
	private CreationLoader creationLoader;
	private DatabaseLocation location;
	
	public Creations(DatabaseLocation location, String directory, String xmlFile) {
		creations = new ArrayList<Creation>();
		this.location = location;
		creationLoader = new CreationLoader(this, this.location, directory, xmlFile);

		loadData();
	}

	/**
	 * Called whenever the database has been loaded/imported externally i.e. not at the start of
	 * Namesayer's execution
	 */
	public void reloadData() {
		loadData();
		creationLoader.saveSeparateRatingsFile();
	}
	
	private void loadData() {
		creationLoader.loadMetadata();
	}
	
	public void addCreation(Creation creation) {
		this.creations.add(creation);
		
		sortCreations();
		
		creationLoader.saveMetadata();
	}
	
	/**
	 * This method should be called only by CreationLoader, instead of addCreation.
	 * We don't want to overwrite metadata.xml with incomplete data while we are reading from it.
	 */
	public void addCreationWithoutSaving(Creation creation) {
		this.creations.add(creation);
		
		sortCreations();
	}
	
	public void deleteCreation(Creation creation) {
		creations.remove(creation);
		
		sortCreations();
		
		creationLoader.saveMetadata();
	}
	
	public void deleteAll() {
		for (int i = 0; i < creations.size(); i++) {
			Creation creation = creations.get(i);
			
			creation.delete();
		}
		
		creations = new ArrayList<Creation>();
		
		creationLoader.saveMetadata();
	}
	
	public void saveState() {
		creationLoader.saveMetadata();
	}
	
	public List<Creation> getCreations() {
		return creations;
	}
	
	public List<Recording> getAllRecordings() {
		List<Recording> allRecordings = new ArrayList<Recording>();
		
		for (Creation creation: creations) {
			allRecordings.addAll(creation.getRecordings());
		}
		
		return allRecordings;
	}
	
	public boolean creationExists(String name) {
		for (Creation creation: creations) {
			if (creation.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Creation getCreationByName(String name) {
		for (Creation creation: creations) {
			if (creation.getName().equals(name)) {
				return creation;
			}
		}
		
		return null;
	}
	
	public boolean recordingExists(String filename) {
		for (Creation creation: creations) {
			for (Recording recording: creation.getRecordings()) {
				if (recording.getFile().getName().equals(filename)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String generateRecordingFilename(String creationName) {
		String filename = creationName + ".wav";
		if (!recordingExists(filename)) {
			return filename;
		}
		
		int tries = 2;
		String repeatName = creationName + " (" + tries + ").wav";
		while (recordingExists(repeatName)) {
			tries++;
			repeatName = creationName + " (" + tries + ").wav";
		}
		
		return repeatName;
	}
	
	public static boolean isValidName(String name) {
		if (name.length() == 0 || name.length() > 32) {
			return false;
		}
		
		if (!Character.isLetter(name.charAt(0)) || !Character.isUpperCase(name.charAt(0))) {
			return false;
		}
		
		for (int i = 0; i < name.length(); i++) {
			char character = name.charAt(i);
			
			if (!Character.isLetter(character) && !(character == '_')) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Sorts all creations in alphabetical order.
	 */
	private void sortCreations() {
		// adapted from https://stackoverflow.com/a/2784576
		Collections.sort(creations, new Comparator<Creation>() {
			@Override
			public int compare(Creation a, Creation b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}
}
