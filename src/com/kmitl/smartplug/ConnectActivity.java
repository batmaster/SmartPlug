package com.kmitl.smartplug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
			dialog.setMessage("Trying to connect...");
			
			if (!dialog.isShowing()) {
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
			
			if (result.length() == 4) {
				Intent intent = new Intent(context, SwitchActivity.class);
				startActivity(intent);
				finish();
			}
			else {
				AlertDialog d;
				AlertDialog.Builder alert = new AlertDialog.Builder(ConnectActivity.this);
				alert.setMessage("Connection error: " + result);
				alert.setCancelable(true);
				d = alert.create();
				d.setCanceledOnTouchOutside(true);
				d.show();
			}
			
		}
	}
}
