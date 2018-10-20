package namesayer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * from https://stackoverflow.com/a/80503
	 */
	public static <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	/**
	 * from https://rosettacode.org/wiki/Multisplit#Java
	 */
	public static List<String> multisplit(String txt, String[] separators) {
		List<String> result = new ArrayList<>();
		int txtLen = txt.length(), from = 0;

		for (int to = 0; to < txtLen; to++) {
			for (String sep : separators) {
				int sepLen = sep.length();
				if (txt.regionMatches(to, sep, 0, sepLen)) {
					result.add(txt.substring(from, to));
					from = to + sepLen;
					to = from - 1; // compensate for the increment
					break;
				}
			}
		}
		if (from < txtLen)
			result.add(txt.substring(from));
		return result;
	}

	/**
	 * from https://stackoverflow.com/a/326440
	 */
	public static String readFile(File file, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(file.toPath());
		return new String(encoded, encoding);
	}
	
	public static String getDateFilenameFragment() {
		LocalDateTime now = LocalDateTime.now();
		
		return now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "-"
				+ padLeft(now.getHour() + "", 2, "0") + padLeft(now.getMinute() + "", 2, "0");
	}
}
