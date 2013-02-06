package com.andre.androidrest;

import com.andre.androidrest.util.RestClient;
import com.andre.androidrest.util.RestClientResult;
import com.google.gson.Gson;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements RestClientResult.Receiver {
	
	private RestClientResult receiver;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		receiver = new RestClientResult(new Handler());
		receiver.setmReceiver(this);

		callService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultBundle) {
		Log.v("app", "result:"+resultCode);
		Toast.makeText(this, ""+resultCode, Toast.LENGTH_SHORT).show();
		
	}

	
	
	public void callService(){
		Intent intent = new Intent(this, RestClient.class);
		intent.setData(Uri.parse("http://api.justin.tv/api/stream/list.json"));

		Gson json = new Gson();
		Bundle params = new Bundle();
		
		//params.putString("body", json.toJson(activity.getLogin()));
	
	
		intent.putExtra(RestClient.EXTRA_PARAMS, params);
		intent.putExtra(RestClient.EXTRA_RESULT_RECEIVER, receiver);

		
		startService(intent);
	}
	
	
}
