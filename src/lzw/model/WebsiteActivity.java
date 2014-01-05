package lzw.model;

import lzw.EmptyClasses.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebsiteActivity extends Activity{
	WebView show;
	String classUrl="http://jwc.bjfu.edu.cn/jscx/143126.html";
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.website);
		show=(WebView)findViewById(R.id.show);
		show.loadUrl(classUrl);
	}
}