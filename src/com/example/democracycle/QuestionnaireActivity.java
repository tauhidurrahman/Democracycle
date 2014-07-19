package com.example.democracycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

public class QuestionnaireActivity extends Activity {
	Spinner question1,question2,question3;//,question8,question9;
	String answer1,answer2,answer3;
	private static final String RECORDER_FOLDER = "Democracycle";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        
        question1 = (Spinner) findViewById(R.id.question_1);
        question2 = (Spinner) findViewById(R.id.question_2);
        question3 = (Spinner) findViewById(R.id.question_3);
    }
    
    
    public void onClickDelete(View view){
    	//need to remove the .wav .accl .loc file
    	//File file = new File(selectedFilePath);
    	//boolean deleted = file.delete();
    	
    	// Get the intent that started this activity
        Intent intentfromMainActivity = getIntent();
        long startTimeOfThisRide=intentfromMainActivity.getExtras().getLong("StartTimeOfThisRide");
    	
    	String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file_loc = new File(dir, startTimeOfThisRide+".loc");
		file_loc.delete();
		File file_wav = new File(dir, startTimeOfThisRide+".wav");
		file_wav.delete();
		File file_acl = new File(dir, startTimeOfThisRide+".acl");
		file_acl.delete();
    	finish();
    }
    
    public void onClickSave(View view){
    	long q1 = question1.getSelectedItemId()>0?1:0;
    	long q2 = question2.getSelectedItemId()>0?1:0;
    	long q3 = question3.getSelectedItemId()>0?1:0;
    	
    	
    	if(q1==0){
    		answer1="";
    	}else{
    		answer1=String.valueOf(question1.getSelectedItem());
    	}
    	if(q2==0){
    		answer2="";
    	}else{
    		answer2=String.valueOf(question2.getSelectedItem());
    	}
    	if(q3==0){
    		answer3="";
    	}else{
    		answer3=String.valueOf(question3.getSelectedItem());
    	}
    	
    	// Get the intent that started this activity
        Intent intentfromMainActivity = getIntent();
        long startTimeOfThisRide=intentfromMainActivity.getExtras().getLong("StartTimeOfThisRide");
        writeQuestionnaireFile(startTimeOfThisRide,answer1, answer2, answer3);
        
    	DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		SensorData sd= new SensorData();
		sd.setIsManual("false");
		sd.setStartTimeOfThisRide(""+startTimeOfThisRide);
		sd.setLocationFileName(startTimeOfThisRide+".loc");
		sd.setAudioFileName(startTimeOfThisRide+".wav");
		sd.setAccelerometerFileName(startTimeOfThisRide+".acl");
		sd.setQuestions(answer1, answer2, answer3, "", "", "", "", "", "", "", "", "");
		db.addContact(sd);
		db.close();
		
    	Intent result = new Intent(this, MainActivity.class);
    	result.putExtra("isSaved", true);
    	setResult(Activity.RESULT_OK, result);
		finish();
    	
    }
    
    private void writeQuestionnaireFile(long startTimeOfThisRide,String q1, String q2, String q3){
		// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		Log.i("MainActivity", filepath);
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, startTimeOfThisRide+".qa");
    
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.append(q1+","+q2+","+q3+"\n");
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
    
    private ArrayList<Stressloctime> readDailySummary(String date){
    	// write on SD card file data
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File dir = new File (filepath, RECORDER_FOLDER);
		if(!dir.exists()){
			dir.mkdirs();
		}
        File file = new File(dir, date+"_rifat.txt");
        ArrayList<Stressloctime> dailySummary = new ArrayList<Stressloctime>();
        try{
        	Scanner inputStream = new Scanner(file);
        	
        	while(inputStream.hasNext()){
        		String line = inputStream.next();
        		String[] values = line.split(",");
        		//Now put the values in the class
        		Stressloctime s1 = new Stressloctime(values[0],values[1],values[2],values[3],values[4],"");
        		//Now add the class in the arraylist
        		dailySummary.add(s1);
        	}
        }catch (FileNotFoundException e){
        	e.printStackTrace();
        	Stressloctime s1 = new Stressloctime("FileNotFound","","","","","");
        	dailySummary.add(s1);
        }
		return dailySummary;
    }
    

}
