package lzw.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper{
	String CREATE_SQL_TABLE="create table class_info"+
     "(id integer primary key autoincrement,cls text,t0 integer,t1 integer"+
			",t2 integer,t3 integer,t4 integer)";
	
	public MyDataBaseHelper(Context cxt,String name,int version){
		super(cxt,name,null,version);
	}
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_SQL_TABLE);
	}
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
	}
	
	void updateData(String cls,int ti,int tiValue){
		if(!isColumnExist(cls)){
			insertData(cls);
		}
		SQLiteDatabase db=getReadableDatabase();
		String tiStr=new String("t"+ti);
		ContentValues values=new ContentValues();
		values.put(tiStr,tiValue);
		db.update("class_info",values,"cls=?",new String[]{cls});
	}
	
	void insertData(String cls){
		SQLiteDatabase db=getReadableDatabase();
		db.execSQL("insert into class_info (cls) values (?)",
			new String[]{cls});
	}
	
	boolean isColumnExist(String columnName){
		boolean ans=false;
		Cursor cursor=null;
		SQLiteDatabase db=getReadableDatabase();
		cursor=db.rawQuery("select * from class_info",null);
		ans=cursor!=null && cursor.getColumnIndex(columnName)!=-1;
		return ans;
	}
	
	void clearTable(){
		SQLiteDatabase db=getReadableDatabase();
		db.execSQL("delete from class_info");
	}
}
