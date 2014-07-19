package com.example.democracycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class ServiceLocation extends Service{
    private LocationManager locMan;
    private Handler handler = new Handler();
    FileOutputStream f;
    PrintWriter pw;
    private static final String RECORDER_FOLDER = "Democracycle";
    private int intentSource;
    final static int myID = 1234;
    SharedPreferences sharedPref;

    public static Location curLocation;

    LocationListener gpsListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        	Boolean locationChanged=false;
        	
            if (curLocation == null) {
                locationChanged = true;
            }
            else if (curLocation.getTime() > location.getTime()){
                locationChanged = false;
                return;
            }
            else if (curLocation.getTime() < location.getTime()){
                locationChanged = true;
            }
            
            if (locationChanged==true){

                curLocation = location;
            	//Log.i("MainActivity", "onLocationChanged in ServiceLocation " + curLocation.getLatitude() + " " + curLocation.getProvider());
            	//Toast.makeText(getBaseContext(), "Location Found " + curLocation.getLatitude(), Toast.LENGTH_LONG).show();
                //locMan.removeUpdates(gpsListener);
            }
        }
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status,Bundle extras) {
            if (status == 0)// UnAvailable
            {
            } else if (status == 1)// Trying to Connect
            {
            } else if (status == 2) {// Available
            }
        }

    };
    
    private final IBinder mBinder = new LocalBinder();// Binder given to clients which is a interface for client to bind
    
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	ServiceLocation getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServiceLocation.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.i("MainActivity", "creating ServiceLocation");
        curLocation = null;
        
    }
    
    PowerManager mgr;
    WakeLock wakeLock;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.i("MainActivity", "onStartCommand ServiceLocation");
    	sharedPref = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
    	index=0;
    	//Here I am starting the wake lock thingy
        mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
    	
    	// This part is for starting the service in foreground with a notification//
    	//The intent to launch when the user clicks the expanded notification
    	Intent intentExpandNotification = new Intent(getApplicationContext(), MainActivity.class);
    	intentExpandNotification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);
    	
    	//THis Notification constructor has deprecated, but good to support old phones
        Notification notification = new Notification(R.drawable.ic_menu_myplaces, getText(R.string.ticker_text), System.currentTimeMillis());
        
        //This method is deprecated. Use Notification.Builder instead.
        notification.setLatestEventInfo(this, "DemocraCycle", "For a bicycle friendly town.", pendIntent);
        
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(myID, notification);
        //Finished setting up notification and startForeground
        
        startTimeOfThisRide = intent.getExtras().getLong("StartTimeOfThisRide");
  	    intentSource = intent.getExtras().getInt("From");
  	    Log.i("MainActivity", "Starting ServiceLocation from " + intentSource);
  	    if(intentSource==1){
	  	    //Here I am starting the ServiceAudioCapture
	  	    Intent intentServiceAudioCapture = new Intent(getApplicationContext(), ServiceAudioRecord.class);
	        intentServiceAudioCapture.putExtra("StartTimeOfThisRide", startTimeOfThisRide);
	        getApplicationContext().startService(intentServiceAudioCapture);
	        //Here I am starting the ServiceAccelerometer
	        Intent intentServiceAccelerometer = new Intent(getApplicationContext(), ServiceAccelerometer.class);
	        intentServiceAccelerometer.putExtra("StartTimeOfThisRide", startTimeOfThisRide);
	        getApplicationContext().startService(intentServiceAccelerometer);
  	    }
        
		
		//Starting GPS
		getBestLocation();
		
		
  	  
  	    if(intentSource==1){
  		    handler.removeCallbacks(GpsFinderStart);
  	        handler.postDelayed(GpsFinderStart,1);
  	    }else{
  		  handler.removeCallbacks(GpsFinderManualCapture);
  		  handler.postDelayed(GpsFinderManualCapture,1);
  	    }
    	
  	    if(intentSource==1){
	  	    // write on SD card file data
			String filepath = Environment.getExternalStorageDirectory().getPath();
			
			File dir = new File (filepath, RECORDER_FOLDER);
			if(!dir.exists()){
				dir.mkdirs();
			}
	        File file = new File(dir, startTimeOfThisRide+".loc");
	    
	        try {
	            f = new FileOutputStream(file);
	            pw = new PrintWriter(f);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            Log.i("MainActivity", "File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
  	    }
  	    
    	return super.onStartCommand(intent, flags, startId);
    }
    
   private final int delaySec=1;//<--------sampling rate of location
   private long startTimeOfThisRide;
   int index;
   int DATALENGTH=50;
   
   public Runnable GpsFinderStart = new Runnable(){
	    public void run(){
	    	handler.postDelayed(GpsFinderStart,delaySec*1000);
	    	Log.i("MainActivity", "GpsFinderStart ServiceLocation");
	    	Location tempLoc = getCurrentLocation();
	    	
	        if(tempLoc!=null){
	        	//if tempLoc is latest, then replace it
	        	if(tempLoc.getTime()>startTimeOfThisRide){
	        		curLocation = tempLoc;
	        		pw.append(curLocation.getTime()+","+curLocation.getLatitude()+","+curLocation.getLongitude()+","+curLocation.getAltitude()+","+curLocation.getProvider()+","+curLocation.getSpeed()+","+curLocation.getBearing()+"\n");
	                
	                index=(index+1)%DATALENGTH;
	                
	                if(index==0){
	                	pw.flush();
	                }
	                
	        		Log.i("MainActivity", "onLocationChanged in ServiceLocation " + curLocation.getLatitude() + " " + curLocation.getProvider());
	        		
	        	}
	        }
	        tempLoc = null;
	        
	    }
  };
 
   @Override
   public IBinder onBind(Intent arg0) {
	   Log.i("MainActivity", "onBind ServiceLocation");
	   handler.removeCallbacks(GpsFinderManualCapture);
	   handler.postDelayed(GpsFinderManualCapture,1);
       return mBinder;
   }
   
   public Runnable GpsFinderManualCapture = new Runnable(){
	    public void run(){
	        getBestLocation();
    	}
   };
   
   
    @Override
	public boolean onUnbind(Intent intent) {
    	Log.i("MainActivity", "onUnBind ServiceLocation");
    	locMan.removeUpdates(gpsListener);
		//return super.onUnbind(intent);
    	//returning true because of Rebinding
    	return true;
	}
    
    

    @Override
	public void onRebind(Intent intent) {
    	Log.i("MainActivity", "onReBind ServiceLocation");
    	handler.removeCallbacks(GpsFinderManualCapture);
 	    handler.postDelayed(GpsFinderManualCapture,1);
		super.onRebind(intent);
	}



	@Override
   public void onDestroy() {
       handler.removeCallbacks(GpsFinderManualCapture);
       handler.removeCallbacks(GpsFinderStart);
       handler = null;
       locMan.removeUpdates(gpsListener);
       locMan=null;
       curLocation=null;
       Log.i("MainActivity", "Destroying ServiceLocation");
       
       //Stopping the ServiceAudioCapture service
       stopService(new Intent(getApplicationContext(),ServiceAudioRecord.class));
       //Stopping the ServiceAccelerometer service
       stopService(new Intent(getApplicationContext(),ServiceAccelerometer.class));
       
       if(intentSource==1){
	       try {
	    	   pw.flush();
	           pw.close();
	           f.close();
	       } catch (FileNotFoundException e) {
	           e.printStackTrace();
	           Log.i("MainActivity", "File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
       }
       
       //releasing Wakelock
       wakeLock.release();
   }

     Location getBestLocation() {
        Location gpslocation = null;
        Location networkLocation = null;
        
        if(locMan==null){
          locMan = (LocationManager) getApplicationContext() .getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            if(locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, gpsListener);// here you can set the 2nd argument as hint of time interval (milisec) and the next argument is mindistance in meter
                gpslocation = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }else{
            	Toast.makeText(getBaseContext(),"Please enable GPS Location Services!",Toast.LENGTH_SHORT).show();
            }
            
            if(locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, gpsListener);
                networkLocation = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }else{
            	Toast.makeText(getBaseContext(),"Please enable Network Location Services!",Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Log.e("error", e.toString());
        }
        if(gpslocation==null && networkLocation==null)
            return null;

        if(gpslocation!=null && networkLocation!=null){
            if(gpslocation.getTime() < networkLocation.getTime()){
                gpslocation = null;
                return networkLocation;
            }else{
                networkLocation = null;
                return gpslocation;
            }
        }
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        return null;
    }
    
    Location getCurrentLocation(){
    	return curLocation;
    }
}
