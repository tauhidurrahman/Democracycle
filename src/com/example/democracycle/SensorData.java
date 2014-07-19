package com.example.democracycle;

public class SensorData {
	
	//private variables
    int _id;
    String _isManual;
    String _StartTimeOfThisRide;
    //declare audio, image and location file
    String _LocationFileName;//Latitude,Longitude,Altitude,Provider,Speed,Bearing
    String _AccelerometerFileName;
    String _AudioFileName;
    String _ImageFileName;
    String _ImageContent;//this is the image tag in the report activity
    //Context Information
    String _Question1;
    String _Question2;
    String _Question3;
    String _Question4;
    String _Question5;
    String _Question6;
    String _Question7;
    String _Question8;
    String _Question9;
    String _Question10;
    String _Question11;
    String _Question12;
    
 
    // Empty constructor
    public SensorData(){
    	this._isManual="";
    	this._StartTimeOfThisRide="";
    	this._LocationFileName="";
    	this._AccelerometerFileName="";
    	this._AudioFileName="";
    	this._ImageFileName="";
    	this._ImageContent="";
    	this._Question1="";
    	this._Question2="";
    	this._Question3="";
    	this._Question4="";
    	this._Question5="";
    	this._Question6="";
    	this._Question7="";
    	this._Question8="";
    	this._Question9="";
    	this._Question10="";
    	this._Question11="";
    	this._Question12="";
    }
    
    public SensorData(int id, String isManu, String dt, String lfn, String acclfn, String afn, String ifn, 
    		String imageContent, String q1, String q2, String q3, String q4, String q5, 
    		String q6, String q7, String q8, String q9, String q10, String q11, String q12){
    	this._id = id;
    	this._isManual=isManu;
    	this._StartTimeOfThisRide=dt;
    	this._LocationFileName=lfn;
    	this._AccelerometerFileName=acclfn;
    	this._AudioFileName=afn;
    	this._ImageFileName=ifn;
    	this._ImageContent=imageContent;
    	this._Question1=q1;
    	this._Question2=q2;
    	this._Question3=q3;
    	this._Question4=q4;
    	this._Question5=q5;
    	this._Question6=q6;
    	this._Question7=q7;
    	this._Question8=q8;
    	this._Question9=q9;
    	this._Question10=q10;
    	this._Question11=q11;
    	this._Question12=q12;
    }
    
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
    
    // getting user isManual either true or false
    public void setIsManual(String isManu){
        this._isManual=isManu;
    }
    
    // setting location
    public void setStartTimeOfThisRide(String dt){
    	this._StartTimeOfThisRide=dt;
    }
    //setting location file name
    public void setLocationFileName(String lfn){
    	this._LocationFileName=lfn;
    }
    //setting accelerometer file name
    public void setAccelerometerFileName(String acclfn){
    	this._AccelerometerFileName=acclfn;
    }
    //setting image file name
    public void setImageFileName(String ifn){
    	this._ImageFileName=ifn;
    }
    //setting image content
    public void setImageContent(String ic){
    	this._ImageContent=ic;
    }
    //setting audio file name
    public void setAudioFileName(String afn){
    	this._AudioFileName=afn;
    }
    
    //Set Questions
    public void setQuestions(String q1,String q2,String q3,String q4,
    		String q5,String q6,String q7,String q8,String q9,String q10,String q11,String q12){
    	
    	this._Question1=q1;
    	this._Question2=q2;
    	this._Question3=q3;
    	this._Question4=q4;
    	this._Question5=q5;
    	this._Question6=q6;
    	this._Question7=q7;
    	this._Question8=q8;
    	this._Question9=q9;
    	this._Question10=q10;
    	this._Question11=q11;
    	this._Question12=q12;
    }
    
    // getting user isManual
    public String getIsManual(){
        return this._isManual;
    }
    
    // getting start time
    public String getStartTimeOfThisRide(){
        return this._StartTimeOfThisRide;
    }
    
    // getting location file name
    public String getLocationFileName(){
        return this._LocationFileName;
    }
    
    // getting accelerometer file name
    public String getAccelerometerFileName(){
        return this._AccelerometerFileName;
    }
    
    // getting audio file name
    public String getAudioFileName(){
        return this._AudioFileName;
    }
    
    // getting image file name
    public String getImageFileName(){
        return this._ImageFileName;
    }
    
    //getting imageContent
    public String getImageContent(){
    	return this._ImageContent;
    }

    
    // getting question1 name
    public String getQuestion1(){
        return this._Question1;
    }
    
    // getting question2 name
    public String getQuestion2(){
        return this._Question2;
    }
    
    // getting question3 name
    public String getQuestion3(){
        return this._Question3;
    }
    
    // getting question4 name
    public String getQuestion4(){
        return this._Question4;
    }
    
    // getting question5 name
    public String getQuestion5(){
        return this._Question5;
    }
    
    // getting question6 name
    public String getQuestion6(){
        return this._Question6;
    }
    
    // getting question7 name
    public String getQuestion7(){
        return this._Question7;
    }
    
    // getting question8 name
    public String getQuestion8(){
        return this._Question8;
    }
    
    // getting question9 name
    public String getQuestion9(){
        return this._Question9;
    }
    
    // getting question10 name
    public String getQuestion10(){
        return this._Question10;
    }
    
    // getting question11 name
    public String getQuestion11(){
        return this._Question11;
    }
    
    // getting question12 name
    public String getQuestion12(){
        return this._Question12;
    }
}
