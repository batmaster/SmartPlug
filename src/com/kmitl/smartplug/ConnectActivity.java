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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends Activity {
	
	private TextView textView0;
	private EditText editTextIp;
	private ImageView imageView1;
	private EditText editTextPort;
	private ImageView imageView2;
	private Button buttonClear;
	private Button buttonConnect;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		textView0 = (TextView) findViewById(R.id.textView0);
		textView0.setText(SharedValues.getModePref(getApplicationContext()).equals("direct") ? "Direct Mode" : "Global Mode");
		
		editTextIp = (EditText) findViewById(R.id.editTextIp);
		editTextIp.setText(Service.getPreference(getApplicationContext(), "ip"));
		
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedValues.showDialog(ConnectActivity.this, SharedValues.getModePref(getApplicationContext()).equals("direct") ? "Your private IP" : "Your public IP");
			}
		});
		
		editTextPort = (EditText) findViewById(R.id.editTextPort);
		editTextPort.setText(Service.getPreference(getApplicationContext(), "port"));
		
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedValues.showDialog(ConnectActivity.this, "Port from Smart Plug");
			}
		});
		
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
