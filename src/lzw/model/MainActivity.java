package lzw.model;

import java.util.ArrayList;
import java.util.List;

import lzw.EmptyClasses.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	String[] classTime = new String[]{"上午1.2节", "上午3.4节", "下午5.6节", "下午7.8节",
			"晚上9.10节"};
	Button goBn;
	Boolean isClick;
	Handler handler;
	ProgressBar bar;
	//String classUrl="http://211.71.149.118:8080/empty_class/class2.html";
	String classUrl= "http://jwc.bjfu.edu.cn/jscx/143126.html";
	//String classUrl = "http://192.168.42.27/empty_class/class2.html";
	final String classFilePath = "ClassInfo.txt";
	public static final String noClassInfoStr="noClassInfo";
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	ActionBar actionBar;
	View tv=null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	  requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		goBn = (Button) findViewById(R.id.goBn);
		bar = (ProgressBar) findViewById(R.id.bar);
		tv=(View)findViewById(R.id.tryInflate);
		preferences = getSharedPreferences("lzw.model", MODE_PRIVATE);
		editor = preferences.edit();
		editor.putBoolean("noClassInfo", false);
		editor.commit();
		setDefaultClassInfo();
		if(isNeedUpdate()&&!NetworkUtil.isConnect(MainActivity.this)){
			NetworkUtil.promptConnect(MainActivity.this);
		}
		final OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				goBn.setVisibility(View.GONE);
			  bar.setVisibility(View.VISIBLE);
			  tv.setVisibility(View.VISIBLE);
				if(isNeedUpdate()) newThreadGetUrl();
				else handler.sendEmptyMessage(0x123);
			}
		};
		goBn.setOnClickListener(listener);
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					intentToClass(MainActivity.this, ClassInfoActivity.class);
					finish();
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
			writeToDatabase(classes,"2013-10-31");
			editor.putBoolean("isFirstTime",false);
			editor.commit();
		}
	}
	
	void putDateIntoPreferences(String date){
		editor.putString("latestDate", date);
		editor.commit();
	}
	
	void intentToClass(Context ctx, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(ctx, cls);
		startActivity(intent);
	}

	void newThreadGetUrl() {
		new Thread() {
			public void run() {
				getClasses();
				handler.sendEmptyMessage(0x123);
			}
		}.start();
	}
	
	void writeToDatabase(List<String> classes,String date) {
		putDateIntoPreferences(date);
		
		ArrayList<ArrayList<String>> classList = ListUtil.splitList(classes),
				classList1,classList2;
		classList1 = ListUtil.getSubArrayList(classList, 0, 5);
		classList2 = ListUtil.getSubArrayList(classList, 5, 10);
		initDatabase(MainActivity.this,"class1.db3",classList2);
		initDatabase(MainActivity.this,"class2.db3",classList1);
	}
	
	public void initDatabase(Context cxt,String file,ArrayList<ArrayList<String>> classList){
		MyDataBaseHelper dbHelper = new MyDataBaseHelper(cxt,file, 1);
		dbHelper.clearTable();
		for(int i=0;i<5;i++){
			ArrayList<String> classes = classList.get(i);
			for(String classStr : classes){
				dbHelper.updateData(classStr, i, 1);
			}
		}
		dbHelper.close();
	}
	
	boolean isHaveClassInfo(String classMsg){
		List<String>classes=ParseUtil.getClassFromHtml(classMsg);
		return classes!=null &&classes.size()>0;
	}
	
	boolean isNeedUpdate(){
		String latestDate = preferences.getString("latestDate", "");
		String todayDate = TimeUtil.getTodayDate();
		long day = TimeUtil.dateSubtract(todayDate, latestDate);
		return day>0;
	}

	void getClasses() {
		boolean isConnect = NetworkUtil.isConnect(MainActivity.this);
		boolean isHaveNewClass = false;
		if (isConnect) {
			String classMsg = "";
			classMsg = DownUtil.getStringFromUrl(classUrl);
			// classMsg = DownUtil.getStringFromUrl(classLocaleUrl);
			if (isHaveClassInfo(classMsg)) {
				List<String> classes = ParseUtil.getClassFromHtml(classMsg);
				String date = getDateStr(classMsg);
				writeToDatabase(classes, date);
			} else {
				String date;
				date = getDateStr(classMsg);
				putDateIntoPreferences(date);
				editor.putBoolean(noClassInfoStr, true);
				editor.commit();
			}
		}
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
