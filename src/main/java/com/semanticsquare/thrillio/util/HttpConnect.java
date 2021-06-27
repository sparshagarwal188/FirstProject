package com.semanticsquare.thrillio.util;

import java.net.URL;
import java.net.URI;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

public class HttpConnect {
	public static String download(String sourceUrl) throws MalformedURLException, URISyntaxException {
		System.out.println("Downloading : " + sourceUrl);
		
		URL url = new URI(sourceUrl).toURL();
		
		try {
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			int responseCode = con.getResponseCode();
			
			if (responseCode >= 200 && responseCode <300) {
				return IOUtil.read(con.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
