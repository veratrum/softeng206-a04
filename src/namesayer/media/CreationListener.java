package namesayer.media;

import namesayer.Creation;

public interface CreationListener {

	public void creationFinished(Creation creation, boolean newRecording, boolean isDatabaseView);
}
