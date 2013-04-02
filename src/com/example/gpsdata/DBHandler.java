package com.example.gpsdata;

import java.util.Iterator;
import java.util.List;

import com.example.gpsdata.MainActivity;
import com.example.gpsdata.MainActivity.LocEntry;
import com.example.gpsdata.MainActivity.SatEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gpsdata.sqlite"; 
	//private static final String DATABASE_PATH = "/data/data/com.example.gpsdata/databases/";
    private static final int DATABASE_VERSION = 1;  
    private static final String TABLE_NAME = "TEST";  
    public static final String KEY_ID = "id";  
    public static final String KEY_EXPID = "expid"; 
    public static final String KEY_PRN = "prn";  
    public static final String KEY_AZIMUTH = "azimuth"; 
    public static final String KEY_ELEVATION = "elevation"; 
    public static final String KEY_SNR = "snr";
   // public static final String KEY_COUNTER = "counter"; // counter for each 
    public static final String KEY_LATITUDE = "lati";
    public static final String KEY_LONGITUDE = "longi";
    public static final String KEY_ALTITUDE = "alti";
    public static final String KEY_TIME = "time"; 
    //private InsertHelper m1Insert, m2Insert;
    
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//TABLE_NAME = MainActivity.experimentId;
		/*String sql1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_SAT") + " (" + KEY_ID + " INTEGER AUTOINCREMENT, "
				+ KEY_EXPID + " TEXT, " + KEY_PRN + " INTEGER, " + KEY_AZIMUTH + " REAL, "
				+ KEY_ELEVATION + " INTEGER, "+  KEY_SNR +" REAL, " + KEY_TIME + " INTEGER, PRIMARY KEY(" + KEY_ID + ", " + KEY_EXPID +"));";  
		String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_LOC") + " (" + KEY_ID + " INTEGER AUTOINCREMENT, "
				+ KEY_EXPID + " TEXT, "
				+ KEY_LATITUDE + " REAL DEFAULT 0, " + KEY_LONGITUDE + " REAL DEFAULT 0, " + KEY_ALTITUDE + " REAL DEFAULT 0, "
				+ KEY_TIME + " INTEGER, PRIMARY KEY(" + KEY_ID + ", "  + KEY_EXPID +"));";  */
		String sql1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_SAT") + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_EXPID + " TEXT, " + KEY_PRN + " INTEGER, " + KEY_AZIMUTH + " REAL, "
				+ KEY_ELEVATION + " INTEGER, "+  KEY_SNR +" REAL, " + KEY_TIME + " INTEGER);";  
		String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME.concat("_LOC") + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_EXPID + " TEXT, "
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
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //m1Insert = new InsertHelper(db, TABLE_NAME.concat("_SAT"));
        //m2Insert = new InsertHelper(db, TABLE_NAME.concat("_LOC"));
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME.concat("_SAT");
		String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME.concat("_LOC");
        db.execSQL(sql1); 
        db.execSQL(sql2); 
        onCreate(db);
	}

	// insert a SatList with full info
	public void addSatList(List<SatEntry> sList) {  
		if(sList.isEmpty()) return;
		SQLiteDatabase db = this.getWritableDatabase(); 
		db.beginTransaction();
		try{
			ContentValues cv = new ContentValues();			
			Iterator<SatEntry> it = sList.iterator();
			while(it.hasNext()){			
				SatEntry s = (SatEntry) it.next();
				cv.clear();
				cv.put(KEY_EXPID, MainActivity.experimentId);
				cv.put(KEY_PRN, s.getPRN());
				cv.put(KEY_AZIMUTH, s.getAzimuth()); 
				cv.put(KEY_ELEVATION, s.getElevation());
				cv.put(KEY_SNR, s.getSNR()); 
				cv.put(KEY_TIME, s.getLocalTime());
				db.insert(TABLE_NAME.concat("_SAT"), null, cv);
				//m1Insert.insert(cv);
			}	
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		//db.close(); 
	}  
	
	// insert a SatEntry with full info
	public void addSatEntry(SatEntry s) {  
		
		SQLiteDatabase db = this.getWritableDatabase(); 
		
		ContentValues cv = new ContentValues();			
		cv.put(KEY_EXPID, MainActivity.experimentId);
		cv.put(KEY_PRN, s.getPRN());
		cv.put(KEY_AZIMUTH, s.getAzimuth()); 
		cv.put(KEY_ELEVATION, s.getElevation());
		cv.put(KEY_SNR, s.getSNR()); 
		cv.put(KEY_TIME, s.getLocalTime());
		db.insert(TABLE_NAME.concat("_SAT"), null, cv);
		//m1Insert.insert(cv);
		//db.close(); 
	}  
	
	// insert a LocEntry with full info
	public void addLocEntry(LocEntry l) {  
		SQLiteDatabase db = this.getWritableDatabase();  
		
		ContentValues cv = new ContentValues();  
		cv.put(KEY_EXPID, MainActivity.experimentId);
		cv.put(KEY_LATITUDE, l.getLati());
		cv.put(KEY_LONGITUDE, l.getLongi());
		cv.put(KEY_ALTITUDE, l.getAlti());
		cv.put(KEY_TIME, l.getLocalTime());
		db.insert(TABLE_NAME.concat("_LOC"), null, cv);
		//m2Insert.insert(cv);
		//db.close(); 
	}  
		
	// delete entries on experimentId
    public void fallback() {  
       SQLiteDatabase db = this.getWritableDatabase();  
       String where = KEY_EXPID + " = ?";  
       String[] whereValue ={ MainActivity.experimentId }; 
       db.beginTransaction();
       try{
    	   db.delete(TABLE_NAME.concat("_SAT"), where, whereValue); 
    	   db.delete(TABLE_NAME.concat("_LOC"), where, whereValue);
    	   db.setTransactionSuccessful();
       }finally{
    	   db.endTransaction();
       }
		//db.close();
    }  
	
/*	public void addEntry(List<SatEntry> sList, LocEntry l) {  
		SQLiteDatabase db = this.getWritableDatabase(); 
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		Iterator<SatEntry> it = sList.iterator();
		
		if(sList.isEmpty()) {
			cv.put(KEY_EXPID, MainActivity.experimentId);
			cv.put(KEY_PRN, 0);
			cv.put(KEY_AZIMUTH, 0); 
			cv.put(KEY_ELEVATION, 0);
			cv.put(KEY_SNR, 0); 
			cv.put(KEY_TIME, 0);
			db.insert(TABLE_NAME.concat("_SAT"), null, cv);
			//m1Insert.insert(cv);
		}

		while(it.hasNext()){			
			SatEntry s = (SatEntry) it.next();
			cv.clear();
			cv.put(KEY_EXPID, MainActivity.experimentId);
			cv.put(KEY_PRN, s.getPRN());
			cv.put(KEY_AZIMUTH, s.getAzimuth()); 
			cv.put(KEY_ELEVATION, s.getElevation());
			cv.put(KEY_SNR, s.getSNR()); 
			cv.put(KEY_TIME, s.getLocalTime());
			db.insert(TABLE_NAME.concat("_SAT"), null, cv);
			//m1Insert.insert(cv);
		}
		
		ContentValues cv2 = new ContentValues();  
		cv2.put(KEY_EXPID, MainActivity.experimentId);
		cv2.put(KEY_LATITUDE, l.getLati());
		cv2.put(KEY_LONGITUDE, l.getLongi());
		cv2.put(KEY_ALTITUDE, l.getAlti());
		cv2.put(KEY_TIME, l.getLocalTime());
		db.insert(TABLE_NAME.concat("_LOC"), null, cv2);
		//m2Insert.insert(cv2);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		
		//db.close(); 
	}*/  
}
