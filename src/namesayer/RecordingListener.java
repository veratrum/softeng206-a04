package namesayer;

import java.io.File;

public interface RecordingListener {

	public void recordingFinished(File recording, Creation creation, DatabaseLocation location);
}
