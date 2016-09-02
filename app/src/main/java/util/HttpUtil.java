package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.util.Log;

import com.example.coolweather.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class HttpUtil {
	//现在通过在本地xml文件中查询对应的城市代号

	
	public static void sendHttpRequest(final Context context,final String address,final CodeCallbackListener listener){
		new Thread(new Runnable() {
			boolean flag_province = false;
			String tempid;
			String tempname;
			List<String> idlist = new ArrayList<String>();
			List<String> namelist = new ArrayList<String>();
			@Override
			public void run() {
			//	HttpsURLConnection connection = null;
				try {
			/*		URL url = new URL(address);
					connection = (HttpsURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}*/
					XmlPullParser xmlPullParser = context.getResources().getXml(R.xml.cityidtest);
			//		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			//		XmlPullParser xmlPullParser = factory.newPullParser();
					int eventType = xmlPullParser.getEventType();
					if(address == null){
						while(eventType !=  XmlPullParser.END_DOCUMENT) {
							String nodeName = xmlPullParser.getName();
							switch(eventType) {
								case XmlPullParser.START_TAG : {
									if(nodeName.equals("Province")){
										tempid = xmlPullParser.getAttributeValue(null, "ID");
										tempname = xmlPullParser.getAttributeValue(null, "Name");
										idlist.add(tempid);
										namelist.add(tempname);
									}
									break;
								}
								case XmlPullParser.END_TAG: {
									Log.d("xml","finishing loading" + tempname);
									break;
								}
								default:
									break;

							}
							eventType = xmlPullParser.next();
						}
					} else {
						while(eventType !=  XmlPullParser.END_DOCUMENT) {
							String nodeName = xmlPullParser.getName();
							switch(eventType) {
								case XmlPullParser.START_TAG : {
									if(nodeName.equals("Province") && address.equals(xmlPullParser.getAttributeValue(null, "ID"))){
										flag_province = true;
									} else if(nodeName.equals("City") && flag_province){
										tempid = xmlPullParser.getAttributeValue(null, "ID");
										tempname = xmlPullParser.getAttributeValue(null, "Name");
										idlist.add(tempid);
										namelist.add(tempname);
									}

									break;
								}
								case XmlPullParser.END_TAG: {
									if(flag_province && "Province".equals(nodeName)) {
										flag_province = false;
									}
									Log.d("xml","finishing" + tempname);
									break;
								}
								default:
									break;

							}
							eventType = xmlPullParser.next();
						}
					}
					if(listener != null){
						listener.onFinish(idlist, namelist);
					}
				} catch (Exception e) {
					listener.onError(e);
				}
				
			}
		}).start();
	}
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					Log.d("address",address);
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						//回调onFinish()方法
						Log.d("whether","true");
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					Log.d("exception", e.toString());
					//回调onError()方法
					listener.onError(e);
				}finally {
					if(connection != null){
						connection.disconnect();
					}
				}

			}
		}).start();
	}
}
