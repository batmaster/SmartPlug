package com.kmitl.smartplug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SharedValues {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	public static final SimpleDateFormat sdf_everyday = new SimpleDateFormat("HH:mm");
	
	public static String KEY_EVERYDAY = "KEY_EVERYDAY";
	public static String KEY_ONETIME = "KEY_ONETIME";
	
	private SharedValues () {
		
	}
	
	public static void addDateTime(Context context, String key, DateTimeItem dti) {
		ArrayList<DateTimeItem> dtl = getDateTimeList(context, key);
		if (!dtl.contains(dti))
			dtl.add(dti);
		Collections.sort(dtl);
		
		SharedPreferences sp = context.getSharedPreferences("smartplug", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		Gson gson = new Gson();
		String json = gson.toJson(dtl);
		editor.putString(key, json);
		editor.commit();
	}
	
	public static void removeDateTime(Context context, String key, DateTimeItem dti) {
		ArrayList<DateTimeItem> dtl = getDateTimeList(context, key);
		dtl.remove(dti);
		
		SharedPreferences sp = context.getSharedPreferences("smartplug", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		Gson gson = new Gson();
		String json = gson.toJson(dtl);
		editor.putString(key, json);
		editor.commit();
		
	}
	
	public static ArrayList<DateTimeItem> getDateTimeList(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("smartplug", Context.MODE_PRIVATE);
		String json = sp.getString(key, "");
		Gson gson = new Gson();
		ArrayList<DateTimeItem> dtl = (ArrayList<DateTimeItem>) gson.fromJson(json, new TypeToken<ArrayList<DateTimeItem>>(){}.getType());
		
		if (dtl == null)
			dtl = new ArrayList<DateTimeItem>();
		
		return dtl;
	}
//	
//	public static void setStringPref(Context context, String key, String value) {
//		SharedPreferences sp = context.getSharedPreferences(TOT_PREF_SETTINGS, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = sp.edit();
//		editor.putString(key, value);
//		editor.commit();
//	}
//
//	public static String getStringPref(Context context, String key) {
//		SharedPreferences sp = context.getSharedPreferences(TOT_PREF_SETTINGS, Context.MODE_PRIVATE);
//		return sp.getString(key, null);
//	}
//
//	public static boolean getStateProvince(Context context, String province) {
//		return getBooleanPref(context, province);
//	}
	
}
