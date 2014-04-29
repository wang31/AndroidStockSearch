package com.cs571.zhengwang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	private static String serverResponse = null;
	private static String[] suggestions = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			/*getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();*/
			
	            FirstFragment firstFragment = new FirstFragment();
	            // Add the fragment to the 'fragment_container' FrameLayout
	            getSupportFragmentManager().beginTransaction()
	                    .add(R.id.fragment_place, firstFragment).commit();
	            
	            AutoCompleteTextView tx = (AutoCompleteTextView)findViewById(R.id.input);
	            tx.addTextChangedListener(new TextWatcher(){
	            	public void onTextChanged(CharSequence s, int start, int before, int count){
	            		getSuggestions(s.toString());
	            	}
	            	public void afterTextChanged(Editable s) {}
	                public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	            });
    	        tx.setThreshold(1);
    	        tx.setOnItemClickListener(new OnItemClickListener(){
    	        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
    	        		String item = (String)arg0.getItemAtPosition(arg2);
    	        		String symbol = item.substring(0, item.indexOf(","));
    	        		setTextField(symbol);
    	        		query(symbol);
    	        	}
    	        });
        		
	            
		}
	}
	private void setTextField(String symbol){
		AutoCompleteTextView tx = (AutoCompleteTextView)findViewById(R.id.input);
		tx.setText(symbol);
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
	
	public void displayNews(View view){
		/*SecondFragment fr = new SecondFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_place, fr);
		transaction.addToBackStack(null);
		transaction.commit();*/
		Intent intent = new Intent(this, HeadlinesActivity.class);
		intent.putExtra("com.csci571.zhengwang.message", serverResponse);
		startActivity(intent);
	}
	
	public void query(String symbol){
		ConnectivityManager conManager = (ConnectivityManager) 
		        getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
		     // fetch data
			String url = "http://cs-server.usc.edu:36586/examples/servlet/HW8Servlet?symbol=" + symbol;
			new QueryServletTask().execute(url);
		} else {
		     // display error
			WebView webview = (WebView) findViewById(R.id.webview);
    		webview.loadData("No network connection available.", "text/html", "UTF-8");
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
			WebView webview = (WebView) findViewById(R.id.webview);
    		webview.loadData("No network connection available.", "text/html", "UTF-8");
		}
	}
	
	private class QueryServletTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Error: Unable to retrieve data from servlet. URL may be invalid.";
            }
        }
        
        private String formatOneLine(String a, String b){
        	int lena = a.length();
        	int lenb = b.length();
        	StringBuilder sb = new StringBuilder();
        	sb.append(a);
        	int remain = 32 - lena -lenb;
        	for(int i = 0; i < remain; i++)
        		sb.append(" ");
        	sb.append(b);
        	return sb.toString();
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if(result.contains("Error")){
        		WebView webview = (WebView) findViewById(R.id.webview);
        		webview.loadData(result, "text/html", "UTF-8");
        		findViewById(R.id.webview).setVisibility(View.VISIBLE);
        		findViewById(R.id.newsbutton).setVisibility(View.INVISIBLE);
        		findViewById(R.id.newsbutton).setEnabled(false);
				findViewById(R.id.fbbutton).setVisibility(View.INVISIBLE);
				findViewById(R.id.fbbutton).setEnabled(false);
        	}else{
        		try {
        			serverResponse = result;
					JSONObject json = new JSONObject(result);
					JSONObject jsonresult = json.getJSONObject("result");
					WebView webview = (WebView) findViewById(R.id.webview);
					String text = "<div><center>" + jsonresult.getString("Name") + "(" + jsonresult.getString("Symbol") + ")</center>";
					JSONObject jsonquote = jsonresult.getJSONObject("Quote");
					text += "<center>" + jsonquote.getString("LastTradePriceOnly") + "</center>";
					if(jsonquote.getString("ChangeType").equals("+")){
						text += "<center>" + "<img src='http://www-scf.usc.edu/~csci571/2014Spring/hw6/up_g.gif'><span style='color:rgb(0,220,0)'>" + jsonquote.getString("Change") + "(" + jsonquote.getString("ChangeInPercent") + ")</span></center>";
					}else{
						text += "<center>" + "<img src='http://www-scf.usc.edu/~csci571/2014Spring/hw6/down_r.gif'><span style='color:rgb(255,0,0)'>" + jsonquote.getString("Change") + "(" + jsonquote.getString("ChangeInPercent") + ")</span></center>";
					}
					text += "<pre>     " + formatOneLine("Prev Close", jsonquote.getString("PreviousClose")) + "</pre>";
					text += "<pre>     " + formatOneLine("Open", jsonquote.getString("Open")) + "</pre>";
					text += "<pre>     " + formatOneLine("Bid", jsonquote.getString("Bid")) + "</pre>";
					text += "<pre>     " + formatOneLine("Ask", jsonquote.getString("Ask")) + "</pre>";
					text += "<pre>     " + formatOneLine("1st Yr Target", jsonquote.getString("OneYearTargetPrice")) + "</pre>";
					text += "<pre>     " + formatOneLine("Day Range", jsonquote.getString("DaysLow") + "-" + jsonquote.getString("DaysHigh")) + "</pre>";
					text += "<pre>     " + formatOneLine("52 wk Range", jsonquote.getString("YearLow") + "-" + jsonquote.getString("YearHigh")) + "</pre>";
					text += "<pre>     " + formatOneLine("Volume", jsonquote.getString("Volume")) + "</pre>";
					text += "<pre>     " + formatOneLine("Avg Vol(3m)", jsonquote.getString("AverageDailyVolume")) + "</pre>";
					text += "<pre>     " + formatOneLine("Market Cap", jsonquote.getString("MarketCapitalization")) + "</pre>";
					//text += "<span style='text-align:left'>Market Cap</span>" + "<span style='text-align:right'>" + jsonquote.getString("MarketCapitalization") + "</span>";
					text += "<center><img src='" + jsonresult.getString("StockChartImageURL") + "'></center></div>";
					webview.loadData(text, "text/html", "UTF-8");
					findViewById(R.id.webview).setVisibility(View.VISIBLE);
					findViewById(R.id.newsbutton).setVisibility(View.VISIBLE);
					findViewById(R.id.newsbutton).setEnabled(true);
					findViewById(R.id.fbbutton).setVisibility(View.VISIBLE);
					findViewById(R.id.fbbutton).setEnabled(true);
					
        		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
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
	        	return "Error: HTTP Connection Error. Error Code: " + Integer.toString(response);
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
	
	private void getSuggestions(String input){
		String url = "http://autoc.finance.yahoo.com/autoc?query=" + input + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		new getSuggestionsTask().execute(url);
	}
	
	private void setAutoCompleteAdapter(){
		ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,suggestions);
		AutoCompleteTextView tx = (AutoCompleteTextView)findViewById(R.id.input);
		tx.setAdapter(adapter);
		if(tx.getText().toString().length() == 1){
			tx.showDropDown();
		}
	}
	private class getSuggestionsTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
            
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Error: Unable to retrieve data from servlet. URL may be invalid.";
            }
        }
		
		protected void onPostExecute(String result) {
			if(!result.contains("Error")){
				String data = result.substring(39, result.length() - 1);
				try{
					JSONObject json = new JSONObject(data);
					JSONArray jarray = json.getJSONObject("ResultSet").getJSONArray("Result");
					suggestions = new String[jarray.length()];
					for(int i = 0; i < jarray.length(); i++){
						JSONObject item = jarray.getJSONObject(i);
						String suggest = item.getString("symbol") + ", " + item.getString("name") + "(" + item.getString("exch") + ")";
						suggestions[i] = suggest;
					}
					setAutoCompleteAdapter();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
	}
	//YAHOO.Finance.SymbolSuggest.ssCallback
	//private class YAHOO{
		
	//}
}
