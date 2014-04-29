package com.cs571.zhengwang;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.app.AlertDialog;

public class HeadlinesActivity extends ListActivity {
	private static String message = null;
	private static String[] titles = null;
	private static String[] urls = null;
	private static int current = 0;
	private final Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_headlines);
		Intent intent = getIntent();
		message = intent.getStringExtra("com.csci571.zhengwang.message");
		init();
		displayToast();
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_titles, titles));
		ListView listview = getListView();
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				current = position;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setMessage("View News");
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("View", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[current]));
						startActivity(intent);
					}
				});
				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});	
				try{
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();*/
		
			
			//TextView txtview = (TextView)findViewById(R.id.headlinesview);
			//displayNews();
			//displayToast();
			//txtview.loadData("<a href='www.cnn.com'>cnn is a mother fucking bitch</a>", "text/html", "UTF-8");
			//txtview.setText(message);
			//txtview.setText(Html.fromHtml("<a href='www.cnn.com'>cnn is a mother fucking bitch</a>"));
			//txtview.setMovementMethod(LinkMovementMethod.getInstance());
			//txtview.setText("<a href='www.cnn.com'>cnn</a><br>");
			//txtview.setText(Html.fromHtml("<a href='www.cnn.com'>bbc</a><br>"));
	}
	
	private void init(){
		try{
			JSONObject json = new JSONObject(message);
			JSONArray ja = json.getJSONObject("result").getJSONObject("News").getJSONArray("Item"); 
			int len = ja.length();
			titles = new String[len];
			urls = new String[len];
			for(int i = 0; i < len; i++){
				if(ja.getJSONObject(i).getString("Title").contains("\ufffd")){
					String temp = ja.getJSONObject(i).getString("Title");
					temp = temp.replaceFirst("\ufffd", "'");
					titles[i] = temp.replace("\ufffd", "");
				}
				else
					titles[i] = ja.getJSONObject(i).getString("Title");
				urls[i] = ja.getJSONObject(i).getString("Link");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void displayNews(){
		try{
			JSONObject json = new JSONObject(message);
			JSONArray ja = json.getJSONObject("result").getJSONObject("News").getJSONArray("Item"); 
			int len = ja.length();
			for(int i = 0; i < len; i++){
				SpannableString ss = new SpannableString(ja.getJSONObject(i).getString("Title") + "\n");
				ClickableSpan clickableSpan = new ClickableSpan() {
					@Override
					public void onClick(View textView) {
						displayToast();
					}
				};
				ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				TextView textView = (TextView) findViewById(R.id.headlinesview);
				textView.append(ss);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void displayToast(){
		try{
			JSONObject json = new JSONObject(message);
			int len = json.getJSONObject("result").getJSONObject("News").getJSONArray("Item").length();
			Context context = getApplicationContext();
			CharSequence text = "Showing " + Integer.toString(len) + " headlines";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.headlines, menu);
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
	/*public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_headlines,
					container, false);
			return rootView;
		}
	}*/

}
