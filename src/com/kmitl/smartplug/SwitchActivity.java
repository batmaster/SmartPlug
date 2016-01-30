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
	
	private TextView textViewStatus;
	private ImageView imageViewRefresh;
	private ImageView imageViewAlarm;
	private ImageView imageViewSetting;
	private ImageView imageViewSwitch;
	private ImageView imageViewBulb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);
		
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		
		imageViewRefresh = (ImageView) findViewById(R.id.imageViewRefresh);
		imageViewRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckStateTask task = new CheckStateTask(getApplicationContext(), true);
            	task.execute();
			}
		});
		
		imageViewAlarm = (ImageView) findViewById(R.id.imageViewAlarm);
		imageViewAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
				startActivity(intent);
			}
		});
		
		imageViewSetting = (ImageView) findViewById(R.id.imageViewSetting);
		imageViewSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(intent);
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
		
//		setRepeatingAsyncTask();
		refreshStatus(getIntent().getStringExtra("the8Digits"));
	}
	
	private void setRepeatingAsyncTask() {

	    final Handler handler = new Handler();
	    Timer timer = new Timer();

	    TimerTask task = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	            	public void run() {
	            		if (ready) {
		                	CheckStateTask task = new CheckStateTask(getApplicationContext(), false);
		                	task.execute();
	            		}
	                }
	            });
	        }
	    };

	    timer.schedule(task, 1 * 1000, 15 * 1000);
	}
	
	private void refreshStatus(String the8Digits) {
			imageViewSwitch.setImageResource(the8Digits.charAt(1) == '1' ? R.drawable.switch_off : R.drawable.switch_on);
			imageViewBulb.setImageResource(the8Digits.charAt(0) == '0' ? R.drawable.bulb_off : R.drawable.bulb_on);
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
			
			textViewStatus.setText("Checking...");
						
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
			
			if (result.length() == 8 || result.length() == 2) {
				refreshStatus(result);
				
				textViewStatus.setText("Ready: " + result);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("เชื่อมต่อไม่ได้");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("ลองหลายรอบแล้ว");
				else
					alert.setMessage("Error: " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
				
				textViewStatus.setText("Ready: " + result);
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
			textViewStatus.setText("Sending command...");
			
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
				imageViewSwitch.setImageResource(result.charAt(1) == '0' ? R.drawable.switch_off : R.drawable.switch_on);
				imageViewBulb.setImageResource(result.charAt(0) == '0' ? R.drawable.bulb_off : R.drawable.bulb_on);
				
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("เชื่อมต่อไม่ได้");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("ลองหลายรอบแล้ว");
				else
					alert.setMessage("Error: " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
			}			
			
			textViewStatus.setText("Ready");
			ready = true;
		}
	}
}
