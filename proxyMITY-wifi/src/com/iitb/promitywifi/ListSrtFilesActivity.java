package com.iitb.promitywifi;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.ivy.util.url.ApacheURLLister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListSrtFilesActivity extends Activity {
	/** Called when the activity is first created. */
	List<URL> list;
	ArrayList<String> filepath = new ArrayList<String>();
	private EditText edittext;
	String ip_address ;
	String srtdirectorypath ;
	URL url1;
	ListView lv;
	private ArrayList<String> arraylist = new ArrayList<String>();
	ArrayList<String> files = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.srtlist);
		Bundle extras = getIntent().getExtras();
		ip_address = extras.getString("ip_address");
		lv = (ListView) findViewById(R.id.listView1);
		edittext = (EditText) findViewById(R.id.EditText01);
		srtdirectorypath = "http://"+ip_address+"/subtitles/";
		System.out.println(srtdirectorypath);
		try {
			url1 = new URL(srtdirectorypath);
			ApacheURLLister lister1 = new ApacheURLLister();
			list = lister1.listFiles(url1);
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			String dg = list.get(i).toString();
			if (dg.contains(".srt"))
			{
				String dg1 = dg.substring(srtdirectorypath.length(),dg.length());
				String dg2 = dg1.replaceAll("%20", " ");
				files.add(dg2);
			}
		}
		System.out.println(files);
		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,R.layout.srtrow, R.id.title, files);
		lv.setAdapter(directoryList);
		edittext.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				arraylist.clear();
				for (int i = 0; i < files.size(); i++) {
					if ((files.get(i).toString().toLowerCase()).contains(edittext.getText().toString().toLowerCase())) {
						arraylist.add(files.get(i));
					}
				}

				lv.setAdapter(new ArrayAdapter<String>(ListSrtFilesActivity.this,R.layout.srtrow, R.id.title, arraylist));

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			public void afterTextChanged(Editable s) {
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.title);
				String dg = tv.getText().toString();
				String ss = srtdirectorypath+dg;
				System.out.println(ss);
				Intent in = new Intent();
				in.putExtra("srtpath", ss);
				setResult(1, in);
				finish();

			}
		});

	}

	
}
