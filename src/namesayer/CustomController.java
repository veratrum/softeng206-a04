package namesayer;

/**
 * Basic scene.
 * Has access to Main (for changing scenes), and Creations
 *
 */
public class CustomController {

	protected MainListener mainListener;
	
	protected Creations creations;
	
	public CustomController() {
		
	}
	
	public void init() {
		
	}
	
	public void setMainListener(MainListener listener) {
		this.mainListener = listener;
	}
	
	public void setCreations(Creations creations) {
		this.creations = creations;
	}
	
}
