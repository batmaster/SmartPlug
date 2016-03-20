package com.kmitl.smartplug;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
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
			
			checkGPS(context, false);
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
				SharedValues.setEnableLocation(context, false);
				SwitchTaskForService task = new SwitchTaskForService(context, dtl.get(i).getState());
				SharedValues.removeDateTime(context, SharedValues.KEY_ONETIME, dtl.get(i));
				task.execute();
				break;
			}
		}
		//0.0018783317
		
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
			try {
				Log.d("LogI", "I: " + result);
				double I = Double.parseDouble(result);
				
				LogIDBHelper db = new LogIDBHelper(context);
				db.addLogI(I);
			}
			catch (Exception e) {
				
			}
		}
	}
	
	public final static double AVERAGE_RADIUS_OF_EARTH = 6371000;
	public int calculateDistance(double userLat, double userLng,
	  double venueLat, double venueLng) {

	    double latDistance = Math.toRadians(userLat - venueLat);
	    double lngDistance = Math.toRadians(userLng - venueLng);

	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	      + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
	      * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
	}
	
	private void checkGPS(Context context, final boolean forceClose) {
		GPSTracker gpsTracker = new GPSTracker(context);
		if (gpsTracker.canGetLocation()) {
			boolean enable = SharedValues.getEnableLocation(context);
			double offRange = SharedValues.getOffRange(context);
			double lat = SharedValues.getLat(context);
			double lng = SharedValues.getLng(context);
			
			Log.d("locationn", "setting is: " + enable + " offRange: " + offRange + " lat: " + lat + " lng: " + lng);
			if (enable && offRange != 0 && lat != 0 && lng != 0) {
				double distance = calculateDistance(lat, lng, gpsTracker.getLatitude(), gpsTracker.getLongitude());
				Log.d("locationn", "getting lat: " + gpsTracker.getLatitude() + " lng: " + gpsTracker.getLongitude());
				Log.d("locationn", "getting distance: " + distance);
				
				if (distance >= offRange) {
					Log.d("locationn", "distance >= offRange");
					SwitchCloseTask task = new SwitchCloseTask(context);
					task.execute();
				}
			}	
		}
	}
	
	private class SwitchCloseTask extends AsyncTask<Void, Void, String> {
		
		private Context context;
		private String switchPin;
		
		public SwitchCloseTask(Context context) {
			this.context = context;
			this.switchPin = String.valueOf(10);
		}
		
		@Override
		protected void onPreExecute() {
			Log.d("p", "SwitchTask: Pre");
		}

		@Override
		protected String doInBackground(Void... params) {
			return Service.sendHttpRequest(context, switchPin, Service.SOCKET_TIMEOUT_TRYING);
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("p", "SwitchTask: Post");
			Toast.makeText(context, "Close switch remotely!", Toast.LENGTH_SHORT).show();
		}
	}
	
}