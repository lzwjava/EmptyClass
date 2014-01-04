package lzw.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class ParseUtil{
	static List<String> getClassFromHtml(String html) {
		//return nums.subList(13,nums.size());
		List<String>nums=null;
		Document doc=Jsoup.parse(html,"gb2312");
		Elements conts=doc.select("div#con_c");
		Element cont=conts.first();
		if(cont==null) return nums;
		String contStr=cont.text();
		//Log.v("lzw",contStr);
		nums=getNumFromStr(contStr,3,3);
		return nums;
	}
	
	static List<String>getNumFromStr(String str,
	 final int lenSt,final int lenEnd){
		Set<Integer> set=new HashSet<Integer>();
		List<Integer> len1=new ArrayList<Integer>(){{
			for(int i=lenSt;i<=lenEnd;i++)
				add(i);
		}};
		set.addAll(len1);
		List<String>nums=getNumFromStr(str,set);
		return nums;
	}
	
	static List<String> getNumFromStr(String str,Set<Integer> len){
		List<String>nums;
		List<String>ans=new ArrayList<String>();
		nums=getNumFromStr(str);
		for(String s :nums){
			if(len.contains(s.length())){
				ans.add(s);
			}
		}
		return ans;
	}
	
	static List<String> getNumFromStr(String str){
		List<String> nums = new ArrayList<String>();
		String[] sArr=str.replaceAll("[^0-9]+", ",").split(",");
		for (String s : sArr) {
			if (s.length() >0) {
					nums.add(s);
			}
		}
		return nums; 
	}
}
