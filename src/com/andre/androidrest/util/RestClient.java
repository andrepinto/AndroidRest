package com.andre.androidrest.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/*
 * REST CLIENT 
 * 
 */
public class RestClient extends IntentService {

	private static final String TAG = RestClient.class.getName();
	public static final int GET = 0x1;
	public static final int POST = 0x2;
	public static final int PUT = 0x3;
	public static final int DELETE = 0x4;

	public static final String EXTRA_HTTP_VERB = "EXTRA_HTTP_VERB";
	public static final String EXTRA_PARAMS = "EXTRA_PARAMS";
	public static final String EXTRA_RESULT_RECEIVER = "EXTRA_RESULT_RECEIVER";
	public static final String REST_RESULT = "REST_RESULT";
	
	private ResultReceiver receiver;
	private Bundle params;
	private int verb;
	private Uri action;
	HttpRequestBase request;
	
	public RestClient() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
  Log.v("app", "entrei");
		action = intent.getData();
		Bundle extras = intent.getExtras();

		verb = extras.getInt(EXTRA_HTTP_VERB, GET);
		params = extras.getParcelable(EXTRA_PARAMS);
		receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);
		
		try {

			switch (verb) {
			case GET: {
				request = new HttpGet();
				//request.setHeader("", "");
				createUri(request, action, params);
			}
			break;
			
			
			case DELETE: {
                request = new HttpDelete();
                createUri(request, action, params);
            }
            break;
            
            case POST: {
                request = new HttpPost();
                request.setURI(new URI(action.toString()));
                
                HttpPost postRequest = (HttpPost) request;
                
                //postRequest.setHeader("Content-Type", "application/json");
                //postRequest.setHeader("", "");
                
                if (params != null) {
                 	StringEntity formEntity = new StringEntity(paramToString(params));
                    postRequest.setEntity(formEntity);
                }
            }
            break;
            
            case PUT: {
                request = new HttpPut();
                request.setURI(new URI(action.toString()));
                
                
                HttpPut putRequest = (HttpPut) request;
                
                if (params != null) {
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                    putRequest.setEntity(formEntity);
                }
            }
            break;
            
            
			default:
				break;
			}
			
		
			if(receiver!=null){
				
				setReturnValue();
			}
						
		} catch (Exception e) {
			Log.d(TAG, "Executing request:"+ e.getMessage());
			receiver.send(0, null);
		}

	}
	
	/*
	 * PUT RESULT
	 */
	public void setReturnValue() throws Exception{
		try {
	        HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);
            
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            
            int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;
            
            if (responseEntity != null) {
                Bundle resultData = new Bundle();
                resultData.putString(REST_RESULT, EntityUtils.toString(responseEntity));
                receiver.send(statusCode, resultData);
            }
            else {
                receiver.send(statusCode, null);
            }
   
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*
	 * CREATE URL
	 */
	private static void createUri(HttpRequestBase request, Uri uri, Bundle params) {
        try {
            if (params == null) {
                request.setURI(new URI(uri.toString()));
            }
            else {
                Uri.Builder uriBuilder = uri.buildUpon();
                
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }
                
                uri = uriBuilder.build();
                request.setURI(new URI(uri.toString()));
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: "+ uri.toString(), e);
        }
    }
	
	/*
	 * PUT BUNDLE PARAMS IN STRING
	 */
	private static String paramToString(Bundle params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
        
        for (String key : params.keySet()) {
            Object value = params.get(key);  
            return value.toString();
        }
        
        return "";
    }
	/*
	 * PUT BUNDLE PARAMS IN LIST
	 */
	 private static List<BasicNameValuePair> paramsToList(Bundle params) {
	        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
	        
	        for (String key : params.keySet()) {
	            Object value = params.get(key);
	            if (value != null) formList.add(new BasicNameValuePair(key, value.toString()));
	        }
	        
	        return formList;
	    }

}
