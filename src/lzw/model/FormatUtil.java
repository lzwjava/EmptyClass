package lzw.model;

public class FormatUtil{
	static String[] classTime = new String[]{"����һ", "�����", "����һ", "�����",
			"����"};
	static String numToClassTime(String num){
		int len=num.length();
		if(len==5) return "ȫ��";
		String ans="";
		boolean first=true;
		if(num.indexOf("01")!=-1){
			ans+="ȫ����";
			first=false;
		} else {
			for (int i = 0; i <= 1; i++) {
				if (num.indexOf('0' + i)!=-1) {
					if (!first)
						ans += "��";
					else
						first = false;
					ans += classTime[i];
				}
			}
		}
		if(num.indexOf("23")!=-1){
			if(!first) ans+="��";
				else first=false;
			ans+="ȫ����";
		}else{
			for (int i = 2; i <= 3; i++) {
				if (num.indexOf('0' + i)!=-1) {
					if (!first)
						ans += "��";
					else
						first = false;
					ans += classTime[i];
				}
			}
		}
		if (num.indexOf('4')!=-1) {
			if (!first)
				ans += "��";
			else
				first = false;
			ans += classTime[4];
		}
		return ans;
	}
}