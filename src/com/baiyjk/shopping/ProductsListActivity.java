package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baiyjk.shopping.adapter.ProdListSimpleAdapter;
import com.baiyjk.shopping.http.HttpFactory;

public class ProductsListActivity extends ListActivity implements OnScrollListener{
	private TextView mTv;
	private Context context;
	private final String TAG = "products_list";
	private String productsJsonString;
	private ProdListSimpleAdapter adapter;
	private Map<String, Object> map;
	private View mBack;
	private View mFilterButton;
	private String url;//e.g /conghuiyunying/sort-1059-3-2
	private boolean loadingMore;
	private String page;//sort-displayId-order-page，
	private String orderBy;//按销量降序1，按价格升序2，按价格降序3，按关注降序4，按上架时间降序5
	private int nextPage;
	private String displayId;
	private String seoName;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products_list);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		
		initUrlParam(url);
		initView();
	}
	
	private void initUrlParam(String url) {
		String [] urlArr = url.split("/");
		seoName = urlArr[0];
		String[] params= urlArr[1].split("-");
		displayId = params[1];
		if (params.length > 2) {
			orderBy = params[2];
			page = params[3];
		}else {
			orderBy = page = "1";
		}
	}

	private void initView(){
		mTv = (TextView)findViewById(R.id.productlistloading);
		mBack = findViewById(R.id.product_list_back);
		mFilterButton = findViewById(R.id.product_list_filter);
		
		//后退按钮
		mBack.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		context = this;
		new Thread(new Runnable() {
            
            @Override  
            public void run() {
            		url += "/?format=true";
                final String strContext = productsJsonString = HttpFactory.getHttp().getRequest(url, context);  
                runOnUiThread(new Runnable() {                    
                    @Override  
                    public void run() {  
                        if(strContext!=null){ 
                            mTv.setVisibility(View.GONE);
                            adapter = new ProdListSimpleAdapter(context, getData(), R.layout.product_item, 
                            		new String[]{"url","name", "info", "price","image"},
                            		new int[] {R.id.list_product_url, R.id.list_product_name, R.id.list_product_info, R.id.list_product_price, R.id.list_product_image}
                            );
                            
                            setListAdapter(adapter);
//                            Log.d(TAG, strContext);
                        }else  
                            mTv.setText("加载失败......");  
                    }
                });  
            }  
        }).start(); 
		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if((lastInScreen >= totalItemCount) && !(loadingMore)){
			Thread thread =  new Thread(null, loadMoreListItems);
			thread.start();
		}
	}

	//上拉加载更多商品
	private Runnable loadMoreListItems = new Runnable() {
		
		@Override
		public void run() {
			loadingMore = true;
			nextPage = Integer.valueOf(page) + 1;
			url += "/" + seoName + "/sort-" + displayId + "-" + orderBy + "-" + nextPage + "/?format=true";
			productsJsonString = null;
			productsJsonString = HttpFactory.getHttp().getRequest(url, context);  
            runOnUiThread(updateAdapter);
		}
	};
	
	//更多商品更新到UI
	private Runnable updateAdapter = new Runnable() {
		@Override
		public void run() {
			if(productsJsonString != null){ 
//				adapter.add();
//                ProdListSimpleAdapter adapter = new ProdListSimpleAdapter(context, getData(), R.layout.product_item, 
//                		new String[]{"url", "name", "info", "price","image"},
//                		new int[] {R.id.list_product_url, R.id.list_product_name, R.id.list_product_info, R.id.list_product_price, R.id.list_product_image}
//                );
//                
//                setListAdapter(adapter);
                loadingMore = false;
                page = String.valueOf(nextPage);
//                Log.d(TAG, strContext);
            }else  {
                //没有更多商品了
            	 	
            }
		}
	};
	
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	private List<Map<String, Object>> getData(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try{
			JSONArray productsJasonArray = new JSONArray(productsJsonString);
			for (int i = 0; i < productsJasonArray.length(); i++) {
//				Log.d(TAG, productsJasonArray.getJSONObject(i).toString());
				JSONObject productJsonObeject = new JSONObject(productsJasonArray.getJSONObject(i).toString());
				map = new HashMap<String, Object>();
				String urlString = productJsonObeject.get("main_first_category_seo_name").toString() + "/" + productJsonObeject.get("product_id") + ".html";
				map.put("url", urlString);
//				map.put("id", productJsonObeject.get("product_id"));
				map.put("name", productJsonObeject.get("product_name"));
				map.put("info", productJsonObeject.get("product_name_desc"));
				map.put("price", "￥" + productJsonObeject.get("price"));
				map.put("image", productJsonObeject.get("small_image"));
				
				list.add(map);
			}
		}catch (JSONException e) {
			// TODO: handle exception
		}
	    
		
		
		return list;
	}

	
	
}
