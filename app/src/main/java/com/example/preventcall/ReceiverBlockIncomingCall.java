package com.example.preventcall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import com.android.internal.telephony.ITelephony;

public class ReceiverBlockIncomingCall extends BroadcastReceiver {

    static private String TAG = ReceiverBlockIncomingCall.class.getSimpleName();

    SharedPreferences prefs;
    Context _context;

    private ITelephony telephonyService;

    @Override
    public void onReceive(Context context, Intent intent) {

        _context = context;

        String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

        Log.i(TAG, "Call state: " + state);

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "Incoming number: " + incomingNumber);

            ArrayList<String> numbers = getNumbersFromPrefs();

            if(numbers.contains(incomingNumber)){
                Log.d(TAG, "Found incoming number: " + incomingNumber);
                block();
            }
            else{
                Log.d(TAG, "Incoming number not found!");
            }
        }

    }

    private void block(){
        TelephonyManager telephony = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);

            Toast.makeText(_context, "This call was blocked by Prevent Call application.",
                    Toast.LENGTH_LONG).show();

            telephonyService.silenceRinger();
            telephonyService.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getNumbersFromPrefs(){

        ArrayList<String> numbers = new ArrayList<String>();

        prefs = _context.getSharedPreferences(Configs.SHARED_PREFS, Activity.MODE_PRIVATE);
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

}