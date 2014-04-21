package com.cs571.zhengwang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void query(View view){
		ConnectivityManager conManager = (ConnectivityManager) 
		        getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
		     // fetch data
			EditText userInput = (EditText) findViewById(R.id.input);
			String input = userInput.getText().toString();
			String url = "http://cs-server.usc.edu:36586/examples/servlet/HW8Servlet?symbol=" + input;
			new QueryServletTask().execute(url);
		} else {
		     // display error
			TextView textview = (TextView) findViewById(R.id.textview);
			textview.setText("No network connection available.");
		}

	}
	
	private class QueryServletTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve data from servlet. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	TextView textview = (TextView) findViewById(R.id.textview);
            textview.setText(result);
       }
       
    }
	
	private String downloadUrl(String queryurl) throws IOException {
	    InputStream is = null;
	        
	    try {
	        URL url = new URL(queryurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        if(response == 200){
	        	is = conn.getInputStream();

	        	// 	Convert the InputStream into a string
	        	String content = readIt(is);
	        	return content;
	        }
	        else
	        	return "HTTP Connection Error. Error Code: " + Integer.toString(response);
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	
	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
	    Reader reader = new InputStreamReader(stream, "UTF-8");
	    BufferedReader br = null;
	    StringBuilder sb = new StringBuilder();
	    try{
	    	br = new BufferedReader(reader);
	    	String line;
	    	while((line = br.readLine()) != null){
	    		sb.append(line);
	    	}
	    }catch(IOException e){
	    	e.printStackTrace();
	    }finally{
	    	if(br != null){
	    		br.close();
	    	}
	    }
	    return sb.toString();
	}


}
