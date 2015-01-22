package com.example.preventcall;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewNumberActivity extends Activity {
	
	EditText mNewNumber;
	Button mSaveNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_number);
		
		mNewNumber = (EditText) findViewById(R.id.newNumberEditText);
		mSaveNumber = (Button) findViewById(R.id.newNumberButton);
		
		mSaveNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveNumber();
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_number, menu);
		return true;
	}
	
	private void saveNumber(){
		
		String newNumber = mNewNumber.getText().toString().trim();
		
		if(null == newNumber || newNumber.equals("")){
			
			Toast.makeText(getApplicationContext(), "Please enter a valid number!",
					Toast.LENGTH_LONG).show();
			return;
			
		}
		
		Intent intent = new Intent();
		intent.putExtra(Configs.EXTRA_NEW_NUMBER, newNumber);
		
		setResult(RESULT_OK, intent);
		
		finish();
		
	}

}
