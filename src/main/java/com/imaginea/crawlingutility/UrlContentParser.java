package com.imaginea.crawlingutility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author akritim
 *
 */
public class UrlContentParser implements ContentParser {

	// Logger
	private static org.apache.log4j.Logger logger = Logger
			.getLogger(UrlContentParser.class);

	private BlockingQueue<String> sharedQueue;
	private String url;
	private String searchWordYear;
	private String searchMonth;
	private int connectionRetryCount;
	private long waitTimeout;
	ContentRetrieval contentRetrieval;

	public void setUrlContentParserParameters(String searchWordYear,
			String searchMonth, String url, BlockingQueue<String> sharedQueue,
			int connectionRetryCount, long waitTimeout) {
		this.searchWordYear = searchWordYear;
		this.searchMonth = searchMonth;
		this.url = url;
		this.sharedQueue = sharedQueue;
		this.connectionRetryCount = connectionRetryCount;
		this.waitTimeout = waitTimeout;
	}

	public void setContentRetrievalBeanFactory(ContentRetrieval contentRetrieval) {
		this.contentRetrieval = contentRetrieval;
	}

	/**
	 * crawl: Receives input parameters checks if they are valid and then calls
	 * for processing input for each month of input year and subsequently
	 * downloading mails as applicable
	 * 
	 * @param searchWordYear
	 *            - Year in yyyy format
	 * @param saveDirectory
	 *            - Location where mails are to be downloaded in the local
	 *            machine
	 * @throws IOException
	 */
	public void run() {
		try {
			String searchWord;
			searchWord = this.searchWordYear.concat(this.searchMonth);
			logger.info("Starting processsing for yyyymm: " + searchWord);
			parseUrlContent(searchWord);
			logger.info("Crawler completed execution for: " + searchMonth);
		} catch (MalformedURLException malformedURLException) {
			logger.error("Exiting the run for " + searchMonth + ". ",
					malformedURLException);
		} catch (InterruptedException interruptedException) {
			logger.error("Exiting the run for " + searchMonth + ". ",
					interruptedException);
		}
	}

	/**
	 * getMailDownloadLinks: Passes the URL to be parsed to the Content Parser
	 * and after parsing adds to the list of pages to visit and mails to
	 * download and pages not processed links. It returns list of mails to be
	 * downloaded for a month
	 * 
	 * @param url
	 *            - The starting point of the HTTP request
	 * @param searchWord
	 *            - Year and month in yyyymm format
	 * 
	 * @return List - MailsToDownload List
	 * 
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	private void parseUrlContent(String searchWord)
			throws MalformedURLException, InterruptedException {
		boolean parseNextPageRequired = true;
		int pageNumber = 0;
		String currentUrl;
		int mailLinksAddedInCurrentPageParse = 0;
		contentRetrieval.setHttpContentRetrievalParameters(
				connectionRetryCount, waitTimeout);

		while (parseNextPageRequired) {
			currentUrl = this.url.concat(searchWord).concat(
					".mbox/date?" + pageNumber);
			logger.info("Starting parse for url: " + currentUrl);
			mailLinksAddedInCurrentPageParse = getNumberOfMailDownloadLinksAddedToQueue(
					currentUrl, searchWord);
			logger.debug("Search word : " + searchWord + " pageNumber : "
					+ pageNumber + " mailLinksAddedInCurrentPageParse : "
					+ mailLinksAddedInCurrentPageParse);
			if (mailLinksAddedInCurrentPageParse != 100) {
				parseNextPageRequired = false;
			} else {
				mailLinksAddedInCurrentPageParse = 0;
			}
			pageNumber++;
		}
	}

	/**
	 * URLContentParser: It makes an HTTP request, checks the response, and then
	 * gathers up all the links on the page. Perform a search functions for
	 * subsequent URLs and mail links after the successful HTML Ajax content is
	 * retrieved
	 * 
	 * @param url
	 *            - The URL to parse
	 * @param searchWord
	 *            - Year and month in yyyymm format
	 * 
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	private int getNumberOfMailDownloadLinksAddedToQueue(String url,
			String searchWord) throws MalformedURLException,
			InterruptedException {

		int numberOfMailLinksOnPage = 0;
		/*
		 * Reading from URL: Not using Jsoup - Jsoup is static HTML parser -
		 * does not retrieve AJAX content Using traditional InputStream reader
		 * HTML received is then parsed and processed by Jsoup for ease of use
		 */
		String httpContent = contentRetrieval.getHttpPageContent(url);
		if (httpContent.isEmpty()) {
			logger.error("URL content could not be parsed for: " + url);
		}

		Document htmlDocument = Jsoup.parse(httpContent, url);
		logger.debug("Proceeding to parse page content retrieved from: " + url);

		// Finding links from the received HTML being parsed
		Elements linksOnPage = htmlDocument.select("a[href]");
		logger.debug("Found (" + linksOnPage.size()
				+ ") links while parsing url: " + url);
		for (Element link : linksOnPage) {
			if (link.absUrl("href").toString().endsWith("%3e")
					&& link.absUrl("href").toString()
							.contains("/" + searchWord + ".mbox/%3c")) {
				logger.debug("Adding to mail download list: "
						+ link.absUrl("href") + " from page url: " + url);
				sharedQueue.put(link.absUrl("href"));
				numberOfMailLinksOnPage++;
			}
		}
		return numberOfMailLinksOnPage;
	}
}