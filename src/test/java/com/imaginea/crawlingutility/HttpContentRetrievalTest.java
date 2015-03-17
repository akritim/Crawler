/**
 * 
 */
package com.imaginea.crawlingutility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import com.imaginea.crawlingutility.HttpContentRetrieval;

import org.junit.Test;

/**
 * @author akritim
 *
 */
public class HttpContentRetrievalTest {
	
	HttpContentRetrieval httpContentRetrieval = new HttpContentRetrieval();
	
	String url;
	String testCaseType;
	String httpContentRetrieved;
	int connectionRetryCount = 5;
	long waitTimeout = 5000;

	/**
	 * Test method for {@link com.imaginea.crawlingutility.HttpContentRetrieval#getHttpPageContent(java.lang.String)}.
	 */
	@Test
	public void testGetHttpPageContent_inCorrectUrl() {
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/date%20?1";
		testCaseType = "Incorrect URL";
		try {
			httpContentRetrieval.setHttpContentRetrievalParameters(connectionRetryCount, waitTimeout);
			httpContentRetrieved = httpContentRetrieval.getHttpPageContent(url);
			assertTrue("Result: " + testCaseType + " : ", httpContentRetrieved.isEmpty());
			System.out.println("Content: " + httpContentRetrieved);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetHttpPageContent_correctUrl() {
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/date?1";
		testCaseType = "Positive Test Case: Correct URL";
		try {
			httpContentRetrieval.setHttpContentRetrievalParameters(connectionRetryCount, waitTimeout);
			httpContentRetrieved = httpContentRetrieval.getHttpPageContent(url);
			assertFalse("Result: " + testCaseType + " : ", httpContentRetrieved.isEmpty());
			System.out.println("Content: " + httpContentRetrieved);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
