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
	private Button buttonRefresh;
	private ImageView[] imageViewSwitch;
	private ImageView[] imageViewBulb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);
		
		imageViewSwitch = new ImageView[4];
		imageViewBulb = new ImageView[4];
		
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
		buttonRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckStateTask task = new CheckStateTask(getApplicationContext(), true);
            	task.execute();
			}
		});
		
		imageViewSwitch[0] = (ImageView) findViewById(R.id.imageViewSwitch1);
		imageViewSwitch[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ready) {
					SwitchTask task = new SwitchTask(getApplicationContext(), 1);
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Sending in progress, please wait and try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		imageViewSwitch[1] = (ImageView) findViewById(R.id.imageViewSwitch2);
		imageViewSwitch[1].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ready) {
					SwitchTask task = new SwitchTask(getApplicationContext(), 2);
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Sending in progress, please wait and try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		imageViewSwitch[2] = (ImageView) findViewById(R.id.imageViewSwitch3);
		imageViewSwitch[2].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ready) {
					SwitchTask task = new SwitchTask(getApplicationContext(), 3);
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Sending in progress, please wait and try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		imageViewSwitch[3] = (ImageView) findViewById(R.id.imageViewSwitch4);
		imageViewSwitch[3].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ready) {
					SwitchTask task = new SwitchTask(getApplicationContext(), 4);
					task.execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Sending in progress, please wait and try again.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		imageViewBulb[0] = (ImageView) findViewById(R.id.imageViewBulb1);
		imageViewBulb[1] = (ImageView) findViewById(R.id.imageViewBulb2);
		imageViewBulb[2] = (ImageView) findViewById(R.id.imageViewBulb3);
		imageViewBulb[3] = (ImageView) findViewById(R.id.imageViewBulb4);
		
		setRepeatingAsyncTask();
	}
	
	private boolean firstChecking = true;
	private void setRepeatingAsyncTask() {

	    final Handler handler = new Handler();
	    Timer timer = new Timer();

	    TimerTask task = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	            	public void run() {
	            		if (ready) {
		                	CheckStateTask task = new CheckStateTask(getApplicationContext(), firstChecking);
		                	task.execute();
		                	firstChecking = false;
	            		}
	                }
	            });
	        }
	    };

	    timer.schedule(task, 1 * 1000, 15 * 1000);
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
			ready = false;
			
			textViewStatus.setText("Checking...");
						
			dialog.setMessage("Checking state...");
			if (!dialog.isShowing() && showDialog) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "Z4");
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.length() == 8) {
				if (result.charAt(4) == '0') {
					imageViewSwitch[0].setImageResource(R.drawable.switch_off);
				}
				else {
					imageViewSwitch[0].setImageResource(R.drawable.switch_on);
				}

				if (result.charAt(5) == '0') {
					imageViewSwitch[1].setImageResource(R.drawable.switch_off);
				}
				else {
					imageViewSwitch[1].setImageResource(R.drawable.switch_on);
				}
				
				if (result.charAt(6) == '0') {
					imageViewSwitch[2].setImageResource(R.drawable.switch_off);
				}
				else {
					imageViewSwitch[2].setImageResource(R.drawable.switch_on);
				}
				
				if (result.charAt(7) == '0') {
					imageViewSwitch[3].setImageResource(R.drawable.switch_off);
				}
				else {
					imageViewSwitch[3].setImageResource(R.drawable.switch_on);
				}
				
				if (result.charAt(0) == '0') {
					imageViewBulb[0].setImageResource(R.drawable.bulb_off);
				}
				else {
					imageViewBulb[0].setImageResource(R.drawable.bulb_on);
				}

				if (result.charAt(1) == '0') {
					imageViewBulb[1].setImageResource(R.drawable.bulb_off);
				}
				else {
					imageViewBulb[1].setImageResource(R.drawable.bulb_on);
				}
				
				if (result.charAt(2) == '0') {
					imageViewBulb[2].setImageResource(R.drawable.bulb_off);
				}
				else {
					imageViewBulb[2].setImageResource(R.drawable.bulb_on);
				}
				
				if (result.charAt(3) == '0') {
					imageViewBulb[3].setImageResource(R.drawable.bulb_off);
				}
				else {
					imageViewBulb[3].setImageResource(R.drawable.bulb_on);
				}
				
				textViewStatus.setText("Ready: " + result);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
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
		
		public SwitchTask(Context context, int switchNumber) {
			this.context = context;
			this.switchPin = String.valueOf(switchNumber + 9);
			this.switchIndex = switchNumber - 1;
			
			dialog = new ProgressDialog(SwitchActivity.this);
			dialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			ready = false;
			textViewStatus.setText("Sending command...");
			
			dialog.setMessage("Sending command...");
			if (!dialog.isShowing()) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, switchPin);
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
//			if (result.length() == 4) {
//				try {
//					Thread.sleep(1000);
					CheckStateTask task = new CheckStateTask(getApplicationContext(), true);
	            	task.execute();
//				} catch(InterruptedException e){
//					ready = true;
//				}
//				
//				
//				textViewStatus.setText("Ready");
//			}
//			else {
//				AlertDialog d;
//				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
//				alert.setMessage("Error: " + result);
//				alert.setCancelable(true);
//				d = alert.create();
//				d.setCanceledOnTouchOutside(true);
//				d.show();
//				
//				textViewStatus.setText("Ready");
//			}
//			
//			ready = true;
		}
	}
}
