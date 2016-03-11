package com.kmitl.smartplug;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	private TextView textView0;
	private Button buttonSetWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		textView0 = (TextView) findViewById(R.id.textView0);
		textView0.setText((SharedValues.getModePref(getApplicationContext()).equals("direct") ? "Direct Mode" : "Global Mode") + " WiFi Setting");
		
		buttonSetWifi = (Button) findViewById(R.id.buttonSetWifi);
		buttonSetWifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(SettingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_setting);
                dialog.setCancelable(true);
                
                final EditText editTextSsid = (EditText) dialog.findViewById(R.id.editTextSsid);
                final EditText editTextPassword = (EditText) dialog.findViewById(R.id.editTextPassword);
                
                Button buttonCancel = (Button)dialog.findViewById(R.id.buttonCancel);
                buttonCancel.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                    	
                        dialog.cancel();
                        
                    }
                });
                
                Button buttonSet = (Button)dialog.findViewById(R.id.buttonSet);
                buttonSet.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                    	
                    	SetWifiTask task = new SetWifiTask(getApplicationContext(), editTextSsid.getText().toString(), editTextPassword.getText().toString(), dialog);
                    	task.execute();
                    	
                    }
                });

                dialog.show();
			}
		});
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
			
			dialog = new ProgressDialog(SettingActivity.this);
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
				
				Intent intent = new Intent(getApplicationContext(), BeforeConnectActivity.class);
				SettingActivity.this.startActivity(intent);
				
				SwitchActivity.activity.finish();
				SettingActivity.this.finish();
			}
		}
	}

}
