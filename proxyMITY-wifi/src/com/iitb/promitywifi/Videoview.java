package com.iitb.promitywifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
	private String xmlPath = "http://10.105.14.224/bunny.xml";
	private String xmlName;
	private String srtPath = null;
	String stringXmltoxml;
	TextView translation;
	String ipaddress;
	long t2 = 0, r = 0, t1 = 0;;
	String txtdisplay = "";
	Thread thread;
	boolean stop = false;
	int index = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.videoview);
		slideButton = (Button) findViewById(R.id.slideButton);
		slideButton.setBackgroundResource(R.drawable.openarrow);
		Intent bgIntent = getIntent();
		SrcPath = bgIntent.getStringExtra("videofilepath");
		// Toast.makeText(VideoView.this, SrcPath, Toast.LENGTH_LONG).show();
		xmlName = bgIntent.getStringExtra("xmlname");
		ipaddress = bgIntent.getStringExtra("ipaddress");
		xmlPath = "http://" + ipaddress + "/xml/" + xmlName + ".xml";
		
		// Toast.makeText(VideoView.this, xmlPath, Toast.LENGTH_LONG).show();
		myVideoView = (NewVideoView) findViewById(R.id.videoView1);
		TextView courseNameContent = (TextView) findViewById(R.id.textView1);
		TextView courseNameContentFromXml = (TextView) findViewById(R.id.textView2);
		TextView speakerNameContent = (TextView) findViewById(R.id.textView3);
		TextView speakerNameContentFromXml = (TextView) findViewById(R.id.textView4);
		courseNameContent.setText("Course Name :");
		speakerNameContent.setText("Speaker :");
		if (exists(xmlPath)) {
			try {
				
				stringXmltoxml = convertXMLFileToString();
				System.out.println("dddddddddddddddddddddddd");
				String stringXmlContent = getEventsFromAnXML(this);
				courseNameContentFromXml.setText((coursename.toString())
						.substring(1, (coursename.toString()).length() - 1));
				speakerNameContentFromXml.setText((speaker.toString())
						.substring(1, (speaker.toString()).length() - 1));
				epView = (ExpandableListView) findViewById(R.id.expandableListView1);
				
				mAdapter = new MyExpandableListAdapter();
				System.out.println("gggggggggggggggggggggggggg");
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
					"Xml  Not Exists , But still you can view lecture",
					Toast.LENGTH_SHORT).show();
		}
		myVideoView.setMediaController(new MediaController(this));
		myVideoView.requestFocus();
		myVideoView.setVideoPath(SrcPath);
		myVideoView.start();
		slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);

		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				slideButton.setBackgroundResource(R.drawable.closearrow);
				slideButton.setVisibility(View.VISIBLE);
			}
		});

		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				slideButton.setBackgroundResource(R.drawable.openarrow);
				slideButton.setVisibility(View.VISIBLE);

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
			//System.out.println("dddddddddddddddddddddddd");
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
		//ArrayList<String> linkattribute = new ArrayList<String>();
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
					slidename.add(xpp.getText());
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
		//System.out.println("content of array attribute list" + linkattribute);
		//System.out.println("content of array attribute val list"
		//		+ linkattributeval);
		//System.out.println("content of array url" + linkurl);

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

		/*System.out.println(linkattribute.get(0) + " " + linkattributeval.get(0)
				+ " " + linkurl.get(0));*/

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
		InputStream is = new URL(xmlPath).openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = in.readLine()) != null) {
			sb.append(line.trim());
		}
		in.close();

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
		switch (item.getItemId()) {

		case R.id.srt:
			Intent i = new Intent(Videoview.this, ListSrtFilesActivity.class);
			i.putExtra("ip_address", ipaddress);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OPEN_SRT_ACTIVITY) {
			if (resultCode == 1) {
				srtPath = data.getStringExtra("srtpath");
				Toast.makeText(getApplicationContext(), srtPath,Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_CANCELED) {
				Log.d("proxymity", "result cancel");
			}
		}
		if (requestCode == OPEN_BOOKMARK_ACTIVITY) {
			if (resultCode == 2) {
				int pos = data.getIntExtra("time", requestCode);
				myVideoView.seekTo(pos);
				myVideoView.start();
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

	
}