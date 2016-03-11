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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BeforeConnectActivity extends Activity {

	private ImageView imageViewGlobalMode;
	private ImageView imageView1;
	
	private ImageView imageViewDirectMode;
	private ImageView imageView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_before_connect);
		
		startService(new Intent(getApplicationContext(), SwitchService.class));
		
		imageViewGlobalMode = (ImageView) findViewById(R.id.imageViewGlobalmode);
		imageViewGlobalMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.setModePref(getApplicationContext(), "global");
				Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.showDialog(BeforeConnectActivity.this, "Control via Internet");
			}
		});
		
		imageViewDirectMode = (ImageView) findViewById(R.id.imageViewDirectMode);
		imageViewDirectMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.setModePref(getApplicationContext(), "direct");
				Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedValues.showDialog(BeforeConnectActivity.this, "Using as common remote control");
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
