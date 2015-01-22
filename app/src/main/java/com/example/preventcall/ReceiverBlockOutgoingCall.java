package com.example.preventcall;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class ReceiverBlockOutgoingCall extends BroadcastReceiver {
	
	static private String TAG = ReceiverBlockOutgoingCall.class.getSimpleName();
	
	SharedPreferences prefs;
	Context _context;

     @Override
     public void onReceive(Context context, Intent intent) {
    	 
    	 _context = context;
    	 
    	 String action = intent.getAction();
    	 Log.i(TAG, "Action received: " + action);
    	 
    	 String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
    	 Log.i(TAG, "Outgoing number: " + outgoingNumber);
    	 
    	 ArrayList<String> numbers = getNumbersFromPrefs();
    	 
    	 if(numbers.contains(outgoingNumber)){
    		 Log.d(TAG, "Found outgoing number: " + outgoingNumber);

             setResultData(null);

             Toast.makeText(context, "This call was blocked by Prevent Call application.",
                     Toast.LENGTH_LONG).show();

    	 }
    	 else{
    		 Log.d(TAG, "Outgoing number not found!");
    	 }
         
     }
     
     private ArrayList<String> getNumbersFromPrefs(){
 		
 		ArrayList<String> numbers = new ArrayList<String>();
 		
 		prefs = _context.getSharedPreferences(Configs.SHARED_PREFS, Activity.MODE_PRIVATE);
 		String jsonString = prefs.getString(Configs.SHARED_OUTGOING_CALL_LIST, null);
 		
 		if(null == jsonString){
 			return numbers;
 		}
 		
 		try {
 			JSONObject jsonObj = new JSONObject(jsonString);
 			JSONArray numbersArray = jsonObj.getJSONArray(Configs.JSON_ARRAY_NUMBERS);
 			
 			for(int i=0; i<numbersArray.length(); i++){
 				
 				JSONObject number = numbersArray.getJSONObject(i);
 				String num = number.getString(Configs.JSON_SINGLE_NUMBER);
 				Log.d(TAG, "num: " + num);
 				
 				numbers.add(num);
 				
 			}
 			
 			
 		} catch (JSONException e) {
 			e.printStackTrace();
 			return numbers;
 		}
 		
 		
 		return numbers;
 		
 	}
     
}