package namesayer;

public class Utils {

	/**
	 * modified from
	 * https://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java
	 * 
	 * doesn't work if `s` contains spaces though
	 */
	public static String padRight(String s, int n, String padCharacter) {
		return String.format("%1$-" + n + "s", s).replace(" ", padCharacter);
	}

	public static String padLeft(String s, int n, String padCharacter) {
		return String.format("%1$" + n + "s", s).replace(" ", padCharacter);  
	}
}
