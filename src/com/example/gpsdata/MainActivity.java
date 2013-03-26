package com.example.gpsdata;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;

import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements GpsStatus.Listener, LocationListener{

	public static int experimentId = 0;
	public static Boolean status = false;
	public static long StartTime = 0;
	public static boolean isFirstLocation = true;
	
	private Button start,end;
	private LinearLayout linearLayout;
	
	private TextView show, tp;
	private LocationManager locMgr;
	public static Location seedLocation;
	//private Listener gpsStatusListener;
	private GpsStatus gpsStatus;
	
	private float azimuth;
	private float elevation;
	private int prn;
	private float snr;
		
	private LocEntry currentLocEntry;
	
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
		currentLocEntry = new LocEntry(); 
		currentLocEntry.setLocalTime(StartTime);
		
        if (seedLocation == null) { 
        	tp.setText("Initializing location ...");
        }else {
        	currentLocEntry.setLoca(seedLocation);
    		double x = currentLocEntry.getLoca().getLatitude();
    		double y = currentLocEntry.getLoca().getLongitude();
    		double z = currentLocEntry.getLoca().getAltitude();
    		String s = "My location: (" + Location.convert(x, Location.FORMAT_SECONDS) + ", " 
    				+ Location.convert(y, Location.FORMAT_SECONDS) + ", " + Location.convert(z, Location.FORMAT_SECONDS) + ")\n";
    		tp.setText(s);
        }
		Log.d("loca","s");
		//locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
	
	}
	
	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		gpsStatus =locMgr.getGpsStatus(null);
		if(gpsStatus != null && event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
			Iterable<GpsSatellite> iSatellites =gpsStatus.getSatellites();
            Iterator<GpsSatellite> it = iSatellites.iterator();
            linearLayout.removeViews (0, linearLayout.getChildCount());
            int count=0;
            while(it.hasNext()){
              count=count+1;
              GpsSatellite oSat = (GpsSatellite) it.next();
              azimuth = oSat.getAzimuth();
              elevation = oSat.getElevation();
              prn = oSat.getPrn();
              snr = oSat.getSnr();             
              
              TextView tv = new TextView(getApplicationContext());
              tv.setId(prn);
              tv.setText("Sat" + prn + ": Azimuth=" + azimuth + ", Elevation=" + elevation + ", SNR=" + snr);
              tv.setTextColor(Color.BLUE);
              linearLayout.addView(tv);
             }
            show.setText("There are " + count + " satellites:");
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
		      experimentId = new Random().nextInt();
		      //locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, MainActivity.this);
		  }
		}
	
	class EndListener implements OnClickListener {
		  public void onClick(View v) {
			  //linearLayout.removeViews (0, linearLayout.getChildCount());
			  //locMgr.removeGpsStatusListener(MainActivity.this); 
		      status = false;
		      //locMgr.removeUpdates(MainActivity.this);
		  }
		}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(isFirstLocation) {		
			// if input location is the first point in tracking
		Log.d("loca", "isFirstLocation ture");	// DEBUG log message
		currentLocEntry.setLoca(location);
	    StartTime = System.currentTimeMillis();	// initiate start timestamp
	    currentLocEntry.setLocalTime(StartTime);
	    isFirstLocation = false;
	    
		}else {		// otherwise, store lastLocation and update currentLocation
			Log.d("loca", "isFirstLocation false");	// DEBUG log message
			currentLocEntry.setLoca(location);
			currentLocEntry.setLocalTime(System.currentTimeMillis()); // update current timestamp
		}
		Log.d("loca", currentLocEntry.toString());
		double x = currentLocEntry.getLoca().getLatitude();
		double y = currentLocEntry.getLoca().getLongitude();
		double z = currentLocEntry.getLoca().getAltitude();
		String s = "My location: (" + Location.convert(x, Location.FORMAT_SECONDS) + ", " 
				+ Location.convert(y, Location.FORMAT_SECONDS) + ", " + Location.convert(z, Location.FORMAT_SECONDS) + ")\n";
		tp.setText(s);
    	tp.setTextColor(Color.BLUE);
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
	
/***************LocEntry: unit structure for tracking data****************/
	
	public static class LocEntry implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private long localtime;	// local timestamp
		private Location loca;
		
		
		public LocEntry() {
			super();
			this.localtime = 0;
			this.loca = null;
		}
		
		public LocEntry(Location loc) {
			super();
			this.localtime = 0;
			this.loca = new Location(loc);
		}
		
				
		public LocEntry(long t, Location loc) {
			super();
			this.localtime = t;
			this.loca = new Location(loc);
		}
		
/*		public LocEntry(long t, double x, double y, double z, long time) {
			super();
			this.localtime = t;
			this.loca = new Location(seedLocation);
			this.loca.setLatitude(x);
			this.loca.setLongitude(y);
			this.loca.setAltitude(z);
			//this.loca.setSpeed(speed);
			this.loca.setTime(time);
		}
*/		
			// get assigned from another LocEntry
		public void set(LocEntry l) {
			this.localtime = l.getLocalTime();
			this.loca = l.getLoca();
		}
		
		// getting local timestamp
	    public long getLocalTime(){
	        return this.localtime;
	    }
	 
	    // setting local timestamp
	    public void setLocalTime(long t){
	        this.localtime = t;
	    }
	    
	    // getting location: latitude, longitude
	    public Location getLoca(){
	        return this.loca;
	    }
	 
	    // setting location: latitude, longitude
	    public void setLoca(Location loc){
	        this.loca = new Location(loc);
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
	    	return this.loca.distanceTo(l.getLoca());
	    }
	    
	    @Override
	    public String toString(){
	    	return new StringBuilder()
	    	.append("Local timestamp: ")
	    	.append(this.localtime)
	    	.append(", Location: ")
	    	.append(this.loca.toString()).toString();
	    }
	}
	

}
