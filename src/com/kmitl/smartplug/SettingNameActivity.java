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

public class SettingNameActivity extends Activity {
	
	private EditText editTextName;
	private Button buttonSetName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_wifi);
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		
		buttonSetName = (Button) findViewById(R.id.buttonSetName);
		buttonSetName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
            	SetWifiTask task = new SetWifiTask(getApplicationContext(), editTextName.getText().toString());
            	task.execute();
			}
		});
	}
	
	private class SetWifiTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private String name;
		
		private ProgressDialog dialog;
		
		public SetWifiTask(Context context, String name) {
			this.context = context;
			this.name = name;
			
			dialog = new ProgressDialog(SettingNameActivity.this);
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
			return Service.sendHttpRequest(context, "0" + name + "&", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
            }
			
			if (result.equals(name)) {
				Toast.makeText(context, "Set name to " + name, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
