package com.app.imc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.app.imc.R;
import com.app.imc.parser.Parser;


public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		new ParseFiles(getApplicationContext()).execute();
	}
	
	private class ParseFiles extends AsyncTask<Void, Void, Void> {
		
		private final Context context;
		
		public ParseFiles(Context context) {
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Parser.getInstance().setup();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}

	}
	
}
