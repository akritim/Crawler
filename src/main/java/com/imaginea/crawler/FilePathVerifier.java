/**
 * 
 */
package com.imaginea.crawler;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

/**
 * @author akritim
 *
 */
public class FilePathVerifier {

	Path directoryPath;

	private static org.apache.log4j.Logger logger = Logger
			.getLogger(FilePathVerifier.class);

	/**
	 * pathVerifier: Receives path for downloading mails, converts it to
	 * standard path It also checks ig path exists in the system
	 * 
	 * @param saveDirectory
	 *            - path to download files in the current system
	 * @returns String - Standard path for downloading mails in the current
	 *          system
	 * 
	 * @throws InvalidPathException
	 */
	public String verifyPath(String saveDirectory) throws InvalidPathException {
		directoryPath = Paths.get(saveDirectory);
		logger.info("Checking for path specified to save mails: "
				+ directoryPath);
		if (doesDirectoryExist(directoryPath)) {
			return saveDirectory;
		} else {
			return "";
		}
	}

	/**
	 * doesDirectoryExists: checks if path exits in the current machine
	 * 
	 * @param saveDirectory
	 *            - path to download files in the current system
	 * 
	 * @return boolean - Returns true if the directory exits
	 */
	private boolean doesDirectoryExist(Path directoryPath) {
		File theDir = new File(directoryPath.toString());
		if (theDir.exists()) {
			logger.info("Save directory specified exists. Proceeding to process.");
			return true;
		} else {
			if (theDir.mkdir()) {
				logger.info("Save directory created. Proceeding to process.");
				return true;
			}
		}
		logger.info("Save directory doest not exist. Request will not be processed.");

		return false;

	}
}
