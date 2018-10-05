package namesayer.media;

import java.io.File;

import namesayer.Creation;

public interface RecordingListener {

	public void recordingFinished(File recording, Creation creation);
}
