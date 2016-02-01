package com.kmitl.smartplug;

import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class SwitchTaskForService extends AsyncTask<Void, Void, String> {
	
	private Context context;
	private boolean toState;
	
	public SwitchTaskForService(Context context, boolean toState) {
		this.context = context;
		this.toState = toState;
	}
	
	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected String doInBackground(Void... params) {
		return Service.sendHttpRequest(context, "9" + (toState ? "1" : "0"), Service.SOCKET_TIMEOUT_TRYING);
	}

	@Override
	protected void onPostExecute(String result) {

		Toast.makeText(context, "alarm: " + (toState ? "1" : "0") + " " + new Date(), Toast.LENGTH_SHORT).show();
	}
}
