package com.vt.derek.netapps.assignment2App;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView txtOut_;
	private SensorManager sense;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtOut_ = (TextView)findViewById(R.id.txtOutput);
		sense = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		if (sense.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
			txtOut_.setText("Ambient Temp: True\n");
		} else {
			txtOut_.setText("Ambient Temp: False\n");
		}
		if (sense.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
			txtOut_.append("Presure: True\n");
		} else {
			txtOut_.append("Presure: False\n");
		}
		if (sense.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
			txtOut_.append("Humidity: True\n");
		} else {
			txtOut_.append("Humidity: False\n");
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
