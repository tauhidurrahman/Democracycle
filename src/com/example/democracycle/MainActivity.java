package com.example.democracycle;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	AlarmManager alarmManager;
	int sampPeriodMin;
	boolean isRunning;
	private int MANUAL_CAPTURE_RQST_CODE = 99;
	private int QUESTIONNAIRE_RQST_CODE = 98;
	int DefaultSampPeriodMin=10;
	TextView sampleCount;
	Button startnStop;
	SharedPreferences sharedPref;
	TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4;
	String loc_1,loc_2,loc_3,loc_4;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //opening shared preferences and sqlite handler 
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        
        //Configuring the startnStop Button
        startnStop = (Button) findViewById(R.id.startnStop);
        isRunning = sharedPref.getBoolean(getString(R.string.Flag_isRunning), false);
        if(isRunning==true){
        	startnStop.setText("Stop");
        	startnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause, 0, 0, 0);
        }else{
        	startnStop.setText("Start");
        	startnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_play_clip, 0, 0, 0);
        }
        
        //Configuring the sampleCount TextView
        sampleCount = (TextView) findViewById(R.id.sampleCount);
        String TotalRewardEarned = (int)(Float.parseFloat(sharedPref.getString("TotalRewardEarned", "0"))*100)+"";
        sampleCount.setText(TotalRewardEarned);
        
        tvLoc1 = (TextView) findViewById(R.id.collegeTown);
        tvLoc2 = (TextView) findViewById(R.id.enggQuad);
        tvLoc3 = (TextView) findViewById(R.id.artsQuad);
        tvLoc4 = (TextView) findViewById(R.id.aggQuad);
        
        try {
        	JSONObject jsonObject = new JSONObject(sharedPref.getString("rewardScheme", "Not Available"));
        	loc_1=(int)(Float.parseFloat(jsonObject.get("loc_1").toString())*100)+"";
        	loc_2=(int)(Float.parseFloat(jsonObject.get("loc_2").toString())*100)+"";
        	loc_3=(int)(Float.parseFloat(jsonObject.get("loc_3").toString())*100)+"";
        	loc_4=(int)(Float.parseFloat(jsonObject.get("loc_4").toString())*100)+"";
        } catch (JSONException e) {
            e.printStackTrace();
            loc_1="1";
            loc_2="1";
            loc_3="1";
            loc_4="1";
            Log.i("MainActivity","Error with Json "+e.toString());
        }
        
        tvLoc1.setText(loc_1);
        tvLoc2.setText(loc_2);
        tvLoc3.setText(loc_3);
        tvLoc4.setText(loc_4);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.activity_main, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
      switch (item.getItemId()) {
      case R.id.menu_settings:
    	Intent intent = new Intent(this, SettingsActivity.class);
      	startActivity(intent);
        break;
      
      default:
        break;
      }
      
      return true;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.Flag_isRunning), isRunning);
        editor.commit();
        
	}
	
	public void onClickStartnStop(View view) {

        if(isRunning==false)
        {	long currentTime = System.currentTimeMillis();
	        sharedPref = getPreferences(Context.MODE_PRIVATE);
	        SharedPreferences.Editor editor = sharedPref.edit();
	        editor.putLong("StartTimeOfThisRide", currentTime);
	        editor.commit();
	        
            //Here I am starting the ServiceLocation
            Intent intent = new Intent(this, ServiceLocation.class);
            intent.putExtra("From", 1);//It carries signal that starting from MainActivity
            intent.putExtra("StartTimeOfThisRide", currentTime);
            startService(intent);
            
            startnStop.setText("Stop");
            startnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause, 0, 0, 0);
            isRunning=true;
        }
        else if(isRunning==true)
        {
        	//Stopping the ServiceLocation service
    		stopService(new Intent(this,ServiceLocation.class));
    		
    		long currentTime = sharedPref.getLong("StartTimeOfThisRide", 0);
    		//start the QuestionnaireActivity
    		Intent intent = new Intent(this, QuestionnaireActivity.class);
        	intent.putExtra("StartTimeOfThisRide", currentTime);//Passing isRunning boolean
        	startActivityForResult(intent, QUESTIONNAIRE_RQST_CODE);
    		
        	startnStop.setText("Start");
        	startnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_play_clip, 0, 0, 0);
            isRunning=false;
        }
	}
	
	public void onReportActivity(View view){
    	Intent intent = new Intent(this, ReportActivity.class);
    	intent.putExtra("IsRunning", isRunning);//Passing isRunning boolean
    	startActivityForResult(intent, MANUAL_CAPTURE_RQST_CODE);
    	
    	//Intent intent = new Intent(this, CalendarViewActivity.class);
    	//startActivity(intent);
    }
	
	public void onLeaderBoard(View view){
		Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==Activity.RESULT_OK) {
			//I don't use it right now. but kept this code for future.
			DatabaseHandler db = new DatabaseHandler(this);
			Log.i("MainActivity", "Total count: "+(int)db.getContactsCount());
			db.close();
			
		}
	}
	
    
}
