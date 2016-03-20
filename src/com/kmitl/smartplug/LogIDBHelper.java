package com.kmitl.smartplug;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LogIDBHelper extends SQLiteOpenHelper {
	
	 private static final String DB_NAME = "SmartPlug";  
	 private static final int DB_VERSION = 1;  
	 public static final String TABLE_NAME = "LogI";  

	public LogIDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);  
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, i DOUBLE, date STRING)", TABLE_NAME));  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void addLogI(double I) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(String.format("INSERT INTO %s (i, date) VALUES (%f, '%s')", TABLE_NAME, I, new Date().toString()));
		db.close();
	}
	
	public double getAllI() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(String.format("SELECT SUM(i) FROM %s", TABLE_NAME), null);
		cursor.moveToFirst();
		double i = cursor.getDouble(0);
		cursor.close();
		db.close();
		
		return i;
	}

}
