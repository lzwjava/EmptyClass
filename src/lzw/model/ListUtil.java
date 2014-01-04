package lzw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil{
	static ArrayList<String> getSubList(ArrayList<String> list, int st, int end) {
		ArrayList<String> subList = new ArrayList<String>();
		for (int i = st; i < end; i++)
			subList.add(list.get(i));
		return subList;
	}
	
	static ArrayList<String> splitList(List<String> list) {
		ArrayList<String> result = new ArrayList<String>();
		String last = "888";
		List<String> curList = null;
		int cnt = 0;
		for (int i = 0; i < list.size(); i++) {
			String cur = list.get(i);
			if (cur.compareTo(last) < 0) {
				if (last != "888") {
					result.add(curList.toString());
				}
				curList = new ArrayList<String>();
				cnt++;
				if (cnt >= 5)
					cnt = 0;
			}
			curList.add(cur);
			last = cur;
		}
		result.add(curList.toString());
		return result;
	}
	
	static String[] getAllKeyFromMap(HashMap<String,String> map){
		String[] ans=new String[map.size()];
		int i=0;
		for(Map.Entry<String,String> entry : map.entrySet()){
			ans[i]=entry.getKey().toString();
			i++;
		}
		return ans;
	}
	
	static HashMap<String,String> getMapFromList(List<String> list){
		HashMap<String,String> map=
		  new HashMap<String,String>();
		for(int i=0;i<list.size();i++){
			//String tmp=map.get("")
			for(String s : list.get(i).replaceAll("[^0-9]"
					,",").split(",")){
				if(s.length()<3) continue;
				String value=map.get(s);
				if(value==null){
					value="";
				}
				value+=i;
				map.put(s,value);
			}
		}
		return map;
	}
	
	static String[] getSubSetHave(HashMap<String,String> map,String[] allKey,String substr){
		ArrayList<String> ans=new ArrayList<String>();
		for(String k : allKey){
			String v=map.get(k);
			if(v.indexOf(substr)>=0){
				ans.add(k);
				//System.out.println(v+" and "+substr);
			}
		}
		return ans.toArray(new String[ans.size()]);
	}
}