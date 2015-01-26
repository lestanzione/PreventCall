package com.example.preventcall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IncomingCallListActivity extends Activity {
	
	ListView mIncomingCallList;
	Button mRegisterButton;
    Button mAddContactButton;
	ArrayAdapter<String> mAdapter;
	
	SharedPreferences prefs;
	
	private static String TAG = IncomingCallListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming_call_list);
		
		mIncomingCallList = (ListView) findViewById(R.id.incomingCallList);
		mIncomingCallList.setEmptyView(findViewById(R.id.emptyIncomingCallList));
		
		mRegisterButton = (Button) findViewById(R.id.registerIncomingNumberButton);
        mAddContactButton = (Button) findViewById(R.id.addContactIncomingNumberButton);
		
		ArrayList<String> numbers = getNumbersFromPrefs();
		
		mAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.number_block, R.id.NumberBlockTextId, numbers);
		
		mIncomingCallList.setAdapter(mAdapter);
		
		mRegisterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				addNumber();
				
			}
		});

        mAddContactButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                addContact();

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

    public void addContact(){

        Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, Configs.CODE_ADD_CONTACT);

    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == Configs.CODE_NEW_NUMBER && resultCode == RESULT_OK){
			
			String newNumber = data.getExtras().getString(Configs.EXTRA_NEW_NUMBER);
			mAdapter.add(newNumber);
			
			saveChanges();
			
		}
        else if(requestCode == Configs.CODE_ADD_CONTACT && resultCode == RESULT_OK){

            Uri contactData = data.getData();
            Cursor c =  managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d(TAG, "Name: " + name);
                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                Log.d(TAG, "ID: " + id);

                if (Integer.parseInt(c.getString(
                        c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = managedQuery(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d(TAG, number);
                        mAdapter.add(number);

                        saveChanges();
                    }
                    pCur.close();
                }
            }

        }
		
	}
	
	private ArrayList<String> getNumbersFromPrefs(){
		
		ArrayList<String> numbers = new ArrayList<String>();
		
		prefs = getSharedPreferences(Configs.SHARED_PREFS, MODE_PRIVATE);
		String jsonString = prefs.getString(Configs.SHARED_INCOMING_CALL_LIST, null);
		
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
		
		int totalNumbers = mIncomingCallList.getCount();
		Log.d(TAG, "totalNumbers: " + totalNumbers);
		
		JSONArray numbersArray = new JSONArray();
		
		for(int i=0; i<totalNumbers; i++){
			
			Log.d(TAG, mIncomingCallList.getItemAtPosition(i).toString());
			
			JSONObject numberObj = new JSONObject();
			try {
				numberObj.put(Configs.JSON_SINGLE_NUMBER, mIncomingCallList.getItemAtPosition(i).toString());
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
		editor.putString(Configs.SHARED_INCOMING_CALL_LIST, jsonObj.toString());
		editor.commit();
		
	}

}
