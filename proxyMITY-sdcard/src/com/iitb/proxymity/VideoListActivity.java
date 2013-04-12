package com.iitb.proxymity;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListActivity extends Activity {
	// Initializations and declarations of objects and variables
	
    final Context context = this;
	private Cursor videocursor;
	 AlertDialog help_dialog;
	    private ProgressDialog mProgressDialog, progressBar;
	ListView videolist;
	int count;
	String folder_path,main_path;
	  List<String> folder;                            ///newwwwww
	  int i = 0;										///newwwwww
	ContentResolver cr;
	 File checkTar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videolistactivity);
		File list=new File("/mnt/extsd/proxyMITY/video/");
		checkTar = new File("/mnt/sdcard/proxyMITY.zip");
		if (list.exists()||new File("/mnt/sdcard/proxyMITY/video/").exists()||new File ("/mnt/external_sd/proxyMITY/video/").exists())
		
		{
			if (list.exists())
			{	folder = getListOfFiles("/mnt/extsd/proxyMITY/video/");
			 System.out.println("folder"+folder);
			 folder_path="/mnt/extsd/proxyMITY/video/";
			 main_path="/mnt/extsd/proxyMITY/";
			}
			
			else if(new File("/mnt/sdcard/proxyMITY/video/").exists())
			{
					
				 folder = getListOfFiles("/mnt/sdcard/proxyMITY/video/");
				 System.out.println("folder"+folder);
				 folder_path="/mnt/sdcard/proxyMITY/video/";
				 main_path="/mnt/sdcard/proxyMITY/";
			}
			else if(new File ("/mnt/external_sd/proxyMITY/video/").exists())
			{
				
				 folder = getListOfFiles("/mnt/external_sd/proxyMITY/video/");
				 System.out.println("folder"+folder);
				 folder_path="/mnt/external_sd/proxyMITY/video/";
				 main_path="/mnt/external_sd/proxyMITY/";
			}
			
			
				videolist = (ListView) findViewById(R.id.PhoneVideoList);
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.row,R.id.textView2, folder);
				videolist.setAdapter(arrayAdapter);
			
				videolist.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						TextView tv = (TextView) view.findViewById(R.id.textView2);
						
						String v_path = folder_path+tv.getText().toString();
					
						 String display = tv.getText().toString();
						System.out.println("path"+display);
						int index = display.lastIndexOf(".");
						String xmlname = display.substring(0, index);
						Intent intent = new Intent(VideoListActivity.this, Videoview.class);
						intent.putExtra("xmlname", xmlname);// sending xml name in Videoview
															// Activity
						intent.putExtra("videofilename", v_path);// sending video file
																	// path in next activity
						intent.putExtra("mainpath", main_path);
						startActivity(intent);
					
					}
				});
			

		}
		
		else
		{
			// download
        	// extract
        	// reboot
        	//Toast.makeText(context, "start downloading", Toast.LENGTH_SHORT).show();
        	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.download_source,null);

            // Building DatepPcker dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    VideoListActivity.this);        	        	
            builder.setView(layout);
            builder.setTitle("Notice");
            builder.setCancelable(false);
            Button btnNO = (Button) layout.findViewById(R.id.btnNo);
           
            btnNO.setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		
            		AlertDialog.Builder builder = new AlertDialog.Builder(context);
                	builder.setIcon(R.drawable.proxy);
                	builder.setTitle("proxyMITY videos are not present in the tablet!!!");
                	builder.setMessage(	"Store the lecture videos at any one of the"+"\n"
                	+"following locations"+"\n"+"\n"+"1. mnt/sdcard/proxyMITY"
                			+"\n"+"2. mnt/extsd/proxyMITY")
                	
                	       .setCancelable(false)
                	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                	           public void onClick(DialogInterface dialog, int id) {
                	        	
                	        	   VideoListActivity.this.finish();
                	        	
                	           }
                	       });
                	AlertDialog alert = builder.create();   
                	alert.show();
                }	
            });	
          
            Button btnyes = (Button) layout.findViewById(R.id.btnyes);
            btnyes.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    startDownload();
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Downloading file..");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener() {
                      
                        public void onClick(DialogInterface dialog, int which) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(VideoListActivity.this);
                            builder.setMessage("Are you sure you want cancel downloading?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    help_dialog.dismiss();
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
            });
            
            help_dialog = builder.create();
            help_dialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            // customizing the width and location of the dialog on screen
            lp.copyFrom(help_dialog.getWindow().getAttributes());
            lp.width = 500;
            help_dialog.getWindow().setAttributes(lp);
		}
		
		/*else{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setIcon(R.drawable.proxy);
        	builder.setTitle("proxyMITY videos are not present in the tablet!!!");
        	builder.setMessage(	"Please check whether videos are present at any one of the"+"\n"
        	+"following locations"+"\n"+"\n"+"1. go to mnt/sdcard/proxyMITY"
        			+"\n"+"2. go to mnt/extsd/proxyMITY")
        	
        	       .setCancelable(false)
        	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	
        	        	   VideoListActivity.this.finish();
        	        	
        	           }
        	       });
        	AlertDialog alert = builder.create();   
        	alert.show();
		
		
		 
	}*/
	}
	private void startDownload() {
    	if(isInternetOn()) {
            // INTERNET IS AVAILABLE, DO STUFF..
                Toast.makeText(context, "Connected to network", Toast.LENGTH_SHORT).show();
            }else{
            // NO INTERNET AVAILABLE, DO STUFF..
                Toast.makeText(context, "Network disconnected", Toast.LENGTH_SHORT).show();
                //rebootFlag = 1;
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoListActivity.this);
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
    	 * global github link for downloading image
    	 **/
    	String url = "http://www.it.iitb.ac.in/AakashApps/repo/proxyMITY.zip";
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
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.video_list, menu);
	        return true;
	    }

	
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item)
	    {
	 
	        switch (item.getItemId())
	        {
	        case R.id.help:
	            // Single menu item is selected do something
	            // Ex: launching new activity/screen or show alert message
	            //Toast.makeText(UserManual.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
	        	
	        	Intent helpweb = new Intent(VideoListActivity.this,Help.class);
	        	startActivity(helpweb);
	        	return true;
	        	
	        
	 
	        
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    } 
	 

	private List<String> getListOfFiles(String path) {
		// TODO Auto-generated method stub
		System.out.println("list order");
		File files = new File(path);
		System.out.println("list order2222");
		List<String> list = new ArrayList<String>();
		 for (File f : files.listFiles()) {
	          
		
					list.add(f.getName());
				i++;
				
			
	                // make something with the name
	        }
		 System.out.println("list order"+list);
	
		return list;
		
		
		
		
	}

	 private void spinner() {
	    	// will start spinner first and then extraction
	    	
	    	// start spinner to show extraction progress
	    	progressBar = new ProgressDialog(context);
	        progressBar.setCancelable(false);
	        progressBar.setMessage("Extracting files, please wait...");
	        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progressBar.show();
	        String zipFile = Environment.getExternalStorageDirectory() + "/proxyMITY.zip"; 
	        String unzipLocation = Environment.getExternalStorageDirectory()+"/"; 
	        new File("mnt/sdcard/proxyMITY").mkdir();
	        new File("mnt/sdcard/proxyMITY/video").mkdir();
	        new File("mnt/sdcard/proxyMITY/xml").mkdir();
	        
	        Decompress d = new Decompress(zipFile, unzipLocation); 
	        d.unzip(); 
	      //  Toast.makeText(context, "unzipped", Toast.LENGTH_SHORT).show();
	        
	       
	    }
	
	 class DownloadFileAsync extends AsyncTask<String, String, String> {
	    	/**
	    	 * download tar.gz from URL and write in '/mnt/sdcard'
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

	                InputStream input = new BufferedInputStream(url.openStream());
	                OutputStream output = new FileOutputStream(
	                        "/mnt/sdcard/proxyMITY.zip");

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
	        	help_dialog.dismiss();
	        	if (checkTar.exists()){
	        		spinner();
	        		
	        	}
	        	  new Thread() {
	        		    public void run() {
	        		        try{
	        		            // just doing some long operation
	        		            Thread.sleep(10000);
	        		         } catch (Exception e) {  }
	        		           // handle the exception somehow, or do nothing
	        		         

	        		         // run code on the UI thread
	        		        runOnUiThread(new Runnable() {

	        		            public void run() {
	        		                progressBar.dismiss();
	        		                Intent intent = getIntent();
	       	        		     finish();
	       	        		     startActivity(intent);
	        		            }
	        		        });
	        		    }
	        		     }.start();
	        		     
	        		    
	       // 	progressBar.dismiss();
	        	
	    }
	        //delete internal files during un-installation 
	        public boolean deleteFile (String name){
	            name = "aakash.sh";
	            name = "help_flag";
	            name = "copyFilesFlag.txt";
	            return false;
	           
	        }
	}
	

}