package com.example.democracycle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarViewActivity extends Activity {
	
	static final String[] numbers = new String[] { 
		"1", "2", "3", "4", "5",
		"6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15",
		"16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26","27","28","29","30"};

	//public static final String PAM_SELECTION = PAMActivity.class.getPackage().getName() + "PAM_SELECTION";
	//public static final String PAM_PHOTO_ID = PAMActivity.class.getPackage().getName() + "PAM_PHOTO_ID";
	
	private String[] filenames;
	private Button next;
	GridView gridview;
	private TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_view);
		
		gridview = (GridView) findViewById(R.id.calendar_grid);
		tv = (TextView) findViewById(R.id.calendar_month);
		next= (Button) findViewById(R.id.calendar_next);
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//load next dates in grid, also change the calendar month if needed
				//setupPAM();
			}

		});

		// set up pam
		setupPAM();
		//gridview.getChildAt(3).setBackgroundColor(Color.BLACK);
		//gridview.getChildAt(6).setBackgroundColor(Color.BLACK);
	}

	/** setup PAM */
	private void setupPAM() {
		
		gridview.setAdapter(new MyGridAdapter (this));
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
				gridview.getChildAt(position).setBackgroundColor(Color.RED);
				/*Intent result = new Intent();
				result.putExtra("Date", position);
				result.putExtra("Month_Year", tv.getText().toString());
				setResult(Activity.RESULT_OK, result);
				finish();*/
				
			}
		});
		
		//gridview.getChildAt(6).setBackgroundColor(Color.BLACK);
		
	}

}
