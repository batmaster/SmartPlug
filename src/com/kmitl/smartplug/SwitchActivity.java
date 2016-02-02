package com.kmitl.smartplug;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SwitchActivity extends Activity {
	
	private TextView textView0;
	private ImageView imageViewSwitch;
	private ImageView imageViewBulb;
	private ImageView imageViewRefresh;
	private ImageView imageViewSetAlarm;
	private ImageView imageViewSetWifi;
	
	public static SwitchActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);
		
		activity = SwitchActivity.this;
		
		textView0 = (TextView) findViewById(R.id.textView0);
		textView0.setText((SharedValues.getModePref(getApplicationContext()).equals("direct") ? "Direct Mode" : "Global Mode") + " ON/OFF");
		
		imageViewRefresh = (ImageView) findViewById(R.id.imageViewRefresh);
		imageViewRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckStateTask task = new CheckStateTask(getApplicationContext(), true);
            	task.execute();
			}
		});
		
		imageViewSwitch = (ImageView) findViewById(R.id.imageViewSwitch);
		imageViewSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ready) {
					SwitchTask task = new SwitchTask(getApplicationContext());
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Sending in progress, please wait and try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		imageViewBulb = (ImageView) findViewById(R.id.imageViewBulb);
		
		imageViewSetAlarm = (ImageView) findViewById(R.id.imageViewSetAlarm);
		imageViewSetAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
				startActivity(intent);
			}
		});
		
		imageViewSetWifi = (ImageView) findViewById(R.id.imageViewSetWifi);
		imageViewSetWifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(intent);
			}
		});
		imageViewSetWifi.setVisibility(SharedValues.getModePref(getApplicationContext()).equals("global") ? View.GONE : View.VISIBLE);
		
		refreshStatus(getIntent().getStringExtra("the8Digits"));
	}
	
	private void refreshStatus(String the8Digits) {
			imageViewSwitch.setImageResource(the8Digits.charAt(1) == '0' ? R.drawable.switch_off : R.drawable.switch_on);
			imageViewBulb.setImageResource(the8Digits.charAt(0) == '0' ? R.drawable.bulb_off : R.drawable.bulb_on);
			
			if (the8Digits.charAt(0) != the8Digits.charAt(1))
				SharedValues.showDialog(SwitchActivity.this, "Appliance has problem!");
			else if (the8Digits.charAt(0) == 0)
				SharedValues.showDialog(SwitchActivity.this, "Appliance is using");
			else
				SharedValues.showDialog(SwitchActivity.this, "Appliance is not using");
	}
	
	private boolean ready = true;
	private class CheckStateTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private boolean showDialog;
		private ProgressDialog dialog;
		
		public CheckStateTask(Context context, Boolean showDialog) {
			this.context = context;
			this.showDialog = showDialog;
			
			dialog = new ProgressDialog(SwitchActivity.this);
			dialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			Log.d("p", "CheckStateTask: Pre");
			ready = false;
			
			dialog.setMessage("Checking state...");
			if (!dialog.isShowing() && showDialog) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.d("p", "CheckStateTask: In");
			return Service.sendHttpRequest(context, "4", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("p", "CheckStateTask: Post");
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.length() == 2) {
				refreshStatus(result);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("ติดต่อบอร์ดไม่ได้");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("ติดต่อบอร์ดไม่ได้");
				else
					alert.setMessage("Error: " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
			}
			ready = true;
		}
	}
	
	private class SwitchTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private String switchPin;
		private int switchIndex;
		private ProgressDialog dialog;
		
		public SwitchTask(Context context) {
			this.context = context;
			this.switchPin = String.valueOf(10);
			this.switchIndex = 1;
			
			dialog = new ProgressDialog(SwitchActivity.this);
			dialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			Log.d("p", "SwitchTask: Pre");
			ready = false;
			
			dialog.setMessage("Sending command...");
			if (!dialog.isShowing()) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.d("p", "SwitchTask: In");
			return Service.sendHttpRequest(context, switchPin, Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("p", "SwitchTask: Post");
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.length() == 2) {
				refreshStatus(result);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("ติดต่อบอร์ดไม่ได้");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("ติดต่อบอร์ดไม่ได้");
				else
					alert.setMessage("Error: " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
			}
			ready = true;
		}
	}
}
