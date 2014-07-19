package com.example.democracycle;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.example.democracycle.ServiceLocation.LocalBinder;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportActivity extends Activity {
	final int CAMERA_CAPTURE_RQST_CODE = 1;
	boolean imageCaptured;
	private boolean isServiceLocationRunning,locationServiceStarted;
	String imageFileName,audioFileName;
	final String RECORDER_FOLDER = "Democracycle";
	ServiceLocation cService;
    boolean mBound;
    EditText imagetag;
    long startTimeOfThisRide;
    Uri outputFileUri;
    ImageView imageView;
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
    	
        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            // We've bound to CounterService, cast the IBinder and get CounterService instance
            LocalBinder binder = (LocalBinder) service;
            cService = binder.getService();
            mBound = true;
            Log.i("MainActivity", "mBound onServiceConnected "+mBound);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.i("MainActivity", "mBound onServiceConnected "+mBound);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		Log.i("MainActivity", "onCreate ReportActivity");
        mBound = false;
        imageCaptured=false;
        locReceived=false;
        startTimeOfThisRide=System.currentTimeMillis();
        
        imagetag = (EditText)findViewById(R.id.imagetag);
        
        // Get the intent that started this activity
        Intent intentfromMainActivity = getIntent();
        isServiceLocationRunning=intentfromMainActivity.getExtras().getBoolean("IsRunning");
        Log.i("MainActivity", "isServiceLocationRunning "+isServiceLocationRunning);
        if(isServiceLocationRunning==false){
        	// Start ServiceLocation if not running
            Intent intentServiceLocation = new Intent(this, ServiceLocation.class);
            intentServiceLocation.putExtra("From", 2);//It carries signal that starting from ManualCaptureActivity
            intentServiceLocation.putExtra("StartTimeOfThisRide", startTimeOfThisRide);
            startService(intentServiceLocation);
            locationServiceStarted=true;
        }
        // Bind to ServiceLocation to get the newest location
        Intent intentBindServiceLocation = new Intent(this, ServiceLocation.class);
        bindService(intentBindServiceLocation, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.i("MainActivity", "onDestroy ManualCaptureActivity");
        // Unbind from the ServiceLocation
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        
        // Stop ServiceLocation if service is not on from MainActivity
        if(isServiceLocationRunning==false && locationServiceStarted==true){
	        stopService(new Intent(this, ServiceLocation.class));
        }
	}
	
	public void onCaptureCamera(View view){
    	
    	try {
    		String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Democracycle" + File.separator + startTimeOfThisRide + ".jpeg";
			File pictureFile = new File(filePath);
			outputFileUri = Uri.fromFile(pictureFile);
			
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			
			//we will handle the returned data in onActivityResult
			startActivityForResult(captureIntent, CAMERA_CAPTURE_RQST_CODE);
		} catch (ActivityNotFoundException anfe) {
			//display an error message
		    String errorMessage = "Your device doesn't support capturing images!";
		    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
		}
    }
	
	@SuppressWarnings("static-access")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode==Activity.RESULT_OK) {
			imageView = (ImageView) findViewById(R.id.imageViewReport);
			if (requestCode == CAMERA_CAPTURE_RQST_CODE) {
				imageCaptured=true;
				if (data != null) {
					if(data.hasExtra("data")){
						Bitmap thumbnail = data.getParcelableExtra("data");
						imageView.setImageBitmap(thumbnail);
					}
					
					
					/*Bitmap photo = (Bitmap) data.getExtras().get("data");
					
					//transferring the Bitmap to byte[]
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); 
				    
				    //Showing it in a imageView
					Bitmap photo1 = Bitmap.createScaledBitmap(photo, 100, 100, false);
					imageView.setImageBitmap(photo1);
					
					String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Democracycle" + File.separator + startTimeOfThisRide + ".jpeg";
					
					imageFileName = filePath;
					
					//Bitmap largeBitmap ;  // save your Bitmap from data[]
					FileOutputStream fileOutputStream = null;
					BufferedOutputStream bos = null;
					int quality = 100;

					File pictureFile = new File(filePath);

					try {
						fileOutputStream = new FileOutputStream(pictureFile);
						bos = new BufferedOutputStream(fileOutputStream);
						photo.compress(CompressFormat.JPEG, quality, bos);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}

					if (bos != null) {
						try {
							bos.close();
						} catch (IOException e) {
							// ignore close error
						}
					}*/
					
				}else{
					// If there is no thumbnail image data, the image
			        // will have been stored in the target output URI.

			        // Resize the full image to fit in out image view.
			        int width = imageView.getWidth();
			        int height = imageView.getHeight();
			        
			        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

			        factoryOptions.inJustDecodeBounds = true;
			        BitmapFactory.decodeFile(outputFileUri.getPath(), factoryOptions);
			          
			        int imageWidth = factoryOptions.outWidth;
			        int imageHeight = factoryOptions.outHeight;
			        
			        // Determine how much to scale down the image
			        int scaleFactor = Math.min(imageWidth/width, 
			                                   imageHeight/height);
			        
			        // Decode the image file into a Bitmap sized to fill the View
			        factoryOptions.inJustDecodeBounds = false;
			        factoryOptions.inSampleSize = scaleFactor;
			        factoryOptions.inPurgeable = true;
			        
			        Bitmap bitmap = BitmapFactory.decodeFile(outputFileUri.getPath(),
			                                   factoryOptions);
			        
			        imageView.setImageBitmap(bitmap); 
			        
				}
			}
		}
	}
	Boolean locReceived;
	Location tempLoc;
	public void onClickThumbsUp(View view){
        
		
		if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
			
			tempLoc= cService.getCurrentLocation();//getting the most updated location from ServiceLocation using binder
            if(tempLoc!=null){
	            if(tempLoc.getTime()>startTimeOfThisRide){
	            	locReceived=true;//checking if the location received from ServiceLocation is the new one
	            	
	            	
	            }
            }
            
        }
		
		if(locReceived==true && imageCaptured==true){
			writeLocationFile(tempLoc.getTime(), tempLoc.getLatitude(), tempLoc.getLongitude(), tempLoc.getAltitude(),tempLoc.getProvider(), tempLoc.getSpeed(), tempLoc.getBearing());
			writeReportFile(imagetag.getText().toString(), "ThumbsUp");
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			SensorData sd= new SensorData();
			sd.setIsManual("true");
			sd.setStartTimeOfThisRide(""+startTimeOfThisRide);
			sd.setLocationFileName(startTimeOfThisRide+".loc");
			if(imageCaptured==true){
				sd.setImageFileName(startTimeOfThisRide+".jpeg");
			}
			sd.setImageContent(imagetag.getText().toString());
			sd.setQuestions("", "", "", "", "thumbsup", "", "", "", "", "", "", "");
			db.addContact(sd);
			db.close();
			
			//sending an intent to ServiceNetwork.
			Intent intentNetworkService = new Intent(getApplicationContext(), ServiceNetwork.class);
	        getApplicationContext().startService(intentNetworkService);
		}
		
		finish();
    	
    }
	
	public void onClickThumbsDown(View view){
		
		
		if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
			
			tempLoc= cService.getCurrentLocation();
            if(tempLoc!=null){
	            if(tempLoc.getTime()>startTimeOfThisRide){
	            	locReceived=true;
	            	
	            }
            }  
        }
		
		if(locReceived==true && imageCaptured==true){
			writeLocationFile(tempLoc.getTime(), tempLoc.getLatitude(), tempLoc.getLongitude(), tempLoc.getAltitude(),tempLoc.getProvider(), tempLoc.getSpeed(), tempLoc.getBearing());
			writeReportFile(imagetag.getText().toString(), "ThumbsDown");
			
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			SensorData sd= new SensorData();
			sd.setIsManual("true");
			sd.setStartTimeOfThisRide(""+startTimeOfThisRide);
			sd.setLocationFileName(startTimeOfThisRide+".loc");
			if(imageCaptured==true){
				sd.setImageFileName(startTimeOfThisRide+".jpeg");
			}
			sd.setImageContent(imagetag.getText().toString());
			sd.setQuestions("", "", "", "", "thumbsdown", "", "", "", "", "", "", "");
			db.addContact(sd);
			db.close();
			
			//sending an intent to ServiceNetwork.
			Intent intentNetworkService = new Intent(getApplicationContext(), ServiceNetwork.class);
	        getApplicationContext().startService(intentNetworkService);
		}
		
		finish();
    	
    }
	
	private void writeLocationFile(long t, double Lat, double Lon, double Alt, String Prov, float Sp, float Bear){
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, startTimeOfThisRide+".loc");
    
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.append(t+","+Lat+","+Lon+","+Alt+","+Prov+","+Sp+","+Bear+"\n");
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
	
	private void writeReportFile(String reportStr, String typeOfReport){
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, startTimeOfThisRide+".rp");
    
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.append(reportStr+","+typeOfReport+"\n");
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
