package com.andre.androidrest.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class RestClientResult extends ResultReceiver {
	
	private Receiver mReceiver;
	
	public RestClientResult(Handler handler) {
		super(handler);	
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) { 
		if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode,resultData);
        }
	}

	public Receiver getmReceiver() {
		return mReceiver;
	}

	public void setmReceiver(Receiver mReceiver) {
		this.mReceiver = mReceiver;
	}
	
	
	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultBundle);
	}
	

}
