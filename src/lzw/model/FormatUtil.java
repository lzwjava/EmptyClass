package lzw.model;

public class FormatUtil{
	static String[] classTime = new String[]{"上午一", "上午二", "下午一", "下午二",
			"晚上"};
	static String numToClassTime(String num){
		int len=num.length();
		if(len==5) return "全天";
		String ans="";
		boolean first=true;
		if(num.indexOf("01")!=-1){
			ans+="全上午";
			first=false;
		} else {
			for (int i = 0; i <= 1; i++) {
				if (num.indexOf('0' + i)!=-1) {
					if (!first)
						ans += "，";
					else
						first = false;
					ans += classTime[i];
				}
			}
		}
		if(num.indexOf("23")!=-1){
			if(!first) ans+="，";
				else first=false;
			ans+="全下午";
		}else{
			for (int i = 2; i <= 3; i++) {
				if (num.indexOf('0' + i)!=-1) {
					if (!first)
						ans += "，";
					else
						first = false;
					ans += classTime[i];
				}
			}
		}
		if (num.indexOf('4')!=-1) {
			if (!first)
				ans += "，";
			else
				first = false;
			ans += classTime[4];
		}
		return ans;
	}
}