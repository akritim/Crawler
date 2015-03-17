/**
 * Class for downloading files at given URL
 */
package com.imaginea.downloadutility;

/**
 * @author akritim
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class HttpFileDownloader implements FileDownloader {

	// Logger
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(HttpFileDownloader.class);

	private static final int BUFFER_SIZE = 4096;
	private HttpURLConnection httpConn;
	private BlockingQueue<String> sharedQueue;
	private String saveDirectory;
	private int connectionRetryCount;
	private long waitTimeout;

	public void setHttpFileDownloaderParameters(String saveDirectory,
			BlockingQueue<String> sharedQueue, int connectionRetryCount,
			long waitTimeout) {
		this.saveDirectory = saveDirectory;
		this.sharedQueue = sharedQueue;
		this.connectionRetryCount = connectionRetryCount;
		this.waitTimeout = waitTimeout;
	}

	public void run() {
		String mailLink;
		while (com.imaginea.crawler.Crawler.isParserRunning
				|| (!sharedQueue.isEmpty())) {
			mailLink = sharedQueue.poll();
			if (mailLink != null) {
				try {
					downloadFile(mailLink);
				} catch (IOException iOException) {
					logger.error(iOException);
				}
				logger.debug("Downloading: " + mailLink);
			}
		}
		logger.debug("Closing thread");
	}

	/**
	 * Downloads a file from a URL
	 * 
	 * @param fileURL
	 *            HTTP URL of the file to be downloaded
	 */
	private void downloadFile(String fileURL) throws IOException {
		// Check if local system has space to download the file
		if (checkDiskSpace(this.saveDirectory) > 0) {
			InputStream inputStream = null;
			FileOutputStream outputStream = null;
			String fileName = "";
			try {
				// establish connection and store in variable httpConn.
				// ResponseCode of connection returned here
				int responseCode = establishHttpConnection(fileURL);

				if (responseCode == HttpURLConnection.HTTP_OK) {
					// determining name for the file to be downloaded
					// extracts file name from URL
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
							fileURL.length());

					logger.debug("fileName = " + fileName);

					// open input stream from the already established HTTP
					// connection
					inputStream = this.httpConn.getInputStream();

					// Set the path to save the file
					String saveFilePath = this.saveDirectory + File.separator
							+ fileName;
					try {
						File file = new File(saveFilePath);
						// makes stream append if the file exists
						if (file.exists()) {
							logger.debug("Appending to: " + fileName);
							outputStream = new FileOutputStream(file, true);
						} else {
							logger.debug("New file Created: " + fileName);
							outputStream = new FileOutputStream(file);
						}

						// skip downloaded content to existing file and append
						// to end of file
						inputStream.skip(file.length());

						int bytesRead = -1;
						byte[] buffer = new byte[BUFFER_SIZE];
						while ((bytesRead = inputStream.read(buffer, 0,
								buffer.length)) != -1) {
							outputStream.write(buffer, 0, bytesRead);
						}

						logger.debug("Download completed for file URL: "
								+ fileURL);
					} catch (IOException iOException) {
						logger.error("Failed to download mail from the link: "
								+ fileURL + ". ", iOException);
					}
				} else {
					logger.error("Failed to download mail from the link: "
							+ fileURL + ". Http Connection returned status: "
							+ responseCode);
				}
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (httpConn != null) {
					httpConn.disconnect();
				}
			}
		} else {
			logger.error("No free space in local machine to download more files.");
		}
	}

	/**
	 * Checks disk space of specified directory in the system
	 * 
	 * @param saveDir
	 *            path of the directory to save the file
	 * 
	 * @return long space left in the directory location
	 */
	private long checkDiskSpace(String saveDir) {
		File diskPartition = new File(saveDir);
		long freePartitionSpace = diskPartition.getFreeSpace();
		logger.debug("Free Space : " + freePartitionSpace / (1024 * 1024)
				+ " MB");
		return freePartitionSpace;
	}

	/**
	 * Establishes HTTP connection which is stored in the HttpFileDownloader
	 * object httpConn
	 * 
	 * @param fileURL
	 *            link of mail to download
	 * 
	 * @return int response code for http connection status
	 * 
	 * @throws MalformedURLException
	 * 
	 */
	private int establishHttpConnection(String fileURL)
			throws MalformedURLException {
		int responseCode = 0;
		int connectionRetryCount = this.connectionRetryCount;
		long waitTimeout = this.waitTimeout;

		URL url = new URL(fileURL);
		while (connectionRetryCount > 0) {
			try {
				this.httpConn = (HttpURLConnection) url.openConnection();
				responseCode = this.httpConn.getResponseCode();
			} catch (IOException iOException) {
				logger.error("Connection not established. \n", iOException);
			}

			if (responseCode == HttpURLConnection.HTTP_OK) {
				return responseCode;
			} else if ((responseCode == 0)
					|| ((responseCode > 500) && (responseCode != 501))) {
				// continue to retry connection
				logger.error("Connection not established. Number of connection-retry attempts left: "
						+ (connectionRetryCount - 1));
				try {
					// wait before next connection retry
					synchronized (this.httpConn) {
						this.httpConn.wait(waitTimeout);
					}
					logger.error("Wait completed after connection attempt: "
							+ (5 - connectionRetryCount + 1));
				} catch (InterruptedException interruptedException) {
					logger.error(interruptedException);
					return responseCode;
				}
			} else {
				logger.error("Connection not established. ResponseCode: "
						+ responseCode);
				connectionRetryCount = 0;
				return responseCode;
			}
			connectionRetryCount--;
		}
		return 0;
	}
}