package lzw.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lzw.EmptyClasses.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ClassInfoActivity extends Activity {
	ListView classView;
	String[] classTime = new String[]{"上午第一节", "上午第二节", "下午第一节", "下午第二节",
			"晚上"};
	String[] classSet;
	String[][] classSubSet=new String[6][];
	HashMap<String,String> map;
	Spinner spinner;
	ArrayAdapter<String> adapter;
	SharedPreferences preferences;
	TextView titleView;
	ActionBar actionBar;
	Boolean noClassInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences=getSharedPreferences("lzw.model", MODE_PRIVATE);
		String date=preferences.getString("latestDate",null);
		setContentView(R.layout.class_info);
		classView = (ListView) findViewById(R.id.classView);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		List<String> classList
		  =bundle.getStringArrayList("class");
		noClassInfo=bundle.getBoolean("noClassInfo");
		
		String title="";
		int whichClass=bundle.getInt("WhichClass");
		Log.v("lzw",whichClass+"");
		title+=date+" ";
		if(whichClass==1) title+="一教";
		else title+="二教";
		title+="空课室 ";
		
		actionBar=getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setTitle(title);
		actionBar.setCustomView(R.layout.custom_title);
		
		spinner=(Spinner)findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListner());
		ArrayAdapter<String> adapterSpinner=new ArrayAdapter<String>(
				this,android.R.layout.simple_dropdown_item_1line);
		String[] timeChoices=getResources().getStringArray(R.array.timeChoices);
		for(int i=0;i<timeChoices.length;i++)
			adapterSpinner.add(timeChoices[i]);
		spinner.setAdapter(adapterSpinner);
		
		adapter = new ArrayAdapter<String>(ClassInfoActivity.this,
				android.R.layout.simple_list_item_1);
		map=ListUtil.getMapFromList(classList);
		classSet=ListUtil.getAllKeyFromMap(map);
		Arrays.sort(classSet,new ClassComparator());
		getSubSet();
		int curTimeIndex=TimeUtil.getCurTimeIndex();
		spinner.setSelection(curTimeIndex);
		adapter.notifyDataSetChanged();
	}
	
	void updateListView(String[] classSet1){
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
	
	void printArr(String[] strs){
		for(String s: strs){
			System.out.println(s);
		}
	}
	
	void getSubSet(){
		classSubSet[0]=classSet;
		classSubSet[1]=ListUtil.getSubSetHave(map,classSet,"01234");
		classSubSet[2]=ListUtil.getSubSetHave(map,classSet,"01");
		classSubSet[3]=ListUtil.getSubSetHave(map,classSet,"23");
		classSubSet[4]=ListUtil.getSubSetHave(map,classSet,"0123");
		classSubSet[5]=ListUtil.getSubSetHave(map,classSet,"4");
		ClassComparator classComparator=new
		  ClassComparator();
		for(int i=2;i<6;i++) Arrays.sort(classSubSet[i]);
	}
	
	class SpinnerSelectedListner implements OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> parent,
				View view,int position,long id){
			if(noClassInfo) updateListViewWhenHoliday();
			else updateListView(classSubSet[position]);
		}
		public void onNothingSelected(AdapterView<?> parent){
			
		}
	}
	
	class ClassComparator implements Comparator{
		public final int compare(Object a,Object b){
			String sa=(String)a;
			String sb=(String)b;
			String va=map.get(sa);
			String vb=map.get(sb);
			int len1=va.length(),len2=vb.length();
			if(len1!=len2) return len2-len1;
			else return sa.compareTo(sb); 
		}
	}
}
