package com.example.democracycle;

public class Stressloctime {
	String hh_mm_ss;
	String longitude;
	String latitude;
	String stressLevel;
	String noise;
	String activity;
	public Stressloctime(){
		this.hh_mm_ss="";
		this.longitude="";
		this.latitude="";
		this.stressLevel="";
		this.noise="";
		this.activity="";
	}
	public Stressloctime(String hms,String lon,String lat,String str, String n, String act){
		this.hh_mm_ss=hms;
		this.longitude=lon;
		this.latitude=lat;
		this.stressLevel=str;
		this.noise=n;
		this.activity=act;
	}
	public String getHHMMSS(){
		return this.hh_mm_ss;
	}
	public String getLongitude(){
		return this.longitude;
	}
	public String getLatitude(){
		return this.latitude;
	}
	public String getStress(){
		return this.stressLevel;
	}
	public String getNoise(){
		return this.noise;
	}
	public String getActivity(){
		return this.activity;
	}
}
