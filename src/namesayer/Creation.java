package namesayer;

import java.util.ArrayList;
import java.util.List;

public class Creation {

	private String name;
	private List<Recording> recordings;
	
	public Creation(String name) {
		this.name = name;
		this.recordings = new ArrayList<Recording>();
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
	
	@Override
	public String toString() {
		return name;
	}
}
