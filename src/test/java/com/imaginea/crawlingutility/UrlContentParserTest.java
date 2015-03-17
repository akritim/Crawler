/**
 * 
 */
package com.imaginea.crawlingutility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
/**
 * @author akritim
 *
 */
public class UrlContentParserTest {
	
	BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();
	private String url = "http://mail-archives.apache.org/mod_mbox/maven-users/";
	private String searchWordYear = "2014";
	private String searchMonth = "05";
	private int connectionRetryCount = 5;
	private long waitTimeout = 5000;
	BeanFactory beanFactory;
	String testCaseType;
	
	/**
	 * Test method for {@link com.imaginea.crawlingutility.UrlContentParser#run()}.
	 */
	@Test
	public void testRun__correctParamaters() {
		int initialQueueSize = sharedQueue.size();
		testCaseType = "All information correct";
		UrlContentParser urlContentParser1 = new UrlContentParser();
		ContentRetrieval contentRetrieval = new HttpContentRetrieval();
		urlContentParser1.setContentRetrievalBeanFactory(contentRetrieval);
		urlContentParser1.setUrlContentParserParameters(searchWordYear,
				searchMonth, url, sharedQueue, connectionRetryCount, waitTimeout);
		urlContentParser1.run();
		int finalQueueSize = sharedQueue.size();
		assertTrue("Result: " + testCaseType + " : ", finalQueueSize > initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}
	
	/**
	 * Test method for {@link com.imaginea.crawlingutility.UrlContentParser#run()}.
	 */
	@Test
	public void testRun__incorrectSearchWordYear() {
		int initialQueueSize = sharedQueue.size();
		searchWordYear = "213";
		testCaseType = "Incorrect SearchWordYear";
		UrlContentParser urlContentParser2 = new UrlContentParser();
		ContentRetrieval contentRetrieval = new HttpContentRetrieval();
		urlContentParser2.setContentRetrievalBeanFactory(contentRetrieval);
		urlContentParser2.setUrlContentParserParameters(searchWordYear,
				searchMonth, url, sharedQueue, connectionRetryCount, waitTimeout);
		urlContentParser2.run();
		int finalQueueSize = sharedQueue.size();
		assertFalse("Result: " + testCaseType + " : ", finalQueueSize > initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}
	
	/**
	 * Test method for {@link com.imaginea.crawlingutility.UrlContentParser#run()}.
	 */
	@Test
	public void testRun__incorrectSearchMonth() {
		int initialQueueSize = sharedQueue.size();
		searchWordYear = "2014";
		searchMonth = "5";		
		testCaseType = "Incorrect SearchMonth";
		UrlContentParser urlContentParser3 = new UrlContentParser();
		ContentRetrieval contentRetrieval = new HttpContentRetrieval();
		urlContentParser3.setContentRetrievalBeanFactory(contentRetrieval);
		urlContentParser3.setUrlContentParserParameters(searchWordYear,
				searchMonth, url, sharedQueue, connectionRetryCount, waitTimeout);
		urlContentParser3.run();
		int finalQueueSize = sharedQueue.size();
		assertFalse("Result: " + testCaseType + " : ", finalQueueSize > initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}
	
	/**
	 * Test method for {@link com.imaginea.crawlingutility.UrlContentParser#run()}.
	 */
	@Test
	public void testRun__incorrectUrl() {
		int initialQueueSize = sharedQueue.size();
		searchMonth = "05";		
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/date%20?1";
		testCaseType = "Incorrect Url";
		UrlContentParser urlContentParser4 = new UrlContentParser();
		ContentRetrieval contentRetrieval = new HttpContentRetrieval();
		urlContentParser4.setContentRetrievalBeanFactory(contentRetrieval);
		urlContentParser4.setUrlContentParserParameters(searchWordYear,
				searchMonth, url, sharedQueue, connectionRetryCount, waitTimeout);
		urlContentParser4.run();
		int finalQueueSize = sharedQueue.size();
		assertFalse("Result: " + testCaseType + " : ", finalQueueSize > initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}
}
