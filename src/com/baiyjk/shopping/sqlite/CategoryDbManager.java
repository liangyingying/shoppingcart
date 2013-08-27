package com.baiyjk.shopping.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.baiyjk.shopping.model.Category;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDbManager {
	private MySqLiteHelper helper;  
    private SQLiteDatabase db; 
    private final String TABLE_NAME = "CATEGORY";
      
    public CategoryDbManager(Context context) {  
        helper = new MySqLiteHelper(context);  
        db = helper.getWritableDatabase();  
    }
    
    public List<Category> querySubCategory(String id){
    		String[] columns = {"ID", "NAME", "PARENT_ID", "CAT_LEVEL", "DISPLAY_ID"};
    		String selection = "PARENT_ID = ?";
    		String[] selectionArgs = {id};
    		Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    		List<Category> list = new ArrayList<Category>();
    		while (cursor.moveToNext()) {
    			Category c = new Category();
    			c.categoryId = cursor.getString(cursor.getColumnIndex("ID"));  
    			c.name = cursor.getString(cursor.getColumnIndex("NAME")); 
    			c.parentId = cursor.getString(cursor.getColumnIndex("PARENT_ID")); 
    			c.categoryLevel = cursor.getString(cursor.getColumnIndex("CAT_LEVEL"));
    			c.displayId = cursor.getString(cursor.getColumnIndex("DISPLAY_ID"));
    			list.add(c);
    		}
    		return list;
    }
}
