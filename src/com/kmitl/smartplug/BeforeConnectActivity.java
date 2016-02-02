package com.kmitl.smartplug;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BeforeConnectActivity extends Activity {

	private Button buttonDirectMode;
	private TextView textView1;
	
	private Button buttonGlobalMode;
	private TextView textView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_before_connect);
		
		startService(new Intent(getApplicationContext(), SwitchService.class));
		
		buttonDirectMode = (Button) findViewById(R.id.buttonDirectMode);
		buttonDirectMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.setModePref(getApplicationContext(), "direct");
				Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.showDialog(BeforeConnectActivity.this, "Can be remote control");
			}
		});
		
		buttonGlobalMode = (Button) findViewById(R.id.buttonGlobalMode);
		buttonGlobalMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.setModePref(getApplicationContext(), "global");
				Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.showDialog(BeforeConnectActivity.this, "Can be control via Internet");
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.before_connect, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
