package lzw.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import android.content.Context;

public class FileUtil {

	public static void write(Context cxt, String fileName, String content, int mode) {
		try {
			FileOutputStream fos;
			fos= cxt.openFileOutput(fileName, mode);
			//fos=new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			ps.print(content);
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String read(Context cxt, String fileName) {
		StringBuilder sb = new StringBuilder("");
		try {
			byte[] buff = new byte[1024];
			int hasRead = 0;
			FileInputStream fis = cxt.openFileInput(fileName);
			while ((hasRead = fis.read(buff)) > 0) {
				sb.append(new String(buff, 0, hasRead));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException f) {
			f.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Be careful about the Charset problem
	 * 
	 * @param id
	 *          ,the id in Raw Directories,such as R.raw.xx
	 * @return the String from the whole file
	 */
	static String getStrFromFile(Context ctx, int id) {
		InputStream is = ctx.getResources().openRawResource(id);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String result = "", line;
		try {
			while ((line = br.readLine()) != null) {
				result += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}