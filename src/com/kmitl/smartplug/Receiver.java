package com.kmitl.smartplug;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			context.startService(new Intent(context, SwitchService.class));
		}
		else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			checkAlarm(context);
			checkI(context);
			
		}
	}
	
	private void checkAlarm(Context context) {
		Date now = new Date();
		
		String dt = SharedValues.sdf.format(now);
		//String t = SharedValues.sdf_everyday.format(now);
		
		DateTimeItem dti = new DateTimeItem(dt, false);
		//DateTimeItem ti = new DateTimeItem(t, false);
		
		ArrayList<DateTimeItem> dtl = SharedValues.getDateTimeList(context, SharedValues.KEY_ONETIME);
		//ArrayList<DateTimeItem> tl = SharedValues.getDateTimeList(context, SharedValues.KEY_EVERYDAY);
		
		for (int i = 0; i < dtl.size(); i++) {
			if (dtl.get(i).getDateTime().compareTo(dti.getDateTime()) < 0) {
				SharedValues.removeDateTime(context, SharedValues.KEY_ONETIME, dtl.get(i));
			}
			if (dtl.get(i).getDateTime().equals(dti.getDateTime())) {
				SwitchTaskForService task = new SwitchTaskForService(context, dtl.get(i).getState());
				SharedValues.removeDateTime(context, SharedValues.KEY_ONETIME, dtl.get(i));
				task.execute();
				break;
			}
		}
		
		/*for (int i = 0; i < tl.size(); i++) {
			if (tl.get(i).getDateTime().equals(ti.getDateTime())) {
				SwitchTaskForService task = new SwitchTaskForService(context, tl.get(i).getState());
				task.execute();
				break;
			}
		}*/
	}
	
	private void checkI(Context context) {
		CheckITask task = new CheckITask(context);
		task.execute();
	}
	
	private class SwitchTaskForService extends AsyncTask<Void, Void, String> {
		
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
	
	private class CheckITask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		
		public CheckITask(Context context) {
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, "6", Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			double I = Double.parseDouble(result);
			
			LogIDBHelper db = new LogIDBHelper(context);
			db.addLogI(I);
		}
	}

}