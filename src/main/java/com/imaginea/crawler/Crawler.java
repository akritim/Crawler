/**
 * 
 */
package com.imaginea.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.imaginea.crawlingutility.ContentParser;
import com.imaginea.downloadutility.FileDownloader;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author akritim
 *
 */
public class Crawler implements BeanFactoryAware {

	private static final Logger logger = Logger.getLogger(Crawler.class);
	private static final int PARSERTHREADPOOLSIZE = 12;
	private static final int DOWNLOADTHREADPOOLSIZE = 39;
	public static boolean isParserRunning = true;
	private ExecutorService parserExecutor;
	private ExecutorService downloadExecutor;
	private ContentParser contentParser;
	private FileDownloader fileDownloader;
	private BeanFactory beanFactory;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void crawl(String saveDirectory, String searchWordYear, String url,
			int connectionRetryCount, long waitTimeout) {
		// Creating shared object
		BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();

		// To initiate crawler process
		parserExecutor = Executors.newFixedThreadPool(PARSERTHREADPOOLSIZE);

		downloadExecutor = Executors.newFixedThreadPool(DOWNLOADTHREADPOOLSIZE);

		String[] months = { "01", "02", "03", "04", "05", "06", "07", "08",
				"09", "10", "11", "12" };

		for (String month : months) {
			contentParser = (ContentParser) beanFactory
					.getBean("contentParser");
			System.out.println(contentParser + " : " + month);
			contentParser.setUrlContentParserParameters(searchWordYear, month,
					url, sharedQueue, connectionRetryCount, waitTimeout);
			parserExecutor.execute(contentParser);
		}
		parserExecutor.shutdown();

		int counter;
		for (counter = 0; counter < DOWNLOADTHREADPOOLSIZE; counter++) {
			fileDownloader = (FileDownloader) beanFactory
					.getBean("fileDownloader");
			fileDownloader.setHttpFileDownloaderParameters(saveDirectory,
					sharedQueue, connectionRetryCount, waitTimeout);
			downloadExecutor.execute(fileDownloader);
		}

		downloadExecutor.shutdown();

		// Wait until all threads are finish
		while (!parserExecutor.isTerminated()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException interruptedException) {
				logger.error(interruptedException);
			}
		}

		isParserRunning = false;
		logger.info("Finished crawling process");

		// Wait until all threads are finish
		while (!downloadExecutor.isTerminated()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException interruptedException) {
				logger.error(interruptedException);
			}
		}
		logger.info("Finished mail download process");

	}

}
