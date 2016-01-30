package com.kmitl.smartplug;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SwitchReceiver extends BroadcastReceiver {
	
	private static int i = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Date now = new Date();
		
		String dt = SharedValues.sdf.format(now);
		String t = SharedValues.sdf_everyday.format(now);
		
		DateTimeItem dti = new DateTimeItem(dt, false);
		DateTimeItem ti = new DateTimeItem(t, false);
		
		ArrayList<DateTimeItem> dtl = SharedValues.getDateTimeList(context, SharedValues.KEY_ONETIME);
		ArrayList<DateTimeItem> tl = SharedValues.getDateTimeList(context, SharedValues.KEY_EVERYDAY);
//		Toast.makeText(context, "check alarm: " + new Date(), Toast.LENGTH_SHORT).show();
		
		for (int i = 0; i < dtl.size(); i++) {
			if (dtl.get(i).getDateTime().compareTo(dti.getDateTime()) < 0) {
				SharedValues.removeDateTime(context, SharedValues.KEY_ONETIME, dtl.get(i));
			}
			if (dtl.get(i).getDateTime().equals(dti.getDateTime())) {
				SwitchTask task = new SwitchTask(context, dtl.get(i).getState());
				SharedValues.removeDateTime(context, SharedValues.KEY_ONETIME, dtl.get(i));
				task.execute();
				break;
			}
		}
		
		for (int i = 0; i < tl.size(); i++) {
			if (tl.get(i).getDateTime().equals(ti.getDateTime())) {
				SwitchTask task = new SwitchTask(context, tl.get(i).getState());
				task.execute();
				break;
			}
		}
		
		
	}
	
private class SwitchTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private boolean toState;
		
		public SwitchTask(Context context, boolean toState) {
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

}
