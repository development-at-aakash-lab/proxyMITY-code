package com.iitb.promitywifi;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ivy.util.url.ApacheURLLister;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListVideoes extends Activity {
	/** Called when the activity is first created. */
	ListView lv;
	EditText edittext;
	URL url1;
	List<URL> list = new ArrayList<URL>();
	ArrayList<String> files = new ArrayList<String>();
	public static final int OPEN_SETTINGS_REQUEST = 1;
	Activity ACTIVITY;
	PendingIntent RESTART_INTENT;
	private SharedPreferences settings;
	String videopath;
	String ip_address;
	String ext;
	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private ArrayList<String> arraylist = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listvideoes);
		ACTIVITY = this;
		//RESTART_INTENT = PendingIntent.getActivity(this.getBaseContext(), 0,
			//	new Intent(getIntent()), getIntent().getFlags());
		edittext = (EditText) findViewById(R.id.EditText01);
		lv = (ListView) findViewById(R.id.listView1);
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		ip_address = settings.getString("cameraStreamURL", "<doesnt-exist>");
		if (validate_ip(ip_address)) {
			videopath = "http://" + ip_address + "/videos/";

			try {
				url1 = new URL(videopath);
				ApacheURLLister lister1 = new ApacheURLLister();
				list = lister1.listFiles(url1);
				for (int i = 0; i < list.size(); i++) {
					String dg = list.get(i).toString();
					if (dg.contains(".mp4") || dg.contains(".3gp")
							|| dg.contains(".MP4") || dg.contains(".3GP")) {
						String dg1 = dg.substring(videopath.length(),
								dg.length());
						String dg2 = dg1.replaceAll("%20", "");
						int j = dg2.lastIndexOf('.');
						ext = dg2.substring(j, dg2.length());
						System.out.println(ext);
						String dg3 = dg2.replaceAll(ext, " ");
						files.add(dg3);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ListVideoes.this);
			alertDialogBuilder.setTitle("Alert");
			alertDialogBuilder
					.setMessage(
							"InValid IP Address, Go To Menu-->Wifi-Settings")
					.setIcon(R.drawable.proxy)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// ListVideoes.this.finish();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
if (list.size()!=0)
{
	

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(
				ListVideoes.this, R.layout.videorow, R.id.title, files);
		lv.setAdapter(directoryList);

		edittext.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				arraylist.clear();
				for (int i = 0; i < files.size(); i++) {
					// String dg = edittext.getText().toString();
					if ((files.get(i).toString().toLowerCase())
							.contains(edittext.getText().toString()
									.toLowerCase())) {

						arraylist.add(files.get(i));
					}
				}

				lv.setAdapter(new ArrayAdapter<String>(ListVideoes.this,
						R.layout.videorow, R.id.title, arraylist));

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			public void afterTextChanged(Editable s) {
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.title);
				String dg = tv.getText().toString();
				String xmlname = dg;
				String ss = videopath + dg + ext;
				System.out.println(ss);
				Intent intent = new Intent(ListVideoes.this, Videoview.class);
				intent.putExtra("xmlname", xmlname);
				intent.putExtra("ipaddress", ip_address);
				intent.putExtra("videofilepath", ss);
				startActivity(intent);

			}
		});

		}

else{
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setIcon(R.drawable.proxy);
	builder.setTitle("proxyMITY videos are not listed in the tablet!!!");
	builder.setMessage(	"1. Please check whether Wi-Fi is enabled on the tablet "+"\n"
        	+"\n"+"2. Check whether you have correctly entered the IP address of your server "
			+"\n"+"		2.1 Select Menu button"
			+"\n"+"		2.2 you will see 2 options WiFi Setting and Help"
			+"\n"+"		2.3 Select WiFi settings option and then enter the correct IP "
			+"\n"+"			addrses of the server"
	+"\n"+"\n"+"3. Check whether there are proxyMITY videos present in your server")
	
	       .setCancelable(false)
	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	
	        	   ListVideoes.this.finish();
	        	
	           }
	       });
	AlertDialog alert = builder.create();   
	alert.show();


}
	}
	public boolean validate_ip(final String ip) {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.wifi:
			Intent i = new Intent(ListVideoes.this, SettingsActivity.class);
			startActivityForResult(i, OPEN_SETTINGS_REQUEST);
			return true;
		
		case R.id.help:
			Intent help = new Intent(ListVideoes.this, Help.class);
			startActivityForResult(help, OPEN_SETTINGS_REQUEST);
			return true;
			
			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static boolean exists(String URLName) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(URLName)
					.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OPEN_SETTINGS_REQUEST) {
			if (resultCode == RESULT_OK) {
			} else if (resultCode == RESULT_CANCELED) {
				AlarmManager mgr = (AlarmManager) ACTIVITY
						.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
						RESTART_INTENT);
				System.exit(2);
				System.out.println("restarting app");

			}
		}
	}
}