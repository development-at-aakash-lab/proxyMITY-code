package com.iitb.proxymity;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListActivity extends Activity {
	// Initializations and declarations of objects and variables
	private Cursor videocursor;
	ListView videolist;
	int count;
	String folder_path,main_path;
	  List<String> folder;                            ///newwwwww
	  int i = 0;										///newwwwww
	ContentResolver cr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videolistactivity);
		File list=new File("/mnt/extsd/proxyMITY/video/");
		if (list.exists()||new File("/mnt/sdcard/proxyMITY/video/").exists())
		
		{
			if (list.exists())
			{	folder = getListOfFiles("/mnt/extsd/proxyMITY/video/");
			 System.out.println("folder"+folder);
			 folder_path="/mnt/extsd/proxyMITY/video/";
			 main_path="/mnt/extsd/proxyMITY/";
			}
			
			else 
			{
					
				 folder = getListOfFiles("/mnt/sdcard/proxyMITY/video/");
				 System.out.println("folder"+folder);
				 folder_path="/mnt/sdcard/proxyMITY/video/";
				 main_path="/mnt/sdcard/proxyMITY/";
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
		
		else{
			
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
		
		
		 
	}
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

	

	

}