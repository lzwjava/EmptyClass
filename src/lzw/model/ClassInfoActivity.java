package lzw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lzw.EmptyClasses.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ClassInfoActivity extends Activity {
	ListView classView;
	String[] classTime = new String[]{"上午第一节", "上午第二节", "下午第一节", "下午第二节",
			"晚上"};
	String[] classSet1,classSet2;
	String[][] classSubSet1=new String[6][],
			classSubSet2=new String[6][];
	HashMap<String,String> map1,map2;
	Spinner spinner,class_spinner;
	ArrayAdapter<String> adapter;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	TextView titleView;
	ActionBar actionBar;
	Boolean noClassInfo;
	int curTimeIndex;
	int curClass;
	MyDataBaseHelper dbHelper1,dbHelper2;
	String origTitle="";
	Menu classMenu=null;
	int[] ids=new int[]{R.id.morning1,R.id.morning2,R.id.afternoon1,R.id.afternoon2,
				R.id.evening,R.id.chooseAll,R.id.cancelAll};
	Handler handler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_info);
		
		preferences=getSharedPreferences("lzw.model", MODE_PRIVATE);
		editor=preferences.edit();
		String date=preferences.getString("latestDate",null);
		noClassInfo=preferences.getBoolean(MainActivity.noClassInfoStr,true);
		initTitle(date);
		
		classView = (ListView) findViewById(R.id.classView);
		adapter = new ArrayAdapter<String>(ClassInfoActivity.this,
				android.R.layout.simple_list_item_1);
		
		curTimeIndex=TimeUtil.getCurTimeIndex();
		setActionBarTitle(curClass);
		
		adapter.notifyDataSetChanged();
		dbHelper1=new MyDataBaseHelper(this, "class1.db3", 1);
		dbHelper2=new MyDataBaseHelper(this, "class2.db3", 1);
		
		handler=new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==0x111){
					String state=preferences.getString("state", "111111");
					Log.i("lzw",state);
					for (int i = 0; i < 5; i++) {
						char ch=state.charAt(i);
						if(ch=='1'){
							setItemState(i, true);
						}
					}
					char ch=state.charAt(5);
					int id;
					if (ch=='0') {
						curClass=0;
						id=R.id.building1;
					} else{
						id=R.id.building2;
						curClass=1;
					}
					onOptionsItemSelected(classMenu.findItem(id));
				}
			}
		};
	}

	void testDataBase(MyDataBaseHelper dbHelper){
		SQLiteDatabase db=dbHelper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from class_info",null);
		ArrayList<Map<String,String>> list=cursorToList(cursor);
		SimpleAdapter adapterS=new SimpleAdapter(ClassInfoActivity.this, list, 
				R.layout.line, new String[]{"cls"}, new int[]{R.id.cls});
		classView.setAdapter(adapterS);
	}
	
	ArrayList<Map<String,String>> cursorToList(Cursor cursor){
		ArrayList<Map<String, String>> ans = new ArrayList<Map<String,String>>();
		while(cursor.moveToNext()){
			HashMap<String, String> map = new HashMap<String,String>();
			map.put("cls",cursor.getString(1));
			String time="";
			for(int i=0;i<5;i++){
				String num=cursor.getString(i+2);
				if(num!=null) time+=i;
			}
			map.put("time",FormatUtil.numToClassTime(time));
			ans.add(map);
		}
		return ans;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflator=new MenuInflater(this);
		classMenu=menu;
		inflator.inflate(R.menu.class_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	void setAllItem(boolean flag){
		for(int i=0;i<5;i++){
			setItemState(i, flag);
		}
	}
	
	void setItemState(int i,boolean flag){
		MenuItem item = classMenu.findItem(ids[i]);
		item.setChecked(flag);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem mi){
		int mid=mi.getItemId();
		if(mid==R.id.chooseAll){
			setAllItem(true);
		}else if(mid==R.id.cancelAll){
			setAllItem(false);
		}else{
			if(mi.isCheckable()){
				if(mi.isChecked()){
					 mi.setChecked(false);
				}else mi.setChecked(true);
			}
		}
		if(mid==R.id.goWebsite)
			goWebsite(null);
		if(noClassInfo){
			updateListViewWhenHoliday();
			return true;
		}
		if(mid==R.id.building1){
			curClass=0;
		}else if(mid==R.id.building2){
			curClass=1;
		}
		MyDataBaseHelper dbHelper=null;
		if(curClass==0){
			dbHelper=dbHelper1;
		}else dbHelper=dbHelper2;
		String clause="select * from class_info";
		boolean first=true;
		
		for(int i=0;i<5;i++){
			int id=ids[i];
			MenuItem item=(MenuItem)classMenu.findItem(id);
			if(item.isChecked()){
				if(!first) clause+=" and";
				else{
					clause+=" where";
					first=false;
				}
				clause+=" t"+i+"=1";
			}
		}
		clause+=" order by cls";
		Cursor cursor=dbHelper.getReadableDatabase().rawQuery(clause,null);
		List<Map<String,String>> list=(List<Map<String,String>>)cursorToList(cursor);
		SimpleAdapter adapter=new SimpleAdapter(ClassInfoActivity.this,
				list,R.layout.line,new String[]{"cls","time"},new int[]{R.id.cls,R.id.time});
		classView.setAdapter(adapter);
		setActionBarTitle(curClass);
		return true;
  }
	
	protected void initTitle(String date) {
		origTitle+=date.substring(5)+" ";
		origTitle+="空课室 ";
	}
	 
	void setActionBarTitle(int classNum){
		String tmp;
		if(classNum==0){
			tmp="一教";
		}else tmp="二教";
		actionBar=getActionBar();
	  actionBar.setTitle(origTitle+" "+tmp);
	  Drawable drawable=this.getResources().getDrawable(R.drawable.bar);
	  actionBar.setBackgroundDrawable(drawable);
	}
	
	public void goWebsite(View v){
		Intent intent=new Intent(ClassInfoActivity.this,WebsiteActivity.class);
		startActivity(intent);
	}
	
	void updateListView(String[] classSet1,HashMap<String,String>map){
		adapter.clear();
		for (int i = 0; i < classSet1.length; i++) {
			String key=classSet1[i];
			adapter.add(key+" : "+FormatUtil.numToClassTime(map.get(key)));
		}
		classView.setAdapter(adapter);
	}
	
	void updateListViewWhenHoliday(){
		adapter.clear();
		adapter.add("所有课室");
		classView.setAdapter(adapter);
	}
	
	protected void onResume(){
		new Thread() {
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0x111);
			}
		}.start();
		super.onResume();
	}
	
	protected void onStop(){
		String state="";
		for(int i=0;i<5;i++){
			MenuItem item=classMenu.findItem(ids[i]);
			if(item.isChecked()){
				state+="1";
			}else state+="0";
		}
		MenuItem item=classMenu.findItem(R.id.building2);
		if(curClass==0){
			state+="0";
		}else state+="1";
		editor.putString("state", state);
		editor.commit();
		super.onPause();
	}
	
	protected void onDestroy(){
		super.onDestroy();
	}
}
