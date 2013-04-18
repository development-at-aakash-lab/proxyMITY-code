package com.iitb.promitywifi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ivy.util.url.ApacheURLLister;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	String videourl;
	 final Context context = this;
	 private ProgressDialog mProgressDialog, progressBar;
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
		RESTART_INTENT = PendingIntent.getActivity(this.getBaseContext(), 0,
				new Intent(getIntent()), getIntent().getFlags());
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
						String dg2 = dg1.replaceAll("%20", " ");
						int j = dg2.lastIndexOf('.');
						ext = dg2.substring(j, dg2.length());
						System.out.println(ext);
						String dg3 = dg2.replaceAll(ext, "");
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
		
		
lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				TextView tv = (TextView) findViewById(R.id.title);
				String videoname = tv.getText().toString();
			//	String xmlname = videoname;
				videourl = videopath + videoname + ext;
				
				
				 final AlertDialog.Builder builder = new AlertDialog.Builder(ListVideoes.this);
                 builder.setMessage("Do you want to download this lecture video?")
                         .setCancelable(false)
                         .setPositiveButton("Yes",
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                    	 startDownload();
                                    	  mProgressDialog = new ProgressDialog(context);
                                          mProgressDialog.setMessage("Downloading file..");
                                          mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                          mProgressDialog.setCancelable(false);
                                          mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener() {
                                            
                                              public void onClick(DialogInterface dialog, int which) {
                                                  final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                  builder.setMessage("Are you sure you want cancel downloading?")
                                                          .setCancelable(false)
                                                          .setPositiveButton("Yes",
                                                                  new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog, int id) {
                                                                          dialog.dismiss();
                                                                         
                                                                       /*   String[] command = {"rm /mnt/sdcard/apl.tar.gz"};
                                                                          RunAsRoot(command);  */
                                                                          finish();
                                                                          android.os.Process.killProcess(android.os.Process.myPid());
                                                                      }
                                                                  })
                                                          .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                              public void onClick(DialogInterface dialog, int id) {
                                                                  mProgressDialog.show();
                                                              }
                                                          });
                                                  AlertDialog alert = builder.create();
                                                  alert.show();
                                                
                                              }
                                          });
                                          mProgressDialog.show();
                                      }
                                       
                                     
                                 })
                         .setNegativeButton("No", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 //mProgressDialog.show();
                            	 dialog.dismiss();
                             }
                         });
                 AlertDialog alert = builder.create();
                 alert.show();
				return true;
				
			}
		});

		}
	
	private void startDownload() {
    	if(isInternetOn()) {
            // INTERNET IS AVAILABLE, DO STUFF..
                Toast.makeText(ListVideoes.this, "Connected to network", Toast.LENGTH_SHORT).show();
            }else{
            // NO INTERNET AVAILABLE, DO STUFF..
                Toast.makeText(ListVideoes.this, "Network disconnected", Toast.LENGTH_SHORT).show();
                //rebootFlag = 1;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListVideoes.this);
                builder.setMessage("No Connection Found, please check your network setting!")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        android.os.Process
                                                .killProcess(android.os.Process.myPid());
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
              
            }  
    	/**
    	 * link for downloading data
    	 **/
    	String url = videourl;
    	new DownloadFileAsync().execute(url);
    }  
	
	
	
	 private final boolean isInternetOn() {
	    	// check internet connection via wifi   
	    	 	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    	 	//NetworkInfo mwifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    	 	//mwifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	            if( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
	            connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
	            connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
	            connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
	            	//Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
	            	return true;
	            } 
	            else if( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  
	            		connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
	            		//System.out.println(“Not Connected”);
	            		return false;
	            	}
	            	return false;
	    }
		
		
		
		 class DownloadFileAsync extends AsyncTask<String, String, String> {
		    	/**
		    	 * download zip from URL and write in '/mnt/sdcard'
		    	 **/
		        @Override        	
		        public void onPreExecute() {
		            super.onPreExecute();
		        }

		        public String doInBackground(String... aurl) {
		            int count;

		            try {
		                URL url = new URL(aurl[0]);
		                URLConnection conexion = url.openConnection();
		                conexion.connect();

		                int lenghtOfFile = conexion.getContentLength();
		                
		                if ( !(new File("mnt/sdcard/proxyMITY downloads")).exists())
		                {
		                	 new File("mnt/sdcard/proxyMITY downloads").mkdir();
		                }

		                InputStream input = new BufferedInputStream(url.openStream());
		                OutputStream output = new FileOutputStream(
		                        "/mnt/sdcard/proxyMITY downloads");

		                byte data[] = new byte[1024];

		                long total = 0;

		                while ((count = input.read(data)) != -1) {
		                    total += count;
		                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
		                    output.write(data, 0, count);
		                }
		                output.flush();
		                output.close();
		                input.close();
		            } catch (Exception e) {
		            }
		            return null;

		        }

		        public void onProgressUpdate(String... progress) {
		            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		        }
		        
		        public void onPostExecute(String unused) {
		        	mProgressDialog.dismiss();
		        	    
		        		    
		             	
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