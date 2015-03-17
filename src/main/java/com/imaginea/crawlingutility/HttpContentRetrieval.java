package com.imaginea.crawlingutility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * @author akritim
 *
 */
public class HttpContentRetrieval implements ContentRetrieval {

	private static org.apache.log4j.Logger logger = Logger
			.getLogger(HttpContentRetrieval.class);
	private int connectionRetryCount;
	private long waitTimeout;

	public void setHttpContentRetrievalParameters(int connectionRetryCount,
			long waitTimeout) {
		this.connectionRetryCount = connectionRetryCount;
		this.waitTimeout = waitTimeout;
	}

	/**
	 * getHttpPageContent - reads all the html and ajax content from URL once
	 * URL connection is successful
	 * 
	 * @param url
	 *            URL of html/ajax page to be downloaded
	 * 
	 * @return String - returns content of http page retrieved
	 * 
	 * @throws MalformedURLException
	 * 
	 * @throws IOException
	 */
	public String getHttpPageContent(String url) throws MalformedURLException {
		String httpPageContent = "";
		boolean acknowledgementCounter = false;
		URL current_url = new URL(url);
		StringBuilder sb = new StringBuilder();
		String strTemp = "";
		BufferedReader br = null;
		int connectionRetryCount = this.connectionRetryCount;
		long waitTimeout = this.waitTimeout;
		try {
			while (connectionRetryCount > 0 && (!acknowledgementCounter)) {
				connectionRetryCount--;
				try {
					br = new BufferedReader(new InputStreamReader(
							current_url.openStream()));
					if (sb.length() > 0) {
						sb.delete(0, sb.length() - 1);
					}
					while (null != (strTemp = br.readLine())) {
						sb.append(strTemp);
						acknowledgementCounter = true;
					}
					logger.debug("Page content successfully retrieved for parsing from: "
							+ current_url);
				} catch (IOException iOException) {
					logger.info("Page content not retrieved from: "
							+ current_url
							+ ". Number of connection-retry attempts left: "
							+ connectionRetryCount);
					// wait before next connection retry
					synchronized (current_url) {
						try {
							current_url.wait(waitTimeout);
						} catch (InterruptedException interruptedException) {
							logger.error(interruptedException);
						}
					}
					logger.info("Wait completed after attempt: "
							+ (5 - connectionRetryCount) + " for url: " + url);
				}
			}
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException iOException) {
					logger.error("Error while closing reader for url: " + url
							+ "\n", iOException);
				}
		}
		if (acknowledgementCounter) {
			httpPageContent = sb.toString();
		} else {
			logger.error("Failed to parse Html content from URL for: " + url);
		}
		return httpPageContent;
	}

}
