/**
 * 
 */
package com.imaginea.crawlingutility;

import java.net.MalformedURLException;

/**
 * @author akritim
 *
 */
public interface ContentRetrieval {
	void setHttpContentRetrievalParameters(int connectionRetryCount,
			long waitTimeout);

	String getHttpPageContent(String url) throws MalformedURLException;
}
