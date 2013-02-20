package com.iitb.proxymity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.MediaController;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public class Videoview extends Activity implements OnClickListener {
	public static final int OPEN_SRT_ACTIVITY = 1;

	public static final int OPEN_BOOKMARK_ACTIVITY = 2;
	public static final int OPEN_AUDIO_ACTIVITY = 3;
	Button slideButton;
	SlidingDrawer slidingDrawer;
	ExpandableListAdapter mAdapter;
	ExpandableListView epView;
	NewVideoView myVideoView;
	String SrcPath = null;
	ArrayList<String> groups = new ArrayList<String>();
	ArrayList<String> slidename = new ArrayList<String>();
	ArrayList<String> themeattributeval = new ArrayList<String>();
	ArrayList<String> starttime = new ArrayList<String>();
	ArrayList<String> coursename = new ArrayList<String>();
	ArrayList<String> speaker = new ArrayList<String>();
	List<Integer> myCoords = new ArrayList<Integer>();
	ArrayList<String> contact = new ArrayList<String>();
	ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> seektime = new ArrayList<ArrayList<String>>();
	private String xmlPath;
	private String mainPath;
	private String xmlName;
	private String srtPath = null;
	String stringXmltoxml;
	File file;
	TextView translation;
	ArrayList<StructureOfList> srtContent = new ArrayList<StructureOfList>();
	String txt = "";
	Thread thread;
	boolean stop = false;
	int index = 0;
	MediaPlayer audio = new MediaPlayer();
	String audioPath = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.videoview);
		slideButton = (Button) findViewById(R.id.slideButton);
		slideButton.setBackgroundResource(R.drawable.openarrow);
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		SrcPath = extras.getString("videofilename");
		xmlName = extras.getString("xmlname");
		mainPath = extras.getString("mainpath");
		File extStore = Environment.getExternalStorageDirectory();
		System.out.println("path"+extStore);
		
		xmlPath = mainPath+"xml/" + xmlName + ".xml";
		myVideoView = (NewVideoView) findViewById(R.id.videoView1);
		translation = (TextView) findViewById(R.id.translation);
		TextView courseNameContent = (TextView) findViewById(R.id.textView1);
		TextView courseNameContentFromXml = (TextView) findViewById(R.id.textView2);
		TextView speakerNameContent = (TextView) findViewById(R.id.textView3);
		TextView speakerNameContentFromXml = (TextView) findViewById(R.id.textView4);
		courseNameContent.setText("Course Name :");
		speakerNameContent.setText("Speaker :");
		if (new File(xmlPath).exists()) {
			try {

				stringXmltoxml = convertXMLFileToString();
				String stringXmlContent = getEventsFromAnXML(this);
				courseNameContentFromXml.setText((coursename.toString())
						.substring(1, (coursename.toString()).length() - 1));
				speakerNameContentFromXml.setText((speaker.toString())
						.substring(1, (speaker.toString()).length() - 1));
				epView = (ExpandableListView) findViewById(R.id.expandableListView1);
				mAdapter = new MyExpandableListAdapter();
				epView.setAdapter(mAdapter);
				epView.expandGroup(0);
				epView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
					public boolean onGroupClick(ExpandableListView arg0,
							View arg1, int groupPosition, long arg3) {
						if (groupPosition == 5) {

						}
						return false;
					}
				});

				epView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						int pos = convertInMili(seektime.get(groupPosition)
								.get(childPosition));
						myVideoView.seekTo(pos);
						myVideoView.start();
						slidingDrawer.close();
						return false;
					}

				});
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				System.out.println("helooo"+e);
			e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
					Toast.makeText(getApplicationContext(),
					"One Continuous  Topic so theme/slide navigation is not needed ",
					Toast.LENGTH_SHORT).show();
		}
		myVideoView.setMediaController(new MediaController(this) {

			public void show() {
				if (srtPath != null) {
					stop = true;
				}
				super.show();

			}

			public void hide() {
				super.hide();
				if (srtPath != null) {
					stop = false;
					// System.out.println("I am on line 167"+srtPath);
					if (srtContent.size() == 0) {
						new LongOperation().execute("");
					}
					Runnable runnable = new ReadFile();
					thread = new Thread(runnable);
					thread.start();
					// System.out.println("I am on line 174"+srtPath);
				}
			}
		});
		myVideoView.requestFocus();
		myVideoView.setVideoPath(SrcPath);
		myVideoView.start();
		slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);

		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				slideButton.setBackgroundResource(R.drawable.closearrow);
			}
		});

		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				slideButton.setBackgroundResource(R.drawable.openarrow);

			}
		});
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

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		public Object getChild(int groupPosition, int childPosition) {
			return children.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			int i = 0;
			try {
				i = children.get(groupPosition).size();

			} catch (Exception e) {
			}

			return i;
		}

		public TextView getchildGenericView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			TextView textView = new TextView(Videoview.this);
			textView.setLayoutParams(lp);

			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTypeface(null, Typeface.NORMAL);
			textView.setTextSize(16);
			textView.setPadding(36, 10, 5, 5);
			return textView;
		}

		public TextView getgroupGenericView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			TextView textView = new TextView(Videoview.this);
			textView.setLayoutParams(lp);

			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTypeface(null, Typeface.BOLD);
			textView.setTextSize(18);
			textView.setPadding(55, 10, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getchildGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		public int getGroupCount() {
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getgroupGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		stop = true;
		index = 0;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private String getEventsFromAnXML(Activity activity)
			throws XmlPullParserException, IOException {
		ArrayList<String> a = new ArrayList<String>();
	//	ArrayList<String> linkattribute = new ArrayList<String>();
		//ArrayList<String> linkattributeval = new ArrayList<String>();
		ArrayList<String> themeattribute = new ArrayList<String>();
		//ArrayList<String> linkurl = new ArrayList<String>();
		ArrayList<String> endtime = new ArrayList<String>();
		ArrayList<String> videoname = new ArrayList<String>();
		ArrayList<String> present = new ArrayList<String>();
		String test = null;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setValidating(false);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(stringXmltoxml));
		xpp.nextToken();
		int eventType = xpp.getEventType();

		int attributecount = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				a.add("--- Start XML ---");
			} else if (eventType == XmlPullParser.START_TAG) {
				a.add(xpp.getName());

				test = a.get(a.size() - 1);

				attributecount = xpp.getAttributeCount();
				if (attributecount != 0) {
					for (int i = 0; i < attributecount; i++) {
						a.add(xpp.getAttributeName(i));
						a.add(xpp.getAttributeValue(i));

						/*if ((test.equalsIgnoreCase("Link"))) {
							linkattribute.add(xpp.getAttributeName(i));
							linkattributeval.add(xpp.getAttributeValue(i));
						}*/

						if ((test.equalsIgnoreCase("Theme"))) {
							themeattribute.add(xpp.getAttributeName(i));
							themeattributeval.add(xpp.getAttributeValue(i));
						}

					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				a.add(xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				a.add(xpp.getText());

				/*if ((test.equalsIgnoreCase("linkurl"))) {
					linkurl.add(xpp.getText());
				}*/

				if ((test.equalsIgnoreCase("slidename"))) {
					String dg1 = xpp.getText();
					/*int j = dg.lastIndexOf('.');
					String dg1 = dg.substring(0, j);*/
					slidename.add(dg1);
				}

				if ((test.equalsIgnoreCase("starttime"))) {
					starttime.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("endtime"))) {
					endtime.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("videoname"))) {
					videoname.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("coursename"))) {
					coursename.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("presentation"))) {
					present.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("speaker"))) {
					speaker.add(xpp.getText());
				}

				if ((test.equalsIgnoreCase("contact"))) {
					contact.add(xpp.getText());
				}

			}

			eventType = xpp.next();
		}
		a.add("\n--- End XML ---");
		// System.out.println("content of array list" + a);
		
		System.out.println("content of array theme attribute" + themeattribute);
		System.out.println("content of array theme attribute val"
				+ themeattributeval);
		System.out.println("content of array  slidename" + slidename);
		System.out.println("content of array  starttime" + starttime);
		System.out.println("content of array endtime " + endtime);

		System.out.println("content of array videoname " + videoname);
		System.out.println("content of array coursename " + coursename);
		System.out.println("content of array presentation " + present);
		System.out.println("content of array speaker " + speaker);
		System.out.println("content of array contact " + contact);

	

		int num = UniqueValues();
		System.out.println(num);
		for (int j = 0, k = 0; j < themeattributeval.size(); j++) {
			if (!containsValue(groups, themeattributeval.get(j))) {
				groups.add(k++, themeattributeval.get(j));
			}
		}
		System.out.println(groups.size());
		for (int i = 0; i < groups.size(); i++) {
			System.out.println(groups.get(i));
		}
		int count = 1;
		for (int i = 0; i < themeattributeval.size() - 1; i++) {
			if (themeattributeval.get(i).equals(themeattributeval.get(i + 1))) {
				count++;
			} else {
				myCoords.add(count);
				count = 1;

			}
		}
		myCoords.add(count);
		int k = 0;
		for (int i = 0; i < groups.size(); i++) {
			ArrayList<String> row = new ArrayList<String>();
			ArrayList<String> row1 = new ArrayList<String>();
			for (int j = 0; j < myCoords.get(i); j++) {
				row.add(slidename.get(k));
				row1.add(starttime.get(k));
				k++;
			}

			children.add(row);
			seektime.add(row1);
		}
		System.out.println("children array " + children);
		return a.toString();

	}

	private int UniqueValues() {
		ArrayList<String> values = new ArrayList<String>();
		int count = 0;
		for (int j = 0; j < themeattributeval.size(); j++) {
			if (!containsValue(values, themeattributeval.get(j)))
				values.add(count++, themeattributeval.get(j));
		}
		return count;
	}

	private static boolean containsValue(ArrayList<String> array, String target) {
		for (int j = 0; j < array.size(); j++) {
			if (array.get(j) != null && array.get(j).equals(target))
				return true;
		}
		return false;
	}

	public String convertXMLFileToString() throws IOException {

		BufferedReader br = new BufferedReader(
				new FileReader(new File(xmlPath)));
		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = br.readLine()) != null) {
			sb.append(line.trim());
		}
		br.close();

		return sb.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		stop = true;
		translation.setText("");
		index = 0;
		switch (item.getItemId()) {

		case R.id.srt:
			int current1 = myVideoView.getCurrentPosition();
			Intent i = new Intent(Videoview.this, ListSrtFilesActivity.class);
			i.putExtra("current", current1);
			startActivityForResult(i, OPEN_SRT_ACTIVITY);
			return true;

		case R.id.bookmark:
			Intent i1 = new Intent(Videoview.this, Bookmark.class);
			int duration = myVideoView.getDuration();
			int current = myVideoView.getCurrentPosition();
			i1.putExtra("duration", duration);
			i1.putExtra("current", current);
			startActivityForResult(i1, OPEN_BOOKMARK_ACTIVITY);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// perform long running operation operation
			String str = "", txt = "";
			int i, j = 0;
			int ti, tf, sleep;
			boolean flag = false;
			File file = new File(srtPath);
			RandomAccessFile br = null;
			StructureOfList a;
			if (!file.exists()) {
				System.out.println("File does not exist.");
				System.exit(0);
			}
			try {
				br = new RandomAccessFile(file, "r");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("i am line 707");
			if ((audioPath != "") && (audioPath != null)) {
				// myVideoView.mutePlayBack();
				audio.seekTo(myVideoView.getCurrentPosition());
				audio.start();

			}
			try {
				while ((str = br.readLine()) != null) {
					if (str == "") {
						while (((str = br.readLine()) != null) && ((str == ""))) {

						}
					}
					if (str != null) {
						i = Integer.parseInt(str);
						if ((str = br.readLine()) != null) {
							int h = Integer.parseInt(str.substring(0, 2));
							int m = Integer.parseInt(str.substring(3, 5));
							int s = Integer.parseInt(str.substring(6, 8));
							int ms = Integer.parseInt(str.substring(9, 12));
							ti = (((((h * 60) + m) * 60) + s) * 1000) + ms;

							int l = str.indexOf("-->") + 4;
							h = Integer.parseInt(str.substring(l + 0, l + 2));
							m = Integer.parseInt(str.substring(l + 3, l + 5));
							s = Integer.parseInt(str.substring(l + 6, l + 8));
							ms = Integer.parseInt(str.substring(l + 9, l + 12));
							tf = (((((h * 60) + m) * 60) + s) * 1000) + ms;

							while (((str = br.readLine()) != null)
									&& (str != "")) {
								txt = txt + " " + str;
								flag = true;

							}

							int st = -1, end = -1;

							while (((st = txt.indexOf("<")) >= 0)
									&& ((end = txt.indexOf(">")) >= 0)
									&& (flag)) {
								txt = txt.substring(0, st)
										+ txt.substring(end + 1, txt.length());
							}
							flag = false;
							sleep = tf - ti;
							a = new StructureOfList(ti, sleep, tf, i, txt);
							srtContent.add(j, a);
							j++;
							txt = "";

						}
					}

				}
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Void... values) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}

	public class StructureOfList {
		String txt;
		int i;
		int ti, tf, sleep;

		public StructureOfList(int timeStart, int DurationOfSleep, int timeEnd,
				int number, String text) {
			txt = text;
			i = number;
			ti = timeStart;
			tf = timeEnd;
			sleep = DurationOfSleep;
		}
	}

	public class ReadFile implements Runnable {

		public ReadFile() {
			// TODO Auto-generated constructor stub
		}

		public void run() {
			// Get the object of DataInputStream
			while (!myVideoView.isPlaying()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// myVideoView.mutePlayBack();
			// int duration=myVideoView.getDuration();
			while ((!Thread.currentThread().isInterrupted()) && (!stop)
					&& (myVideoView.isPlaying())) {
				try {
					doWork();
					if ((audioPath != "") && (audioPath != null)) {
						// myVideoView.mutePlayBack();
						audio.seekTo(myVideoView.getCurrentPosition());
					}
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("this thread has stopped");

		}
	}

	public void doWork() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (!stop) {
					long playTime = myVideoView.getCurrentPosition();
					if ((index < srtContent.size())
							&& (playTime > srtContent.get(index).tf)) {
						// srtContent.get(index).;
						while ((index < srtContent.size())
								&& (playTime > srtContent.get(index).ti)) {
							index++;
						}
					} else if ((index > 0)
							&& (playTime < srtContent.get(index).ti)) {
						while ((index > 0)
								&& (playTime < srtContent.get(index).ti)) {
							index--;
						}

					}
					if ((playTime >= srtContent.get(index).ti)
							&& (playTime <= srtContent.get(index).tf)) {
						translation.setText(srtContent.get(index).txt);
					} else {
						translation.setText("");
					}

				}
			}
		});
	}

	public class CheckAndPlay implements Runnable {
		public CheckAndPlay() {
			// TODO Auto-generated constructor stub;
			/*
			 * if((audioPath != "") && (audioPath != null)){ try {
			 * System.out.println("hillo"+audioPath);
			 * audio.setDataSource(audioPath); audio.prepare(); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalStateException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } }
			 */
			myVideoView.start();
			stop = false;
		}

		public CheckAndPlay(int p) {
			// TODO Auto-generated constructor stub
			/*
			 * if((audioPath != "") && (audioPath != null)){ try {
			 * audio.setDataSource(audioPath); audio.prepare(); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalStateException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace() ; } }
			 */
			myVideoView.seekTo(p);
			myVideoView.start();
			stop = false;
		}

		public void run() {
			/*
			 * while ((!myVideoView.isPlaying())&&(!stop)) { try {
			 * Thread.currentThread(); Thread.sleep(10); } catch
			 * (InterruptedException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } } if((audioPath != "") && (audioPath !=
			 * null)){ //myVideoView.mutePlayBack();
			 * audio.seekTo(myVideoView.getCurrentPosition()); audio.start();
			 * 
			 * }
			 */

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OPEN_SRT_ACTIVITY) {
			if (resultCode == 1) {
				srtPath = data.getStringExtra("srtpath");
				if ((srtPath != "") && (srtPath != null)) {
					if (srtContent.size() != 0) {
						srtContent.clear();
					}
					new LongOperation().execute("");
					Runnable runnable = new ReadFile();
					thread = new Thread(runnable);
					thread.start();
				}
				Runnable runnable = new CheckAndPlay();
				Thread thread_checkAndPlay = new Thread(runnable);
				thread_checkAndPlay.start();
			}
			if (resultCode == 2) {
				int pos = data.getIntExtra("time", requestCode);
				myVideoView.seekTo(pos);
				myVideoView.start();
			}else if (resultCode == RESULT_CANCELED) {
				Log.d("proxymity", "result cancel");
			}
		}
		if (requestCode == OPEN_BOOKMARK_ACTIVITY) {
			if (resultCode == 2) {
				int pos = data.getIntExtra("time", requestCode);
				if ((srtPath != "") && (srtPath != null)) {
					if (srtContent.size() != 0) {
						srtContent.clear();
					}
					new LongOperation().execute("");
					Runnable runnable = new ReadFile();
					thread = new Thread(runnable);
					thread.start();
				}
				Runnable runnable = new CheckAndPlay(pos);
				Thread thread_checkAndPlay = new Thread(runnable);
				thread_checkAndPlay.start();
			}
			if (resultCode == 3) {
				int pos = data.getIntExtra("time", requestCode);
				myVideoView.seekTo(pos);
				myVideoView.start();
			} else if (resultCode == RESULT_CANCELED) {
				Log.d("proxymity", "result cancel");
			}
		}
	}
}