/**
 * 
 */
package com.imaginea.crawlingutility;

import java.util.concurrent.BlockingQueue;

/**
 * @author akritim
 *
 */
public interface ContentParser extends Runnable {
	void setUrlContentParserParameters(String searchWordYear,
			String searchMonth, String url, BlockingQueue<String> sharedQueue,
			int connectionRetryCount, long waitTimeout);
}