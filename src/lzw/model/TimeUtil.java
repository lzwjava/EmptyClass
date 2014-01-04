package lzw.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TimeUtil{
	static int getCurTimeIndex(){
		int[] timePoint={0,6,11,17,21};
		int h=getCurHour();
		if(h>=21) return 1;
		int i;
		for(i=0;i<timePoint.length-1;i++)
			if(h>=timePoint[i] && h<timePoint[i+1]){
				break;
			}
		if(i==3) return i+2;
		return i+1;
	}
	
	static int getCurHour(){
		Date date=new Date(System.currentTimeMillis());
		SimpleDateFormat formatter=new SimpleDateFormat
		  ("HH");
		try{
			//date=formatter.parse("12");
		}catch(Exception e){
		}
		String str=formatter.format(date);
		//System.out.println(str);
		return Integer.parseInt(str);
	}
	//dateSubtract("2014-1-1","2013-12-31")=1
	static long dateSubtract(String date1Str,String date2Str){
		SimpleDateFormat df=new SimpleDateFormat
				("yyyy-MM-dd");
		long day=0;
		try{
		  Date date1=df.parse(date1Str);
		  Date date2=df.parse(date2Str);
		  long DateMilliSec=1000*60*60*24;
		  day=(date1.getTime()-date2.getTime())
				/DateMilliSec;
		}catch(ParseException e){
			System.out.println("日期格式有误");
			e.printStackTrace();
		}
		return day;
	}
	
	static String getTodayDate(){
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String ans=df.format(date);
		return ans;
	}
	
	//addDateStr("2013-12-31",1)="2014-1-1"
	static String addDateStr(String dateStr,int rollN){
		SimpleDateFormat df=new SimpleDateFormat(
			"yyyy-MM-dd");
		String[] strs=dateStr.split("-");
		int y,m,d;
		y=Integer.parseInt(strs[0]);
		m=Integer.parseInt(strs[1]);
		d=Integer.parseInt(strs[2]);
		Calendar c=Calendar.getInstance();
		c.set(y,m-1,d);
		c.add(Calendar.DAY_OF_MONTH,rollN);
		Date date=c.getTime();
		return df.format(date);
	}
}