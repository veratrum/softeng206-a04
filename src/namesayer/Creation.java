package namesayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Creation {

	private String name;
	private List<Recording> recordings;
	private DatabaseLocation location;
	
	public Creation(String name, DatabaseLocation location) {
		this.name = name;
		this.recordings = new ArrayList<Recording>();
		this.location = location;
	}
	
	public void delete() {
		for (int i = 0; i < recordings.size(); i++) {
			Recording recording = recordings.get(i);
			
			recording.delete();
		}
		
		while (recordings.size() > 0) {
			removeRecording(recordings.get(0));
		}
	}

	public void removeRecording(Recording recording) {
		recordings.remove(recording);
	}
	
	public String getName() {
		return name;
	}
	
	public void addRecording(Recording recording) {
		recordings.add(recording);
	}
	
	public List<Recording> getRecordings() {
		return recordings;
	}
	
	public Recording getRandomGoodRecording() {
		
		ArrayList<Recording> goodRecordingsList = new ArrayList<Recording>();
		
		// Finding all the recordings with a good rating.
		for (Recording recording : recordings) {
			if (recording.isBad() == false) {
				goodRecordingsList.add(recording);
			}
		}
		Collections.shuffle(goodRecordingsList);
		
		// If there are no good recordings just pick a random bad recording and we will return it.
		if (goodRecordingsList.size() == 0) {
			
			// Create a temp array of recordings 
			List<Recording> tempRecordingsList = recordings;
			Collections.shuffle(tempRecordingsList);
			
			// Return a random bad recording
			return tempRecordingsList.get(0);
		}
		
		// Return a random good recording.
		return goodRecordingsList.get(0);
	}
	
	@Override
	public String toString() {
		String fullName = name;
		
		switch (location) {
		case DATABASE:
			break;
		case USER_DATABASE:
			fullName = "[USER] " + name;
			break;
		default:
			break;
		}
		
		return fullName;
	}
}
