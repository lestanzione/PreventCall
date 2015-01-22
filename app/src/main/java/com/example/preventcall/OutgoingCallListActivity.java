package com.example.preventcall;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class OutgoingCallListActivity extends Activity {
	
	ListView mOutgoingCallList;
	Button mRegisterButton;
	ArrayAdapter<String> mAdapter;
	
	SharedPreferences prefs;
	
	private static String TAG = OutgoingCallListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outgoing_call_list);
		
		mOutgoingCallList = (ListView) findViewById(R.id.outgoingCallList);
		mOutgoingCallList.setEmptyView(findViewById(R.id.emptyOutgoingCallList));
		
		mRegisterButton = (Button) findViewById(R.id.registerOutgoingNumberButton);
		
		ArrayList<String> numbers = getNumbersFromPrefs();
		
		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.number_block, R.id.NumberBlockTextId, numbers);
		
		mOutgoingCallList.setAdapter(mAdapter);
		
		mRegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				addNumber();
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void addNumber(){
		
		Intent newNumberIntent = new Intent(getApplicationContext(), NewNumberActivity.class);
		startActivityForResult(newNumberIntent, Configs.CODE_NEW_NUMBER);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == Configs.CODE_NEW_NUMBER && resultCode == RESULT_OK){
			
			String newNumber = data.getExtras().getString(Configs.EXTRA_NEW_NUMBER);
			mAdapter.add(newNumber);
			
			saveChanges();
			
		}
		
	}

	private ArrayList<String> getNumbersFromPrefs(){

		ArrayList<String> numbers = new ArrayList<String>();
		
		prefs = getSharedPreferences(Configs.SHARED_PREFS, MODE_PRIVATE);
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
	
	private void saveChanges(){
		
		int totalNumbers = mOutgoingCallList.getCount();
		Log.d(TAG, "totalNumbers: " + totalNumbers);
		
		JSONArray numbersArray = new JSONArray();
		
		for(int i=0; i<totalNumbers; i++){
			
			Log.d(TAG, mOutgoingCallList.getItemAtPosition(i).toString());
			
			JSONObject numberObj = new JSONObject();
			try {
				numberObj.put(Configs.JSON_SINGLE_NUMBER, mOutgoingCallList.getItemAtPosition(i).toString());
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			
			numbersArray.put(numberObj);
			
		}
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(Configs.JSON_ARRAY_NUMBERS, numbersArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.d(TAG, "jsonObj: " + jsonObj.toString());
		
		prefs = getSharedPreferences(Configs.SHARED_PREFS, MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(Configs.SHARED_OUTGOING_CALL_LIST, jsonObj.toString());
		editor.commit();
		
	}

}
