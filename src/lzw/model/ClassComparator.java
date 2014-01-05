package lzw.model;

import java.util.Comparator;
import java.util.HashMap;

public class ClassComparator implements Comparator {
	HashMap<String, String> map;
	public ClassComparator(HashMap<String, String> mapa) {
		map = mapa;
	}
	public final int compare(Object a, Object b) {
		String sa = (String) a;
		String sb = (String) b;
		String va = map.get(sa);
		String vb = map.get(sb);
		int len1 = va.length(), len2 = vb.length();
		if (len1 != len2)
			return len2 - len1;
		else
			return sa.compareTo(sb);
	}
}
