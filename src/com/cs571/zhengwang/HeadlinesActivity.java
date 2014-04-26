package com.cs571.zhengwang;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.os.Build;

public class HeadlinesActivity extends ActionBarActivity {
	private static String message = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_headlines);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();*/
		
			Intent intent = getIntent();
			message = intent.getStringExtra("com.csci571.zhengwang.message");
			TextView txtview = (TextView)findViewById(R.id.headlinesview);
			//txtview.loadData("<a href='www.cnn.com'>cnn is a mother fucking bitch</a>", "text/html", "UTF-8");
			//txtview.setText(message);
			//txtview.setText(Html.fromHtml("<a href='www.cnn.com'>cnn is a mother fucking bitch</a>"));
			//txtview.setMovementMethod(LinkMovementMethod.getInstance());
			//txtview.setText("<a href='www.cnn.com'>cnn</a><br>");
			//txtview.setText(Html.fromHtml("<a href='www.cnn.com'>bbc</a><br>"));
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
