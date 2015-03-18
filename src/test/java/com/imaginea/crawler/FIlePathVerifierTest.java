/**
 * 
 */
package com.imaginea.crawler;

import static org.junit.Assert.*;
import com.imaginea.crawler.FilePathVerifier;

import org.junit.Test;

/**
 * @author akritim
 *
 */
public class FIlePathVerifierTest {

	FilePathVerifier filePathVerifier = new FilePathVerifier();
	String saveDirectory;
	String testCaseType;

	/**
	 * Test method for
	 * {@link com.imaginea.crawler.FilePathVerifier#verifyPath(java.lang.String)}
	 * .
	 */
	@Test
	public void testVerifyPath_incorrectPath() {
		testCaseType = "Incorrect Path";
		saveDirectory = "G:\folder2";
		String result;
		try {
			result = filePathVerifier.verifyPath(saveDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			result = "";
		}
		System.out.println(testCaseType);
		System.out.println("Result Size: " + result.length());
		System.out.println("Result String: " + result);
		assertFalse("Result: " + testCaseType + " : ", result.length() > 0);

	}

	/**
	 * Test method for
	 * {@link com.imaginea.crawler.FilePathVerifier#verifyPath(java.lang.String)}
	 * .
	 */
	@Test
	public void testVerifyPath_correctPathFolderNotYetThere() {
		testCaseType = "correctPathFolderNotYetThere";
		saveDirectory = "F:\\folder2";
		String result = filePathVerifier.verifyPath(saveDirectory);
		assertTrue("Result: " + testCaseType + " : ", result.length() > 0);
		System.out.println(testCaseType);
		System.out.println("Result Size: " + result.length());
		System.out.println("Result String: " + result);

	}

	/**
	 * Test method for
	 * {@link com.imaginea.crawler.FilePathVerifier#verifyPath(java.lang.String)}
	 * .
	 */
	@Test
	public void testVerifyPath_correctPathFolderExists() {
		testCaseType = "correctPathFolderExists";
		saveDirectory = "C:\\Users\\akritim\\Resources\\Downloads\\folder1";
		String result = filePathVerifier.verifyPath(saveDirectory);
		assertTrue("Result: " + testCaseType + " : ", result.length() > 0);
		System.out.println(testCaseType);
		System.out.println("Result Size: " + result.length());
		System.out.println("Result String: " + result);
	}

}
