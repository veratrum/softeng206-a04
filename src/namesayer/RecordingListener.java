package namesayer;

import java.io.File;

public interface RecordingListener {

	/**
	 * @param recording The location of the recorded audio.
	 * @param creation The creation the recording belongs to. Can be null if the recording is temporary.
	 * @param location Whether the recording is stored in the database, user database, or is a temporary recording.
	 */
	public void recordingFinished(File recording, Creation creation, DatabaseLocation location);
}
