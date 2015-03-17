/**
 * 
 */
package com.imaginea.downloadutility;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

/**
 * @author akritim
 *
 */
public class HttpFileDownloaderTest {
	
	private int connectionRetryCount = 5;
	private long waitTimeout = 5000;
	
	private String saveDirectory = "C:\\Users\\akritim\\Resources\\Downloads\\folder1";
	
	BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();
	
	

	String testCaseType;
	//int SharedQueueSize = 0;
	
	
	/**
	 * Test method for {@link com.imaginea.downloadutility.HttpFileDownloader#run()}.
	 */
	@Test
	public void testRun_NoChangeInSharedQueue() {
		com.imaginea.crawler.Crawler.isParserRunning = false;
		int initialQueueSize = sharedQueue.size();
		testCaseType = "All information correct: NoChangeInSharedQueue";
		HttpFileDownloader httpFileDownloader1 = new HttpFileDownloader();
		httpFileDownloader1.setHttpFileDownloaderParameters(saveDirectory, sharedQueue, connectionRetryCount, waitTimeout);
		httpFileDownloader1.run();
		int finalQueueSize = sharedQueue.size();
		assertTrue("Result: " + testCaseType + " : ", finalQueueSize == initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}	
	
	/**
	 * Test method for {@link com.imaginea.downloadutility.HttpFileDownloader#run()}.
	 */
	@Test
	public void testRun_SharedQueueHasData_isParserRunningFalse() {
		com.imaginea.crawler.Crawler.isParserRunning = false;
		String mailLink = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/ajax/%3Cm5hi1f%24757%241%40ger.gmane.org%3E";
		try {
			sharedQueue.put(mailLink);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int initialQueueSize = sharedQueue.size();
		testCaseType = "All information correct : SharedQueueHasData_isParserRunningFalse";
		HttpFileDownloader httpFileDownloader2 = new HttpFileDownloader();
		httpFileDownloader2.setHttpFileDownloaderParameters(saveDirectory, sharedQueue, connectionRetryCount, waitTimeout);
		httpFileDownloader2.run();
		int finalQueueSize = sharedQueue.size();
		assertTrue("Result: " + testCaseType + " : SharedQueueHasData_isParserRunningFalse", finalQueueSize < initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}	
	
	/**
	 * Test method for {@link com.imaginea.downloadutility.HttpFileDownloader#run()}.
	 */
	@Test
	public void testRun_incorrectMailLink() {
		com.imaginea.crawler.Crawler.isParserRunning = false;
		String mailLink = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/ajax/%3Cm5hi1f%24757%241%40ger.gmane.org %3E";
		HttpFileDownloader httpFileDownloader3 = new HttpFileDownloader();
		httpFileDownloader3.setHttpFileDownloaderParameters(saveDirectory, sharedQueue, connectionRetryCount, waitTimeout);
		try {
			sharedQueue.put(mailLink);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int initialQueueSize = sharedQueue.size();
		testCaseType = "inCorrectMailLink";
		try
		{
			httpFileDownloader3.run();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int finalQueueSize = sharedQueue.size();
		assertTrue("Result: " + testCaseType + " : SharedQueueHasData_isParserRunningFalse", finalQueueSize < initialQueueSize );
		System.out.println(testCaseType);
		System.out.println("Initial Queue Size: " + initialQueueSize);
		System.out.println("Final Queue Size: " + finalQueueSize);
	}		
}
