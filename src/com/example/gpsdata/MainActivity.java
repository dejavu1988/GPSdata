package com.example.gpsdata;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.example.gpsdata.DBHandler;

import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements GpsStatus.Listener, LocationListener{

	public static String experimentId = "X1";
	public static Boolean status = false;
	public static long StartTime = 0;
	
	private Button start,end;
	private LinearLayout linearLayout;
	
	private TextView show, tp;
	private LocationManager locMgr;
	public static Location seedLocation;
	//private Listener gpsStatusListener;
	private GpsStatus gpsStatus;
	
	//private float azimuth;
	//private float elevation;
	//private int prn;
	//private float snr;
		
	private LocEntry currentLocEntry;
	private SatEntry currentSatEntry;
	private List<SatEntry> satList;
	private DBHandler db;
	//private Handler mHandler; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start = (Button) findViewById(R.id.start);
		end = (Button) findViewById(R.id.end);
		linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
		show = (TextView) findViewById(R.id.show);
		show.setText("There are 0 satellites:");		
		tp = (TextView) findViewById(R.id.tp);					
		start.setOnClickListener(new StartListener());
		end.setOnClickListener(new EndListener());

		db = new DBHandler(this);	//an instance of database handler
		
		
        locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//locMgr.addGpsStatusListener(this);
		
		if ( locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
		{		// If GPS can provide the last known location data, get it using GPS
			seedLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		}else if (locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null)
		{		// otherwise, test mobile network provider if the last known location is available
			seedLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		StartTime = System.currentTimeMillis();
		currentSatEntry = new SatEntry();
		satList = new ArrayList<SatEntry>();
		currentLocEntry = new LocEntry(); 
		currentLocEntry.setLocalTime(StartTime);
		
        if (seedLocation == null) { 
        	tp.setText("Initializing location ...");
        }else {
        	currentLocEntry.setLoca(seedLocation);
    		double x = currentLocEntry.getLati();
    		double y = currentLocEntry.getLongi();
    		double z = currentLocEntry.getAlti();
    		String s = "My location: (" + Location.convert(x, Location.FORMAT_SECONDS) + ", " 
    				+ Location.convert(y, Location.FORMAT_SECONDS) + ", " + Location.convert(z, Location.FORMAT_SECONDS) + ")\n";
    		tp.setText(s);
        }
		Log.d("loca","s");
		//locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

	    Log.i("dbpath", new ContextWrapper(this).getDatabasePath("gpsexp.sqlite").getAbsolutePath());
	
	}
	
		
	private class AddSatEntryTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
          
            try {
            	if(db != null && status)
            		db.addSatEntry(experimentId, satList);
            	
            } catch (Exception e) {
              e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
        	
        }
    }
	
	private class AddLocEntryTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
          
            try {
            	if(db != null && status)
            		db.addLocEntry(experimentId, currentLocEntry);
            	
            } catch (Exception e) {
              e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
        	
        }
    }
	
	private class AddEntrysTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
          
            try {
            	if(db != null && status){
            		db.addSatEntry(experimentId, satList);
            		db.addLocEntry(experimentId, currentLocEntry);
            	}           	
            } catch (Exception e) {
              e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
        	try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data//com.example.gpsdata//databases//gpsexp.sqlite";
                    String backupDBPath = "gpsexp.sqlite";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            } catch (Exception e) {
            }
        }
    }
	
	
	@Override
	protected void onResume() {
		
	    super.onResume();
	    locMgr.addGpsStatusListener(this);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
	    
	}
	
	 @Override
	protected void onPause() {
			
		locMgr.removeGpsStatusListener(this);
		locMgr.removeUpdates(this);
		super.onPause();
	}
	
	 @Override
	protected void onDestroy() {
	    	
		super.onDestroy();
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	class StartListener implements OnClickListener {
		  public void onClick(View v) {
			  //linearLayout.removeViews (0, linearLayout.getChildCount());
			  //scenario = (EditText) findViewById(R.id.name);
			  //locMgr.addGpsStatusListener(MainActivity.this); 
		      status = true;
		      
		      //locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, MainActivity.this);
		  }
		}
	
	class EndListener implements OnClickListener {
		  public void onClick(View v) {
			  //linearLayout.removeViews (0, linearLayout.getChildCount());
			  //locMgr.removeGpsStatusListener(MainActivity.this); 
		      status = false;
		      //locMgr.removeUpdates(MainActivity.this);
		      AddEntrysTask addEntrysTask = new AddEntrysTask();
	          addEntrysTask.execute((Object[]) null);
	          
		  }
		}

	
	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		gpsStatus =locMgr.getGpsStatus(null);
		if(gpsStatus != null && event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
			Iterable<GpsSatellite> iSatellites =gpsStatus.getSatellites();
            Iterator<GpsSatellite> it = iSatellites.iterator();
            linearLayout.removeViews(0, linearLayout.getChildCount());
            
            currentSatEntry.setLocalTime(System.currentTimeMillis());
            satList.clear();
            int count=0;
            while(it.hasNext()){
              count=count+1;
              GpsSatellite oSat = (GpsSatellite) it.next();              
              currentSatEntry.setSate(oSat);
              satList.add(currentSatEntry);
              
              TextView tv = new TextView(getApplicationContext());
              tv.setId(currentSatEntry.getPRN());
              tv.setText("Sat" + currentSatEntry.getPRN() + ": Azimuth=" + currentSatEntry.getAzimuth() + ", Elevation=" + currentSatEntry.getElevation() + ", SNR=" + currentSatEntry.getSNR());
              tv.setTextColor(Color.BLUE);
              linearLayout.addView(tv);
             }
            show.setText("There are " + count + " satellites:");
            
            AddSatEntryTask addSatEntryTask = new AddSatEntryTask();
            addSatEntryTask.execute((Object[]) null);
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		currentLocEntry.setLocalTime(System.currentTimeMillis()); // update current timestamp
		currentLocEntry.setLoca(location);
		
		Log.d("loca", currentLocEntry.toString());
		double x = currentLocEntry.getLati();
		double y = currentLocEntry.getLongi();
		double z = currentLocEntry.getAlti();
		String s = "My location: (" + Location.convert(x, Location.FORMAT_SECONDS) + ", " 
				+ Location.convert(y, Location.FORMAT_SECONDS) + ", " + Location.convert(z, Location.FORMAT_SECONDS) + ")\n";
		tp.setText(s);
    	tp.setTextColor(Color.BLUE);
    	
    	AddLocEntryTask addLocEntryTask = new AddLocEntryTask();
        addLocEntryTask.execute((Object[]) null);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
/***************LocEntry: unit structure for tracking location data****************/
	
	public static class LocEntry implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private long localtime;	// local timestamp
		private double lati;
		private double longi;
		private double alti;
		
		
		public LocEntry() {
			super();
			this.localtime = 0;
			this.lati = 0;
			this.longi = 0;
			this.alti = 0;
		}		
				
		public LocEntry(long t, Location loc) {
			super();
			this.localtime = t;
			this.lati = loc.getLatitude();
			this.longi = loc.getLongitude();
			this.alti = loc.getAltitude();
		}
		
			// get assigned from another LocEntry
		public void set(LocEntry l) {
			this.localtime = l.getLocalTime();
			this.lati = l.getLati();
			this.longi = l.getLongi();
			this.alti = l.getAlti();
		}
		
		// getting local timestamp
	    public long getLocalTime(){
	        return this.localtime;
	    }
	 
	    // setting local timestamp
	    public void setLocalTime(long t){
	        this.localtime = t;
	    }
	    	 
	    // setting location: latitude, longitude, altitude
	    public void setLoca(Location loc){
	        this.lati = loc.getLatitude();
	        this.longi = loc.getLongitude();
			this.alti = loc.getAltitude();
	    }
	    
	   
	    public double getLati(){	    	
	        return this.lati;
	    }
	    
	    public double getLongi(){	    	
	        return this.longi;
	    }
	    
	    public double getAlti(){	    	
	        return this.alti;
	    }
	    	
	    // returns {hh, mm, ss} of localtime
	    public int[] formatLocalTime(){
	    	long millis = this.localtime;
	    	int sec = (int) (millis / 1000);
    		int min = sec / 60; sec %= 60;
    		int hour = min / 60; min %= 60;
    		int r[] = {hour, min, sec};
    		return r;
	    }
	    	
	    // if equals, return true
	    public boolean isEqual(LocEntry l){
	    	return (this.getLocalTime() == l.getLocalTime());
	    }
	    
	    // difference in distance
	    public float diff(LocEntry l){
	    	float results[] = new float[3];
	    	Location.distanceBetween(this.lati, this.longi, l.getLati(), l.getLongi(), results);
	    	return results[0];
	    }
	    
	   
	}
	
/***************SatEntry: unit structure for tracking satellite info****************/
	
	public static class SatEntry implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2L;
		private long localtime;	// local timestamp
		private int prn;
		private float azimuth;
		private float elevation;
		private float snr;
		
		
		public SatEntry() {
			super();
			this.localtime = 0;
			this.prn = 0;
			this.azimuth = 0;
			this.elevation = 0;
			this.snr = 0;
		}
		
		public SatEntry(long t, GpsSatellite g) {
			super();
			this.localtime = t;
			this.prn = g.getPrn();
			this.azimuth = g.getAzimuth();
			this.elevation = g.getElevation();
			this.snr = g.getSnr();
		}
		
		
			// get assigned from another SatEntry
		public void set(SatEntry s) {
			this.localtime = s.localtime;
			this.prn = s.prn;
			this.azimuth = s.azimuth;
			this.elevation = s.elevation;
			this.snr = s.snr;
		}
		
		// getting local timestamp
	    public long getLocalTime(){
	        return this.localtime;
	    }
	 
	    // setting local timestamp
	    public void setLocalTime(long t){
	        this.localtime = t;
	    }
	    
	    // setting satellite info
	    public void setSate(GpsSatellite g){
	    	this.prn = g.getPrn();
			this.azimuth = g.getAzimuth();
			this.elevation = g.getElevation();
			this.snr = g.getSnr();
	    }
	    
	    // getting PRN
	    public int getPRN(){
	        return this.prn;
	    }
	 
	    public float getAzimuth(){
	        return this.azimuth;
	    }
	 	    
	    public float getElevation(){
	        return this.elevation;
	    }
	 
	    public float getSNR(){
	        return this.snr;
	    }
	    	
	    // returns {hh, mm, ss} of localtime
	    public int[] formatLocalTime(){
	    	long millis = this.localtime;
	    	int sec = (int) (millis / 1000);
    		int min = sec / 60; sec %= 60;
    		int hour = min / 60; min %= 60;
    		int r[] = {hour, min, sec};
    		return r;
	    }
	    	
	    // if equals, return true
	    public boolean isEqual(SatEntry s){
	    	return (this.localtime == s.getLocalTime() && this.prn == s.prn);
	    }
	}
	

}
