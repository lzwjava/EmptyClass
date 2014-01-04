package lzw.model;

import java.util.ArrayList;
import java.util.List;

import lzw.EmptyClasses.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	String[] classTime = new String[]{"上午1.2节", "上午3.4节", "下午5.6节", "下午7.8节",
			"晚上9.10节"};
	Button building1Bn, building2Bn;
	Boolean isClick;
	Handler handler;
	ProgressBar bar;
	final String classUrl = "http://jwc.bjfu.edu.cn/jscx/143126.html";
	final String classLocaleUrl = "http://192.168.42.63:8080/empty_class/class2.html";
	final String classFilePath = "ClassInfo.txt";
	ArrayList<String> classList1, classList2;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	Boolean noClassInfo=false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		building2Bn = (Button) findViewById(R.id.building2Bn);
		building1Bn = (Button) findViewById(R.id.building1Bn);
		bar = (ProgressBar) findViewById(R.id.bar);
		preferences = getSharedPreferences("lzw.model", MODE_PRIVATE);
		editor = preferences.edit();
		//Log.i("lzw", "hello");
		setDefaultClassInfo();
		if(!NetworkUtil.isConnect(MainActivity.this))
			NetworkUtil.promptConnect(MainActivity.this);
		final OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> objClassList = null;
				int whichClass=0;
				if (v.getId() == R.id.building1Bn) {
					objClassList = classList2;
					whichClass=1;
				} else if (v.getId() == R.id.building2Bn) {
					objClassList = classList1;
					whichClass=2;
				}
				Bundle bundle = new Bundle();
				bundle.putInt("WhichClass",whichClass);
				bundle.putStringArrayList("class", objClassList);
				bundle.putBoolean("noClassInfo", noClassInfo);
				intentToClass(MainActivity.this, ClassInfoActivity.class, bundle);
			}
		};
		newThreadGetUrl();
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					building1Bn.setOnClickListener(listener);
					building2Bn.setOnClickListener(listener);
					building1Bn.setVisibility(View.VISIBLE);
					building2Bn.setVisibility(View.VISIBLE);
					bar.setVisibility(View.GONE);
				}
			}
		};
	}

	void setDefaultClassInfo(){
		boolean isFirstTime=preferences.getBoolean("isFirstTime",true);
		String classMsg="";
		List<String>classes;
		if(isFirstTime){
			isFirstTime=false;
			classMsg = FileUtil.getStrFromFile(MainActivity.this, R.raw.class1);
			classes = ParseUtil.getNumFromStr(classMsg);
			putDateIntoPreferences("2013-10-31");
			FileUtil.write(MainActivity.this, classFilePath, classes.toString(),MODE_PRIVATE);
			editor.putBoolean("isFirstTime",false);
			editor.commit();
		}
	}
	
	void putDateIntoPreferences(String date){
		editor.putString("latestDate", date);
		editor.commit();
	}
	
	void intentToClass(Context ctx, Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtras(bundle);
		intent.setClass(ctx, cls);
		startActivity(intent);
	}

	void newThreadGetUrl() {
		new Thread() {
			public void run() {
				// String classMsg = DownUtil.getStringFromUrl(classUrl);
				List<String> classes = getClasses();
				ArrayList<String> classList = ListUtil.splitList(classes);
				classList1 = ListUtil.getSubList(classList, 0, 5);
				classList2 = ListUtil.getSubList(classList, 5, 10);
				handler.sendEmptyMessage(0x123);
			}
		}.start();
	}
	
	boolean isHaveClassInfo(String classMsg){
		List<String>classes=ParseUtil.getClassFromHtml(classMsg);
		return classes!=null &&classes.size()>0;
	}
	
	List<String> getClasses() {
		String classMsg;
		List<String> classes;
		// boolean isDownloadFile = preferences.getBoolean("isDownloadFile", false);
		classMsg = FileUtil.read(MainActivity.this, classFilePath);
		classes = ParseUtil.getNumFromStr(classMsg);
		String latestDate = preferences.getString("latestDate","");
		String todayDate = TimeUtil.getTodayDate();
		long day = TimeUtil.dateSubtract(todayDate, latestDate);
		//Log.v("lzw",latestDate);
		if (day > 0) {
			boolean isConnect = NetworkUtil.isConnect(MainActivity.this);
			boolean isHaveNewClass = false;
			if (isConnect) {
				classMsg = DownUtil.getStringFromUrl(classUrl);
				//classMsg = DownUtil.getStringFromUrl(classLocaleUrl);
				if (isHaveClassInfo(classMsg)) {
					isHaveNewClass = true;
					Log.v("lzw","else");
				}else {
					String date = getDateStr(classMsg);
				  putDateIntoPreferences(date);
					noClassInfo=true;
				}
			}
			if (isHaveNewClass) {
				classes = ParseUtil.getClassFromHtml(classMsg);
				FileUtil.write(MainActivity.this, classFilePath, classes.toString(),
						MODE_PRIVATE);
				String date = getDateStr(classMsg);
				putDateIntoPreferences(date);
				return classes;
			}
		}
		return classes;
	}

	String getDateStr(String originStr) {
		Document doc=Jsoup.parse(originStr,"gb2312");
		Element cont=doc.select("div#con_djl").first();
		String str=cont.text();
		List<String> nums = ParseUtil.getNumFromStr(str, 1, 4);
		String y,m,d;
		y=nums.get(0);
		m=nums.get(1);
		d=nums.get(2);
		String date=y + "-" + m + "-" + d;
		return TimeUtil.addDateStr(date, 1);
	}
}
