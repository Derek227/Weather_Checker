package com.vt.derek.netapps.assignment2App;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WeatherActivity extends Activity implements SensorEventListener {

	TextView txtAsOf_, txtInternet_, txtSensor_;
	Button btnUpdate_;
	String zip, serverPort, serverURL;
	final Handler myHandler = new Handler();
	SensorManager sensorManager_;
	Sensor pressure_, humidity_, temp_;
	String pres_, hum_, tem_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		txtAsOf_ = (TextView)findViewById(R.id.txtAsOf);
		btnUpdate_ = (Button)findViewById(R.id.btnUpdate);
		txtInternet_ = (TextView)findViewById(R.id.txtData);
		txtSensor_ = (TextView)findViewById(R.id.txtSensors);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = sharedPref.getString("server_url", "localhost");
		serverPort = sharedPref.getString("server_port", "8888");
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		
		zip = "23456";	// VA Beach is the default zip code (it's where I'm from!)
		Geocoder myGeo = new Geocoder(WeatherActivity.this, Locale.ENGLISH);
		try {
			List<Address> addresses = myGeo.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
			zip = addresses.get(0).getPostalCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AsyncGetData dataFetch = new AsyncGetData(txtAsOf_, txtInternet_);
		dataFetch.execute(zip, serverPort, serverURL);
		
		btnUpdate_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AsyncGetData dataFetch = new AsyncGetData(txtAsOf_, txtInternet_);
				dataFetch.execute(zip, serverPort, serverURL);
			}
		});
		
		Timer t = new Timer();
		//Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {
			@SuppressLint("NewApi")
			@Override
		    public void run() {
		        //Called every 15 seconds (the period parameter)
				ToggleButton tgl = (ToggleButton)findViewById(R.id.tbtnUpdate);
				if (tgl.isChecked()) { 
					myHandler.post(myRun);
				}
		    }
		         
		},
		//Set how long before to start calling the TimerTask (in milliseconds)
		(long)15000,
		//Set the amount of time between each execution (in milliseconds)
		(long)15000);

		
		sensorManager_ = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		pressure_ = sensorManager_.getDefaultSensor(Sensor.TYPE_PRESSURE);
		humidity_ = sensorManager_.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		temp_ = sensorManager_.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		
		if (pressure_ != null) {
			sensorManager_.registerListener(this, pressure_, SensorManager.SENSOR_DELAY_NORMAL);
			pres_ = "Acquiring data.";
		} else {
			pres_ = "No sensor available.";
		}
		if (humidity_ != null) {
			sensorManager_.registerListener(this, humidity_, SensorManager.SENSOR_DELAY_NORMAL);
			hum_ = "Acquiring data.";
		} else {
			hum_ = "No sensor available.";
		}
		if (temp_ != null) {
			sensorManager_.registerListener(this, temp_, SensorManager.SENSOR_DELAY_NORMAL);
			tem_ = "Acquiring data.";
		} else {
			tem_ = "No sensor available.";
		}
		txtSensor_.setText("Temperature: " + tem_ + "\nPressure: " + pres_ + "\nHumidity: " + hum_);
		
		
	}
	
	// We've got to do some crazy gymnastics to get the GUI to only be updated from the GUI thread.
	final Runnable myRun = new Runnable() {
		public void run() {
			AsyncGetData dataFetch = new AsyncGetData(txtAsOf_, txtInternet_);
			dataFetch.execute(zip, serverPort, serverURL);
		}
	};
	
	@Override
	public final void onSensorChanged(SensorEvent event) {
			if (event.sensor == temp_) {
				tem_ = String.valueOf(event.values[0]) + " F";
			} else if (event.sensor == pressure_) {
				pres_ = String.valueOf(event.values[0] * 0.0295301) + " in";	// We must convert from mBar to in to be consistant with the internet data
			} else if (event.sensor == humidity_) {
				hum_ = String.valueOf(event.values[0]) + "%";
			}
			txtSensor_.setText("Temperature: " + tem_ + "\nPressure: " + pres_ + "\nHumidity: " + hum_);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing here for us!
	}

}
