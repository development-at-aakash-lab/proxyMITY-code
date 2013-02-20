package com.iitb.proxymity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListSrtFilesActivity extends Activity {
	/** Called when the activity is first created. */
	List<String> files;
	ArrayList<String> filepath = new ArrayList<String>();
	private EditText edittext;
	ListView lv;
	int current ;
	private ArrayList<String> arraylist = new ArrayList<String>();
	private int textlenght = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.srtlist);
		Bundle extras = getIntent().getExtras();
		current = extras.getInt("current");
		lv = (ListView) findViewById(R.id.listView1);
		edittext = (EditText) findViewById(R.id.EditText01);
		files = getListOfFiles("mnt/extsd");
		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.srtrow, R.id.title, files);
		lv.setAdapter(directoryList);
		edittext.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlenght = edittext.getText().length();
				arraylist.clear();
				for (int i = 0; i < files.size(); i++) {
					if (textlenght <= files.get(i).length()) {
						if (edittext
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) files.get(i).subSequence(0,
												textlenght))) {
							arraylist.add(files.get(i));
						}
					}
				}

				lv.setAdapter(new ArrayAdapter<String>(
						ListSrtFilesActivity.this, R.layout.srtrow, R.id.title,
						arraylist));

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
				String ss = tv.getText().toString();
				// Toast.makeText(getApplicationContext(),
				// ss,Toast.LENGTH_SHORT) .show();
				int index = Return_Index(ss);
				Intent in = new Intent();
				in.putExtra("srtpath", filepath.get(index));
				setResult(1, in);
				Log.d("srtpath", filepath.get(index));
				finish();

			}
		});

	}

	private int Return_Index(String ss) {
		// TODO Auto-generated method stub
		int a = 0;
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).equals(ss))
				// a = i;
				return i;

		}
		return a;
	}

	private List<String> getListOfFiles(String path) {

		File files = new File(path);

		FileFilter filter = new FileFilter() {

			private final List<String> exts = Arrays.asList("srt", "txt");

			public boolean accept(File pathname) {
				String ext;
				String path = pathname.getPath();
				ext = path.substring(path.lastIndexOf(".") + 1);
				return exts.contains(ext);
			}
		};

		final File[] filesFound = files.listFiles(filter);
		List<String> list = new ArrayList<String>();
		if (filesFound != null && filesFound.length > 0) {
			for (File file : filesFound) {
				list.add(file.getName());
				filepath.add(file.getPath());
			}
		}

		return list;
	}
	@Override
	public boolean onKeyDown(int keyCode , KeyEvent event)
	{ if (keyCode==KeyEvent.KEYCODE_BACK)
	{  
		Intent in = new Intent();
		in.putExtra("time", current);
		setResult(2, in);
		finish();
		return true;
	}
		return super.onKeyDown(keyCode, event);
	}
}