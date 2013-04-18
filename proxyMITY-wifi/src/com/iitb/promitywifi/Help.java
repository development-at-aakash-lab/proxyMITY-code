package com.iitb.promitywifi;



import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Help extends Activity {
	WebView helppage;
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        helppage = (WebView) findViewById(R.id.helpwebView);
        helppage.getSettings().setJavaScriptEnabled(true);
        //helppage.loadUrl("file:///android_asset/help.html");
        
if (new File("/mnt/extsd/Instructions/proxyMITY_wifi_help.html").exists())
    		
		{    
	  helppage.loadUrl("file:\\mnt\\extsd\\Instructions\\proxyMITY_wifi_help.html");
		}
        
        else if(new File("/mnt/sdcard/Instructions/proxyMITY_wifi_help.html").exists())
        {
        	  helppage.loadUrl("file:\\mnt\\sdcard\\Instructions\\proxyMITY_wifi_help.html");
        }
        
        else if(new File ("/mnt/external_sd/Instructions/proxyMITY_wifi_help.html").exists())
        {
      	  helppage.loadUrl("file:\\mnt\\external_sd\\Instructions\\proxyMITY_wifi_help.html");
        }
        else
        {
        	helppage.loadUrl("file:///android_asset/proxyMITY_wifi_help.html");	
        }    
        
    }

   

    
}
