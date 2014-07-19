package com.example.democracycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class ServiceAccelerometer extends Service implements SensorEventListener{
	private SensorManager mSensorManager;
    //float [] acclData;
    //long [] time;
    int index;
    int DATALENGTH=90;//After this many data the writer flushes
    private static final String RECORDER_FOLDER = "Democracycle";
    private static final String RECORDER_FILE_EXT = ".acl";
    private Thread flushThread = null;
    long startTimeOfThisRide;
    FileOutputStream f;
    PrintWriter pw;
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//acclData = new float[DATALENGTH];
		//time = new long[DATALENGTH];
		index=0;
		
		startTimeOfThisRide = intent.getExtras().getLong("StartTimeOfThisRide");
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
			// Success! There's an Accelerometer.
			mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
	    }
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
			// Success! There's a gyro.
			mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, startTimeOfThisRide+RECORDER_FILE_EXT);
    
        try {
            f = new FileOutputStream(file);
            pw = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("MainActivity", "File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	getAccelerometer(event);
        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        	getGyro(event);
        }
	}
	
	private void getAccelerometer(SensorEvent event) {
	    // Movement
	    long timeStamp = event.timestamp;
	    Log.i("MainActivity", ""+timeStamp+","+event.values[0]+","+event.values[1]+","+event.values[2]);
	    //time[index/3]=event.timestamp;
	    //acclData[index]=event.values[0];
	    //acclData[index+1]=event.values[1];
	    //acclData[index+2]=event.values[2];
	    
	    //putting the data in to PrintWriter
        pw.append(event.timestamp+","+event.values[0]+","+event.values[1]+","+event.values[2]+"\n");
        
        index=(index+1)%DATALENGTH;
        
        flushThread = new Thread(new Runnable() {
    		@Override
    		public void run() {
    		    
    			pw.flush();
    		    
    		}
    	},"Flush Thread");
        
        if(index==0){
        	flushThread.start();
        }
	    
    }
	
	
	private void getGyro(SensorEvent event){
		// Axis of the rotation sample, not normalized yet.
		long timeStamp = event.timestamp;
	    float axisX = event.values[0];
	    float axisY = event.values[1];
	    float axisZ = event.values[2];
	}
	
	private void writeCSVFile(float [] ad, long [] t){
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, startTimeOfThisRide+RECORDER_FILE_EXT);
        
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for(int i=0;i<DATALENGTH;i=i+3){
            	pw.append(t[i/3]+","+ad[i]+","+ad[i+1]+","+ad[i+2]+"\n");
            }
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		flushThread=null;
		mSensorManager.unregisterListener(this);
		Log.i("MainActivity", "Destroying ServiceAccelerometer");
		
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
	
	
}
