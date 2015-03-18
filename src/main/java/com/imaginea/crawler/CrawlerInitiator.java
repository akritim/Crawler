/**
 * 
 */
package com.imaginea.crawler;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author akritim
 *
 */
public class CrawlerInitiator {

	static final Logger logger = Logger.getLogger(CrawlerInitiator.class);
	private static String url;
	private static int connectionRetryCount;
	private static long waitTimeout;

	/**
	 * @param args
	 *            - command line arguments
	 */
	public static void main(String[] args) {
		try {
			setSourceProperties();

			String saveDirectory = args[1];
			String searchWordYear = args[0];
			String regex = "\\d+";

			FilePathVerifier verifyFilePath = new FilePathVerifier();
			String saveDirectoryPath = verifyFilePath.verifyPath(saveDirectory);
			if (!(saveDirectoryPath.length() > 0)) {
				logger.error("Path specified to save mails does not exist on this machine."
						+ " Request cannot be processed." + saveDirectory);
			} else if (searchWordYear.length() != 4
					|| (!searchWordYear.matches(regex))) {
				logger.error("Year specified is not correct. Request cannot be processed: "
						+ searchWordYear);
			} else {
				ClassPathXmlApplicationContext applicationContext = null;
				try{
					//String confFile = "src\\main\\resources\\beandefinition.xml";
					//Resource resource_beans = new FileSystemResource(confFile);
					applicationContext = new ClassPathXmlApplicationContext("/beandefinition.xml");
					//BeanFactory beanFactory = new XmlBeanFactory(resource_beans);
					BeanFactory beanFactory = applicationContext.getBeanFactory();
					Crawler crawler = (Crawler) beanFactory.getBean("crawler");
					crawler.crawl(saveDirectory, searchWordYear, url,
							connectionRetryCount, waitTimeout);	
				} finally{
					applicationContext.close();
				}
			}
		} catch (IOException ioexception) {
			logger.error(ioexception);
		}
	}

	/**
	 * setSourceUrl: Read property file urlsource.properties to get URL to begin
	 * crawling
	 */
	public static void setSourceProperties() throws IOException {
		//String confFile = "src\\main\\resources\\connection_sourceproperties.xml";
		//Resource resource_source_url = new FileSystemResource(confFile);
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/connection_sourceproperties.xml");
		//BeanFactory beanFactory = new XmlBeanFactory(applicationContext);
		//Properties sourceConfig = (Properties) beanFactory
	//	.getBean("sourceProperty");
		Properties sourceConfig = (Properties) applicationContext.getBean("sourceProperty");
		url = sourceConfig.getProperty("source_url");
		connectionRetryCount = Integer.parseInt(sourceConfig
				.getProperty("connectionRetryCount"));
		waitTimeout = Integer.parseInt(sourceConfig.getProperty("waitTimeout"));
		applicationContext.close();
	}
}
