package com.kmitl.smartplug;

import java.net.SocketException;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectActivity extends Activity {
	
	private EditText editTextIp;
	private EditText editTextPort;
	private Button buttonClear;
	private Button buttonConnect;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		editTextIp = (EditText) findViewById(R.id.editTextIp);
		editTextIp.setText(Service.getPreference(getApplicationContext(), "ip"));
		
		editTextPort = (EditText) findViewById(R.id.editTextPort);
		editTextPort.setText(Service.getPreference(getApplicationContext(), "port"));
		
		buttonClear = (Button) findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextIp.setText("");
				editTextPort.setText("");
				editTextIp.requestFocus();
			}
		});
		
		buttonConnect = (Button) findViewById(R.id.buttonConnect);
		buttonConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Service.setPerference(getApplicationContext(), "ip", editTextIp.getText().toString());
				Service.setPerference(getApplicationContext(), "port", editTextPort.getText().toString());
				
				TryToConnectTask task = new TryToConnectTask(getApplicationContext());
				task.execute();
				
//				Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
//				startActivity(intent);
				
//				Intent intent = new Intent(getApplicationContext(), SwitchActivity.class);
//				intent.putExtra("the8Digits", "00");
//				startActivity(intent);
//				finish();
			}
		});
		
		boolean start = isServiceRunning(SwitchReceiver.class);
		if (!start) {
			Toast.makeText(getApplicationContext(), "start from main", Toast.LENGTH_SHORT).show();
			
			Intent alarmIntent = new Intent(getApplicationContext(), SwitchReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 19096, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 20000, pendingIntent);
		}
	}
	
	private boolean isServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private class TryToConnectTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private ProgressDialog dialog;
		
		public TryToConnectTask(Context context) {
			this.context = context;
			
			dialog = new ProgressDialog(ConnectActivity.this);
			dialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			Log.d("p", "TryToConnectTask: Pre");
			dialog.setMessage("Trying to connect...");
			
			if (!dialog.isShowing()) {
				dialog.show();
            }
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.d("p", "TryToConnectTask: In");
			
			return Service.sendHttpRequest(context, "4", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("p", "TryToConnectTask: Post");
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.length() == 2 || result.length() == 8) {
				Intent intent = new Intent(context, SwitchActivity.class);
				intent.putExtra("the8Digits", result);
				startActivity(intent);
				finish();
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(ConnectActivity.this);
				if (result.equals("ConnectTimeoutException"))
					alert.setMessage("เชื่อมต่อไม่ได้");
				else if (result.equals("SocketTimeoutException"))
					alert.setMessage("ลองหลายรอบแล้ว");
				else
					alert.setMessage("Connection error : " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
			}
			
		}
	}
}
