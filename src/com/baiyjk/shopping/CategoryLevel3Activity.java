package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baiyjk.shopping.model.Category;
import com.baiyjk.shopping.sqlite.CategoryDbManager;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CategoryLevel3Activity extends ListActivity{

	private Context mContext;
	private String categoryId;
	private String categoryName;
	private ListView listView;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(mContext, ProductsListActivity.class);
		
		String cId = ((TextView)v.findViewById(R.id.level2_item_id)).getText().toString();
		String displayId = ((TextView)v.findViewById(R.id.level2_item_display_id)).getText().toString();
		String url = getUrl(cId, displayId);
		Log.d("商品列表页", url);
		intent.putExtra("url", url);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_level3);
		
		this.mContext = this;
		Intent intent = getIntent();
		this.categoryId = intent.getStringExtra("categoryId");
		this.categoryName = intent.getStringExtra("categoryName");
		
		//后退
		ImageView backView = (ImageView)findViewById(R.id.category_level3_back);
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		TextView titlebar = (TextView)findViewById(R.id.category_level3_titlebar);
		titlebar.setText(this.categoryName);
		this.listView = (ListView)findViewById(android.R.id.list);
		loadCategories();
	}

	//加载三级分类
    private void loadCategories(){
    		CategoryDbManager dbManager = new CategoryDbManager(mContext);
    		List<Category> list = dbManager.querySubCategory(this.categoryId);
    		
    	    ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>(); 
    	    for(int i=0; i < list.size(); i++)  
    	    {  
    	        HashMap<String, Object> map2 = new HashMap<String, Object>();
    	        map2.put("ItemId", list.get(i).categoryId);
    	        map2.put("ItemName", list.get(i).name);
    	        map2.put("ItemDisplayId", list.get(i).displayId);
//    	        Log.d("from table category", list.get(i).categoryId + ":" + list.get(i).name);
    	        mylist.add(map2);  
    	    }
    	    SimpleAdapter adapter = new SimpleAdapter(mContext, mylist, 
    	    		R.layout.category_level2_listitem, 
    	    		new String[] {"ItemId", "ItemName", "ItemDisplayId"}, 
    	    		new int[] {R.id.level2_item_id, R.id.level2_item_name, R.id.level2_item_display_id});
    	    
    	    this.listView.setAdapter(adapter);
    }
    
  //TODO 木有分页；
  	private String getUrl(String categoryId, String displayId){
  		String url = "";
  		switch (categoryId.charAt(2)) {
  		case '1':
  			url = "/conghuiyunying";
  			break;
  		case '2':
  			url = "/yingyangjiapin";
  			break;
  		case '3':
  			url = "/zhongxiyaopin";
  			break;
  		case '4':
  			url = "/meifuhuyan";
  			break;
  		case '5':
  			url = "/jiankangshenghuo";
  			break;
  		case '6':
  			url = "/zhongyaoyangsheng";
  			break;
  		default:
  			url = "/conghuiyunying";
  			break;
  		}
  		url += "/sort-" + displayId;
  		return url;
  	}
	
}
