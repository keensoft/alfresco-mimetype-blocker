package es.keensoft.alfresco.test;

import java.util.Arrays;
import java.util.List;

import es.keensoft.alfresco.util.MimeTypeExpressionHandler;

/**
 * The Class MimeTypeExpressionHandlerTest.
 */
public class MimeTypeExpressionHandlerTest {
	
	/*
	## Samples
	## STARTS WITH video = video*
	##   ENDS WITH xml   = *xml 
	##    CONTAINS pdf   = *pdf*
	## MANY (use pipes)  =  application/octet-stream|application/zip
	 */
	private static List<String> restrictingExpressions = Arrays
			.asList(new String[] { "video*", "*xml", "*pdf*", "application/octet-stream|application/zip",
					"application/octet-stream", "", "video*|*xml|*pdf*|application/octet-stream" });

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String... args) throws Exception {
		final MimeTypeExpressionHandler mteh = new MimeTypeExpressionHandler();
		for (String expression : restrictingExpressions) {
			System.out.println(expression);
			mteh.setRestrictedMimetypesExpression(expression);
			System.out.println("video/3gpp:" + mteh.isRestricted("video/3gpp"));
			System.out.println("application/xml:" + mteh.isRestricted("application/xml"));
			System.out.println("application/pdf:" + mteh.isRestricted("application/pdf"));
			System.out.println("application/zip:" + mteh.isRestricted("application/zip"));
			System.out.println("application/octet-stream:" + mteh.isRestricted("application/octet-stream"));
			System.out.println("--");
		}
	}
}
