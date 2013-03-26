package com.example.gpsdata;

import java.util.Iterator;
import java.util.List;

import com.example.gpsdata.MainActivity;
import com.example.gpsdata.MainActivity.LocEntry;
import com.example.gpsdata.MainActivity.SatEntry;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.location.GpsSatellite;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gpsdata.sqlite"; 
	private static final String DATABASE_PATH = "/data/data/com.example.gpsdata/databases/";
    private static final int DATABASE_VERSION = 1;  
    private static String TABLE_NAME;  
    public static final String KEY_ID = "id";  
    //public static final String KEY_EXPID = "expid"; 
    public static final String KEY_PRN = "prn";  
    public static final String KEY_AZIMUTH = "azimuth"; 
    public static final String KEY_ELEVATION = "elevation"; 
    public static final String KEY_SNR = "snr";
   // public static final String KEY_COUNTER = "counter"; // counter for each 
    public static final String KEY_LATITUDE = "lati";
    public static final String KEY_LONGITUDE = "longi";
    public static final String KEY_ALTITUDE = "alti";
    public static final String KEY_TIME = "time";  //timestamp
	
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		TABLE_NAME = MainActivity.experimentId;
		String sql1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_SAT") + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_PRN + " INTEGER, " + KEY_AZIMUTH + " REAL, "
				+ KEY_ELEVATION + " INTEGER , "+  KEY_SNR +" REAL, " + KEY_TIME + " INTEGER);";  
		String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_LOC") + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_LATITUDE + " REAL DEFAULT 0, " + KEY_LONGITUDE + " REAL DEFAULT 0, " + KEY_ALTITUDE + " REAL DEFAULT 0, "
				+ KEY_TIME + " INTEGER);";  
	    try{
	    	db.execSQL(sql1);
	    	db.execSQL(sql2);
	    }catch(SQLiteException e) {
	        Log.e("dberr", e.toString());
	    }
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME.concat("_SAT");
		String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME.concat("_LOC");
        db.execSQL(sql1); 
        db.execSQL(sql2); 
        onCreate(db);
	}

	// insert a SatEntry with full info
	public void addSatEntry(List<SatEntry> sList) {  
		SQLiteDatabase db = this.getWritableDatabase(); 
		/* ContentValues */  
		ContentValues cv = new ContentValues();
		Iterator<SatEntry> it = sList.iterator();
		while(it.hasNext()){
			SatEntry s = (SatEntry) it.next();
			//cv.put(KEY_EXPID, expid);
			cv.put(KEY_PRN, s.getPRN());
			cv.put(KEY_AZIMUTH, s.getAzimuth()); 
			cv.put(KEY_ELEVATION, s.getElevation());
			cv.put(KEY_SNR, s.getSNR()); 
			cv.put(KEY_TIME, s.getLocalTime());
			long row = db.insert(TABLE_NAME.concat("_SAT"), null, cv); 
		}
		db.close(); 
	}  
	
	// insert a LocEntry with full info
	public long addLocEntry(LocEntry l) {  
		SQLiteDatabase db = this.getWritableDatabase();  
		/* ContentValues */  
		ContentValues cv = new ContentValues();  
		//cv.put(KEY_EXPID, expid);
		cv.put(KEY_LATITUDE, l.getLati());
		cv.put(KEY_LONGITUDE, l.getLongi());
		cv.put(KEY_ALTITUDE, l.getAlti());
		cv.put(KEY_TIME, l.getLocalTime());
		long row = db.insert(TABLE_NAME.concat("_LOC"), null, cv);  
		db.close();
		return row;  
	}  
		
		
}
