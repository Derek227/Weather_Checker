package com.vt.derek.netapps.assignment2App;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EntryActivity extends Activity {

	Button btnSensors_, btnReadings_, btnSettings_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		btnSensors_ = (Button)findViewById(R.id.btnSensor);
		btnReadings_ = (Button)findViewById(R.id.btnReadings);
		btnSettings_ = (Button)findViewById(R.id.btnSettings);
		
		btnSensors_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(EntryActivity.this, MainActivity.class);
				EntryActivity.this.startActivity(intent);
			}
		});
		
		btnReadings_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(EntryActivity.this, WeatherActivity.class);
				EntryActivity.this.startActivity(intent);
			}
		});
		
		btnSettings_.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(EntryActivity.this, SettingsActivity.class);
				EntryActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}

}
