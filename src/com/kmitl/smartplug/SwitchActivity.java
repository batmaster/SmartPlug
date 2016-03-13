package com.kmitl.smartplug;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SwitchActivity extends Activity {
	
	private TextView textView0;
	private ImageView imageViewSwitch;
	private ImageView imageViewBulb;
	private ImageView imageViewLocation;
	private ImageView imageViewSetAlarm;
	private ImageView imageViewSetWifi;
	private ImageView imageViewCheckUnit;
	
	public static SwitchActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);
		
		activity = SwitchActivity.this;
		
		textView0 = (TextView) findViewById(R.id.textView0);
		textView0.setText((SharedValues.getModePref(getApplicationContext()).equals("direct") ? "Direct Mode" : "Global Mode") + " ON/OFF");
		
		imageViewLocation = (ImageView) findViewById(R.id.imageViewLocation);
		imageViewLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
				AskWifiSSID task = new AskWifiSSID(getApplicationContext());
				task.execute();
			}
		});
		imageViewSetWifi.setVisibility(SharedValues.getModePref(getApplicationContext()).equals("global") ? View.VISIBLE : View.GONE);
		
		imageViewCheckUnit = (ImageView) findViewById(R.id.imageViewCheckUnit);
		imageViewCheckUnit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				double I = new LogIDBHelper(getApplicationContext()).getAllI();
				
				final Dialog dialog = new Dialog(SwitchActivity.this);
	            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	            dialog.setContentView(R.layout.custom_dialog_unit);
	            dialog.setCancelable(true);
	            
	            final double unit = I * 0.00383333;

	            TextView textViewUnit = (TextView) dialog.findViewById(R.id.textViewUnit);
	            textViewUnit.setText(String.valueOf(unit));
	            final EditText editTextU = (EditText) dialog.findViewById(R.id.editTextU);
	            final TextView textViewBaht = (TextView) dialog.findViewById(R.id.textViewBaht);
	            
	            editTextU.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						double baht = unit * Double.parseDouble(editTextU.getText().toString());
						textViewBaht.setText(String.format("%.2f ฿", baht));
						return false;
					}
				});
	            
	            dialog.show();
			}
		});
		
		refreshStatus(getIntent().getStringExtra("the8Digits"), false);
	}
	
	private void refreshStatus(String the8Digits, boolean isShowDialog) {
			imageViewSwitch.setImageResource(the8Digits.charAt(1) == '0' ? R.drawable.switch_off : R.drawable.switch_on);
			imageViewBulb.setImageResource(the8Digits.charAt(0) == '0' ? R.drawable.bulb_off : R.drawable.bulb_on);
			
			if (isShowDialog) {
				if (the8Digits.charAt(0) != the8Digits.charAt(1))
					SharedValues.showDialog(SwitchActivity.this, "Appliance has problem!");
				else if (the8Digits.charAt(0) == '1')
					SharedValues.showDialog(SwitchActivity.this, "Appliance is using");
				else
					SharedValues.showDialog(SwitchActivity.this, "Appliance is not use");
			}
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
				refreshStatus(result, true);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("เธ•เธดเธ”เธ•เน�เธญเธ�เธญเธฃเน�เธ”เน�เธกเน�เน�เธ”เน�");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("เธ•เธดเธ”เธ•เน�เธญเธ�เธญเธฃเน�เธ”เน�เธกเน�เน�เธ”เน�");
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
				refreshStatus(result, false);
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(SwitchActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("เธ•เธดเธ”เธ•เน�เธญเธ�เธญเธฃเน�เธ”เน�เธกเน�เน�เธ”เน�");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("เธ•เธดเธ”เธ•เน�เธญเธ�เธญเธฃเน�เธ”เน�เธกเน�เน�เธ”เน�");
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
	
	private class AskWifiSSID extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private String ssid;
		private String password;
		private Dialog outerDialog;
		
		private ProgressDialog dialog;
		
		public AskWifiSSID(Context context) {
			this.context = context;
			
			dialog = new ProgressDialog(SwitchActivity.this);
			dialog.setCancelable(true);
		}
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Trying to connect...");
			
			if (!dialog.isShowing()) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "7", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			String[] ssids = result.split(",");
			
			final Dialog dialog = new Dialog(SwitchActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_location);
            dialog.setCancelable(true);
            
            final Switch switchEnable = (Switch) findViewById(R.id.switchEnable);
            
            Button buttonSetCenter = (Button) findViewById(R.id.buttonSetCenter);
            buttonSetCenter.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
            
            final EditText editTextRange = (EditText) findViewById(R.id.editTextRange);
            
            Button buttonCheck = (Button) findViewById(R.id.buttonCheck);
            buttonCheck.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
            
            Button buttonCancel = (Button)dialog.findViewById(R.id.buttonCancel);
            buttonCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	
                    dialog.cancel();
                    
                }
            });
            
            Button buttonSet = (Button)dialog.findViewById(R.id.buttonSet);
            buttonSet.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	
                }
            });

            dialog.show();
		}
	}
	
	private class SetWifiTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private String ssid;
		private String password;
		private Dialog outerDialog;
		
		private ProgressDialog dialog;
		
		public SetWifiTask(Context context, String ssid, String password, Dialog outerDialog) {
			this.context = context;
			this.ssid = ssid;
			this.password = password;
			this.outerDialog = outerDialog;
			
			dialog = new ProgressDialog(SwitchActivity.this);
			dialog.setCancelable(true);
		}
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Trying to connect...");
			
			if (!dialog.isShowing()) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "8&" + ssid + "&" + password + "&", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.equals(ssid + "-" + password)) {
				outerDialog.dismiss();
				
				Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
				SwitchActivity.this.startActivity(intent);
				
				SwitchActivity.activity.finish();
				SwitchActivity.this.finish();
			}
		}
	}
	
	private boolean onBoot = true;
	
	@Override
	protected void onResume() {
		if (!onBoot) {
			CheckStateTask task = new CheckStateTask(getApplicationContext(), false);
	    	task.execute();
		}
		else {
			onBoot = false;
		}
		
		super.onResume();
	}
	
}
