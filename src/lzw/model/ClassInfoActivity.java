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
	String[] classSet1,classSet2;
	String[][] classSubSet1=new String[6][],
			classSubSet2=new String[6][];
	HashMap<String,String> map1,map2;
	Spinner spinner,class_spinner;
	ArrayAdapter<String> adapter;
	SharedPreferences preferences;
	TextView titleView;
	ActionBar actionBar;
	Boolean noClassInfo;
	int curTimeIndex;
	int curClass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_info);
		
		preferences=getSharedPreferences("lzw.model", MODE_PRIVATE);
		String date=preferences.getString("latestDate",null);
		classView = (ListView) findViewById(R.id.classView);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		List<String> classList1
		  =bundle.getStringArrayList("class1");
		List<String> classList2=bundle.getStringArrayList("class2");
		noClassInfo=bundle.getBoolean("noClassInfo");
		
		String title="";
		int whichClass=bundle.getInt("WhichClass");
		title+=date.substring(5)+" ";
		title+="空课室 ";
		actionBar=getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setTitle(title);
		actionBar.setCustomView(R.layout.custom_title);
		
		spinner=(Spinner)findViewById(R.id.spinner);
		SpinnerSelectedListner listner = new SpinnerSelectedListner();
		spinner.setOnItemSelectedListener(listner);
		class_spinner=(Spinner)findViewById(R.id.class_spinner);
		class_spinner.setOnItemSelectedListener(listner);
		
    adapter = new ArrayAdapter<String>(ClassInfoActivity.this,
				android.R.layout.simple_list_item_1);
		map1=ListUtil.getMapFromList(classList1);
		map2=ListUtil.getMapFromList(classList2);
		classSet1=ListUtil.getAllKeyFromMap(map1);
		classSet2=ListUtil.getAllKeyFromMap(map2);
		Arrays.sort(classSet1,new ClassComparator(map1));
		Arrays.sort(classSet2,new ClassComparator(map2));
		getSubSet(classSet1,map1,classSubSet1);
		getSubSet(classSet2,map2,classSubSet2);
		
		curTimeIndex=TimeUtil.getCurTimeIndex();
		curClass=1;
		spinner.setSelection(curTimeIndex);
		class_spinner.setSelection(curClass);
		adapter.notifyDataSetChanged();
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
	
	void printArr(String[] strs){
		for(String s: strs){
			System.out.println(s);
		}
	}
	
	void getSubSet(String[] classSet,HashMap<String,String>map,
	 String classSubSet[][]){
		classSubSet[0]=classSet;
		classSubSet[1]=ListUtil.getSubSetHave(map,classSet,"01234");
		classSubSet[2]=ListUtil.getSubSetHave(map,classSet,"01");
		classSubSet[3]=ListUtil.getSubSetHave(map,classSet,"23");
		classSubSet[4]=ListUtil.getSubSetHave(map,classSet,"0123");
		classSubSet[5]=ListUtil.getSubSetHave(map,classSet,"4");
		ClassComparator classComparator=new
		  ClassComparator(map);
		for(int i=2;i<6;i++) Arrays.sort(classSubSet[i]);
	}
	
	class SpinnerSelectedListner implements OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> parent,
				View view,int position,long id){
			int pid=parent.getId();
			if(pid==class_spinner.getId()&&
				 position==2){
				goWebsite(null);
			}
				
			if(pid==spinner.getId()){
				curTimeIndex=position;
			}else if(pid==class_spinner.getId()){
				curClass=position;
			}
			HashMap<String,String> map=null;
			String[][] classSubSet;
			if(curClass==0){
				classSubSet=classSubSet1;
				map=map1;
			}else{
				classSubSet=classSubSet2;
				map=map2;
			}
			if(noClassInfo) updateListViewWhenHoliday();
			else updateListView(classSubSet[curTimeIndex],map);
		}
		public void onNothingSelected(AdapterView<?> parent){
			
		}
	}
	
	class ClassComparator implements Comparator{
		HashMap<String,String> map;
		public ClassComparator(HashMap<String,String>mapa){
			map=mapa;
		}
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
