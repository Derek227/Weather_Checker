package com.vt.derek.netapps.assignment2App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.TextView;

public class AsyncGetData extends AsyncTask<String, Void, double[]>{

	TextView txtAsOf_, txtData_;
	
	AsyncGetData(TextView asOf, TextView data) {
		txtAsOf_ = asOf;
		txtData_ = data;
	}
	
	
	protected void onPreExecute(){
		txtAsOf_.setText("Fetching Data...");
	}
	
	@Override
	protected double[] doInBackground(String... arg0) {
		// Arg list:
		// [0] : Zip code of user (23456 by default)
		// [1] : Port Number of server (8888 by default)
		// [2] : URL of server (localhost by default)
		
		String server = "";
		String port = "";
		String query = "zip=";
		String zip = "";
		double[] networkResult = new double[5];
		
		// The deployed version is at URL http://netapps-project-2.appspot.com/
		// The port must by 80 (because it's html after all)
		
		if (arg0.length == 0) {
			// No Args
			server = "localhost";
			port = "8888";
			query += "23456";
			zip = "23456";
		} else if (arg0.length == 1) {
			// only a zip code
			server = "localhost";
			port = "8888";
			query += arg0[0];
			zip = arg0[0];
		} else if (arg0.length == 2) {
			// zip and port
			server = "localhost";
			port = arg0[1];
			query += arg0[0];
			zip = arg0[0];
		} else if (arg0.length > 2) {
			// Zip, Port, and URL.  NOTE: All other args are ignored
			server = arg0[2];
			port = arg0[1];
			query += arg0[0];
			zip = arg0[0];
		}
		
		HttpClient client = new DefaultHttpClient();
		String url = "http://" + server + ":" + port + "/weather?" + query;
		HttpGet get = new HttpGet(url);

		try {
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			line = sb.toString();
			String temp = line.substring(line.indexOf("<temp>") + 6, line.indexOf("</temp>"));
			String pressure = line.substring(line.indexOf("<pressure>") + 10, line.indexOf("</pressure>"));
			String humidity = line.substring(line.indexOf("<humidity>") + 10, line.indexOf("</humidity>"));
			networkResult[0] = 0;
			networkResult[1] = Double.parseDouble(temp);
			networkResult[2] = Double.parseDouble(pressure);
			networkResult[3] = Double.parseDouble(humidity);
			networkResult[4] = Double.parseDouble(zip);
	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			networkResult[0] = 1;	// Error code 1
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			networkResult[0] = 2;	// Error code 2
		}

		
		
		return networkResult;
	}
	
	protected void onPostExecute(double[] output) {
		if (output[0] != 0) {
			// We've got an error! Best handle it!
			if (output[0] == 1) {
				// ClientProtocolException
				txtAsOf_.setText("Client Protocol Error");
			} else if (output[0] == 2) {
				// IOException
				txtAsOf_.setText("IO Error");
			} else {
				// Unknown error
				txtAsOf_.setText("Unknown Error");
			}
		} else {
			// Give the results back to the display activity
			txtAsOf_.setText("As Of: ");
			Time now = new Time();
			now.setToNow();
			txtAsOf_.append(now.format("%H:%M:%S"));
			String zip = Double.toString(output[4]);
			if (zip.contains(".")) {
				zip = zip.substring(0, zip.indexOf("."));
			}
			txtData_.setText("Location: " + zip + "\nTemperature: " + Double.toString(output[1]) + " F\nPressure: " + Double.toString(output[2]) +" in\nHumidity: " + Double.toString(output[3]) + "%");
		}
	}
	
}
