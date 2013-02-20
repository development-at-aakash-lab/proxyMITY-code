package com.iitb.promitywifi;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Bookmark extends TabActivity {
	EditText inputContent1, inputContent2, inputContent3;
	Button buttonAdd, buttonDeleteAll;
	final int NOTIF_ID = 1234;
	private SQLiteAdapter mySQLiteAdapter;
	ListView listContent;

	SimpleCursorAdapter cursorAdapter;

	Cursor cursor;
	int duration, current;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);
		Bundle extras = getIntent().getExtras();
		duration = extras.getInt("duration");
		current = extras.getInt("current");
		inputContent1 = (EditText) findViewById(R.id.mob);
		inputContent2 = (EditText) findViewById(R.id.email);
		inputContent3 = (EditText) findViewById(R.id.policyname);
		inputContent3.setText(convertMS(current));
		inputContent3.setFocusable(false);
		buttonAdd = (Button) findViewById(R.id.save);
		buttonDeleteAll = (Button) findViewById(R.id.delete);

		listContent = (ListView) findViewById(R.id.restaurants);

		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToWrite();

		cursor = mySQLiteAdapter.queueAll();
		String[] from = new String[] { SQLiteAdapter.KEY_CONTENT1,
				SQLiteAdapter.KEY_CONTENT2, SQLiteAdapter.KEY_CONTENT3 };
		int[] to = new int[] { R.id.txt1, R.id.txt2, R.id.txt3 };
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.bookmarkrow,
				cursor, from, to);
		listContent.setAdapter(cursorAdapter);
		TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");

		spec.setContent(R.id.restaurants);
		spec.setIndicator("My Bookmarks",
				getResources().getDrawable(R.drawable.list));
		getTabHost().addTab(spec);
		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.details);
		spec.setIndicator("New Bookmark",
				getResources().getDrawable(R.drawable.tab2));
		getTabHost().addTab(spec);

		getTabHost().setCurrentTab(0);

		listContent
				.setOnItemLongClickListener(listContentOnItemLongClickListener);
		listContent.setOnItemClickListener(listContentOnItemClickListener);
		buttonAdd.setOnClickListener(buttonAddOnClickListener);
		buttonDeleteAll.setOnClickListener(buttonDeleteAllOnClickListener);

	}

	Button.OnClickListener buttonAddOnClickListener = new Button.OnClickListener() {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String data1 = inputContent1.getText().toString();
			String data2 = inputContent2.getText().toString();
			String data3 = inputContent3.getText().toString();
			if (convertInMili(data3) < duration) {
				mySQLiteAdapter.insert(data1, data2, data3);
				updateList();
				Toast.makeText(getApplicationContext(),
						"Successfully Bookmarked", Toast.LENGTH_LONG).show();
			}

		}

	};

	Button.OnClickListener buttonDeleteAllOnClickListener = new Button.OnClickListener() {

		public void onClick(View arg0) {

			Intent in = new Intent();
			in.putExtra("time", current);
			setResult(2, in);
			finish();
		}

	};
	private ListView.OnItemClickListener listContentOnItemClickListener = new ListView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView tv = (TextView) view.findViewById(R.id.txt3);
			String ss = tv.getText().toString();
			int tt = convertInMili(ss);
			Intent in = new Intent();
			in.putExtra("time", tt);
			setResult(3, in);
			finish();
		}
	};
	private ListView.OnItemLongClickListener listContentOnItemLongClickListener = new ListView.OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			final int item_id = cursor.getInt(cursor
					.getColumnIndex(SQLiteAdapter.KEY_ID));
			String item_content1 = cursor.getString(cursor
					.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
			String item_content2 = cursor.getString(cursor
					.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
			String item_content3 = cursor.getString(cursor
					.getColumnIndex(SQLiteAdapter.KEY_CONTENT3));
			AlertDialog.Builder myDialog = new AlertDialog.Builder(
					Bookmark.this);

			myDialog.setTitle("Delete/Edit?");
			LayoutInflater factory = LayoutInflater.from(Bookmark.this);
			View myview = factory.inflate(R.layout.update, null);
			final EditText dialogC1_id = (EditText) myview
					.findViewById(R.id.mob);
			dialogC1_id.setText(item_content1);

			final EditText dialogC2_id = (EditText) myview
					.findViewById(R.id.email);
			dialogC2_id.setText(item_content2);

			final EditText dialogC3_id = (EditText) myview
					.findViewById(R.id.policyname);
			dialogC3_id.setText(item_content3);
			// dialogC3_id.setFocusable(false);

			myDialog.setView(myview);

			myDialog.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							mySQLiteAdapter.delete_byID(item_id);
							updateList();
						}
					});

			myDialog.setNeutralButton("Update",
					new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							String value1 = dialogC1_id.getText().toString();
							String value2 = dialogC2_id.getText().toString();
							String value3 = dialogC3_id.getText().toString();
							if (convertInMili(value3) < duration) {
								mySQLiteAdapter.update_byID(item_id, value1,
										value2, value3);
								updateList();
								Toast.makeText(getApplicationContext(),
										"Bookmark UpDated ", Toast.LENGTH_LONG)
										.show();
							}
						}
					});

			myDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});

			myDialog.show();
			return true;

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mySQLiteAdapter.close();
	}

	private void updateList() {
		cursor.requery();
	}

	public String convertMS(int ms) {
		int seconds = ((ms / 1000) % 60);
		int minutes = (((ms / 1000) / 60) % 60);
		int hours = ((((ms / 1000) / 60) / 60) % 24);

		String sec, min, hrs;
		if (seconds < 10)
			sec = "0" + seconds;
		else
			sec = "" + seconds;
		if (minutes < 10)
			min = "0" + minutes;
		else
			min = "" + minutes;
		if (hours < 10)
			hrs = "0" + hours;
		else
			hrs = "" + hours;
		return hrs + ":" + min + ":" + sec;

	}

	public int convertInMili(String string) {
		// TODO Auto-generated method stub
		String sub1 = string.substring(0, 2);
		String sub2 = string.substring(3, 5);
		String sub3 = string.substring(6);
		int a1 = Integer.parseInt(sub1);
		int a2 = Integer.parseInt(sub2);
		int a3 = Integer.parseInt(sub3);

		return (a1 * 3600 + a2 * 60 + a3 * 1) * 1000;
	}
}