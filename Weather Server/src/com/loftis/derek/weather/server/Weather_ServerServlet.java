package com.loftis.derek.weather.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.*;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class Weather_ServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String url = req.getQueryString();
		if (url == null) {
			url = "zip=23456";	// If they don't put a zip, give the VA Beach!
		}
		url = url.substring(url.indexOf("zip=")+4);
		
		url = "http://www.weather.com/weather/today/" + url;
		
		URLFetchService myFetcher = URLFetchServiceFactory.getURLFetchService();
		HTTPRequest request = new HTTPRequest(new URL(url), HTTPMethod.GET);
		
		HTTPResponse response = myFetcher.fetch(request);
		
		// Acquire the data (may be messy at this point)
		String res = new String(response.getContent(), "UTF-8");
		String weatherTemp = res.substring(res.indexOf("&deg;F,") - 3, res.indexOf("&deg;F,"));
		String humidity = res.substring(res.indexOf("relativeHumidity") + 18, res.indexOf("relativeHumidity") + 21);
		String pressure = res.substring(res.indexOf("barometric-pressure-incheshg") + 31, res.indexOf("barometric-pressure-incheshg") + 40);
		
		// Clean it up a bit
		if (weatherTemp.contains("  ")) {
			weatherTemp = weatherTemp.substring(weatherTemp.indexOf("  ") + 2);
		} else if (weatherTemp.contains(" ")) {
			weatherTemp = weatherTemp.substring(weatherTemp.indexOf(" ") + 1);
		}
		if (humidity.contains(",")) {
			humidity = humidity.substring(0, humidity.indexOf(","));
		}
		if (pressure.contains(" ")) {
			pressure = pressure.substring(0, pressure.indexOf(" "));
		}
		
		// Build the output
		String output = null;
		output = "<?xml version='1.0'?>\n";
		output += "<reading>\n";
		output += "\t<source>weather.com</source>\n";
		output += "\t<temp>" + weatherTemp + "</temp>\n";
		output += "\t<humidity>" + humidity + "</humidity>\n";
		output += "\t<pressure>" + pressure + "</pressure>\n";
		output += "</reading>\n";
		
		resp.setContentType("text/plain");
		resp.getWriter().print(output);
		
	}
}
