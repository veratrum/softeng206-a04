package namesayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * TODO
 * prompt user with file picker
 * generate zip asynchronously
 *
 */
public class ImportScreenController extends CustomController {

	/**
	 * Packages every indexed file in the /userdata/ directory into a zip file along with metadata.xml.
	 * Prompts the user to save it on their computer.
	 */
	public void saveDatabase() {
		// all indexed recordings. there could be others in the userdata directory, but we don't care about them
		List<Recording> allRecordings = creations.getAllRecordings();
		
		List<File> allFiles = new ArrayList<File>();
		
		allFiles.add(new File("userdata" + File.separator + "metadata.xml"));
		for (Recording recording: allRecordings) {
			allFiles.add(recording.getFile());
		}
		
		/* modified from
		https://examples.javacodegeeks.com/core-java/util/zip/create-zip-file-from-directory-with-zipoutputstream/*/
		try {
			String zipFilename = generateExportZipFilename();
			FileOutputStream fos = new FileOutputStream(zipFilename);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			byte[] buffer = new byte[1024];
			
			for (File file: allFiles) {
				FileInputStream fis = new FileInputStream(file);
				
				zos.putNextEntry(new ZipEntry(file.getName()));
				
				int length;
				// read the file 1024 bytes at a time
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				
				zos.closeEntry();
				fis.close();
			}
			
			zos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a packaged archive saved previous using saveDatabase.
	 */
	public void loadDatabase() {
		
	}
	
	public void clearDatabase() {
		
	}
	
	public void importDatabase() {
		
	}
	
	public void goMain() {
		mainListener.goMain();
	}
	
	private String generateExportZipFilename() {
		LocalDateTime now = LocalDateTime.now();
		
		return "namesayer_export_" + now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "-"
				+ Utils.padLeft(now.getHour() + "", 2, "0") + Utils.padLeft(now.getMinute() + "", 2, "0") + ".zip";
	}
}
