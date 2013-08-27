package com.baiyjk.shopping.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqLiteHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "byjk.db"; //数据库名称
	private static final String DB_PATH = "/data/data/com.baiyjk.shopping/databases/";
	private static final int DATABASE_VERSION = 1;//数据库版本

//	private static final String SQL_CREATE_CART =
//		    "CREATE TABLE CART (USER_ID INTEGER,  PRODUCT_ID INTEGER, PRODUCT_NUMBER INTEGER )";
	private static final String SQL_CREATE_CATEGOTY = 
			"CREATE TABLE CATEGORY(ID, NAME,PARENT_ID,ATTRIBUTE_SET_ID,POSITION,CAT_LEVEL,DISPLAY_ID)";
	private static final String SQL_DROP_CATEGOTY = 
			"DROP TABLE CATEGORY";
	private Context mContext;
	private SQLiteDatabase db;
	private CursorFactory factory;
	
	public MySqLiteHelper(Context context, String dbName, CursorFactory factory,
			int version) {
		super(context, dbName, factory, version);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.factory = factory;
	}
	
	public MySqLiteHelper(Context context){
		super(context, DB_NAME, null, DATABASE_VERSION); 
		this.mContext = context;
//		db = super.getWritableDatabase();
//		db.execSQL(SQL_DROP_CATEGOTY);
//		db.execSQL(SQL_CREATE_CATEGOTY);
//		try {
//			copyCategory();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

    
    /**
     * 将分类数据从assets目录读出写入数据库
     * @throws IOException
     */
    private void copyCategory() throws IOException{
    		String sourceData = "categoryID(2).txt";
    		Log.d("copyCategory", mContext.toString());
	    	InputStream is = mContext.getAssets().open(sourceData);
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	    String reader = "";
	    while ((reader = in.readLine()) != null)
	    {
	        String[] RowData = reader.split(",");
	//        this.createContact(RowData[0], RowData[1], RowData[2], RowData[3], RowData[4], RowData[5]);
	        if (RowData[7].equals("0")) {
				continue;
			}
	        String sql = "insert into CATEGORY(ID, NAME,PARENT_ID,ATTRIBUTE_SET_ID,POSITION,CAT_LEVEL,DISPLAY_ID) values('" +
	        		RowData[0] + "','" + RowData[1] + "','" + RowData[2] + "','" + 
	        		RowData[3] + "','" + RowData[4] + "','" + RowData[5] + "'," + 
	        		RowData[6] + ")";
	        db.execSQL(sql);
	    }
	    
	    in.close();
    }
    
    /**
     * 只有在初次使用数据库时自动调用一次
     * 创建分类表，并插入数据；
     * @param db
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 创建分类表，并从txt文件中拷贝数据。
		Log.d("create table", "category");
		db.execSQL(SQL_CREATE_CATEGOTY);
		try {
			copyCategory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SQLiteDatabase getWritableDatabase(){
		// TODO create or open a writable database
		db = super.getWritableDatabase();
		if (db == null) {
//			return context.openOrCreateDatabase(DATABASE_NAME , Context.MODE_PRIVATE , null );
			return SQLiteDatabase.openOrCreateDatabase(DB_NAME, factory);
		}
		return db;
	}
	
	@Override
	public SQLiteDatabase getReadableDatabase(){
		db = super.getReadableDatabase();
		if (db == null) {
//			return context.openOrCreateDatabase(DATABASE_NAME , Context.MODE_PRIVATE , null );
			return SQLiteDatabase.openOrCreateDatabase(DB_NAME, factory);
		}
		return db;
	}
}
