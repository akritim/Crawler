/**
 * 
 */
package com.imaginea.downloadutility;

import java.util.concurrent.BlockingQueue;

/**
 * @author akritim
 *
 */
public interface FileDownloader extends Runnable {
	void setHttpFileDownloaderParameters(String saveDirectory,
			BlockingQueue<String> sharedQueue, int connectionRetryCount,
			long waitTimeout);
}
