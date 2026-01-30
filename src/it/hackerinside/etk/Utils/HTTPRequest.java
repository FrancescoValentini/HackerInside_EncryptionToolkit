package it.hackerinside.etk.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HTTPRequest {
	/**
	 * Sends an HTTP GET request to the specified URL and returns the response body as a String.
	 * 
	 * @param url The URL to send the GET request to. Must be a valid, properly encoded URL.
	 *            
	 * @return The response body as a String. If the response contains no body,
	 *         an empty string ("") is returned.
	 *                  
	 * @implSpec The HttpClient is configured with:
	 *           <ul>
	 *             <li>5-second connection timeout</li>
	 *             <li>Default HTTP/2 protocol preference</li>
	 *             <li>Normal redirect policy (Always redirect, except from HTTPS URLs to HTTP URLs.)</li>
	 *           </ul>
	 *           The response body is decoded using UTF-8 character set.
	 */
	public static String getString(String url) throws IOException, InterruptedException {
    	HttpClient client = HttpClient.newBuilder()
    			.connectTimeout(Duration.ofSeconds(5))
    			.followRedirects(Redirect.NORMAL) // Always redirect, except from HTTPS URLs to HTTP URLs.
    			.build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
	}
}
