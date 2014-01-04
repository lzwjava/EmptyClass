package lzw.model;

public class FormatUtil{
	static String[] classTime = new String[]{"上午第一节", "上午第二节", "下午第一节", "下午第二节",
			"晚上"};
	static String numToClassTime(String num){
		int len=num.length();
		if(len==5) return "全天";
		boolean[] used=new boolean[len];
		String ans="";
		boolean morning=false,afternoon=false;
		for(int i=0;i<len;i++){
			if(num.charAt(i)=='0'&& i+1<len 
					&& num.charAt(i+1)=='1'){
				morning=true;
				used[i]=used[i+1]=true;
			}
			if(num.charAt(i)=='2'&& i+1<len 
					&& num.charAt(i+1)=='3'){
				afternoon=true;
				used[i]=used[i+1]=true;
			}
		}
		boolean first=true;
		if(morning){
			ans+="全上午"; first=false;
		}
		if(afternoon){
			if(!first) {
				ans+="，";
			}else first=false;
			ans+="全下午";
		}
		for(int i=0;i<len;i++) if(!used[i]){
			if(!first) {
				ans+="，";
			}else first=false;
			ans+=classTime[Integer.parseInt
			  (num.substring(i,i+1))];
		}
		return ans;
	}
}