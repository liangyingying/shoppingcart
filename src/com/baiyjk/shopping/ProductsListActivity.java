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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baiyjk.shopping.adapter.ProdListSimpleAdapter;
import com.baiyjk.shopping.http.HttpFactory;

public class ProductsListActivity extends ListActivity implements OnScrollListener, OnClickListener{
	private TextView mTv;
	private Context context;
	private final String TAG = "products_list";
	private String productsJsonString;
	private ProdListSimpleAdapter adapter;
	private Map<String, Object> map;
	private View mBack;
	private View mFilterButton;
	private String url;//e.g /conghuiyunying/sort-1059-3-2
	private boolean loadingMore = false;
	private String page;//sort-displayId-order-page，
	private String orderBy;//按销量降序1，按价格升序2，按价格降序3，按关注降序4，按上架时间降序5
	private String nextPage;
	private String displayId;
	private String seoName;
	private ListView mListView;
	private List<Map<String, Object>> list;
	private View priceSort;
	private View salesSort;
	private View timeSort;
	private View wishSort;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products_list);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		
//		initUrlParam(url); 下一页的url从服务器端获取，不再解析
		initView();
	}
	
	private void initUrlParam(String url) {
		String [] urlArr = url.split("/");
		seoName = urlArr[1];
		String[] params= urlArr[2].split("-");Log.d(TAG, params[0] + params.length);
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
		mListView = (ListView)findViewById(android.R.id.list);
		//注册上拉到底部加载更多事件
		mListView.setOnScrollListener(this);
		
		//初始化排序View
		priceSort = findViewById(R.id.product_sortby_price);
		salesSort = findViewById(R.id.product_sortby_sales);
		timeSort = findViewById(R.id.product_sortby_time);
		wishSort = findViewById(R.id.product_sortby_wish);
		//注册点击排序时间
		priceSort.setOnClickListener(this);
		salesSort.setOnClickListener(this);
		timeSort.setOnClickListener(this);
		wishSort.setOnClickListener(this);
		
		//后退按钮
		mBack.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//加载商品数据
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
		if(totalItemCount > 0 && (lastInScreen >= totalItemCount) && !loadingMore){
			Log.d(TAG, "load more");
			Thread thread =  new Thread(null, new Runnable() {
				
				@Override
				public void run() {
					loadingMore = true;
					if (!nextPage.equals("null")) {
						url = nextPage + "?format=true";
//						nextPage = Integer.valueOf(page) + 1;
//						url = "/" + seoName + "/sort-" + displayId + "-" + orderBy + "-" + nextPage + "/?format=true";
						productsJsonString = null;
						productsJsonString = HttpFactory.getHttp().getRequest(url, context);  
						//更多商品更新到UI
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Log.d(TAG, productsJsonString);
								if(productsJsonString != null){
									adapter.data.addAll(getData());
					                adapter.notifyDataSetChanged();
					                loadingMore = false;
					                
					            }else  {
					                //没有更多商品了
					            		loadingMore = true;
					            }
							}
						});
					}else {
						//已经是最后一页啦
						loadingMore = true;
					}
										
				}
			});
			thread.start();
			
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	private List<Map<String, Object>> getData(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try{
			JSONArray productsJasonArray = new JSONObject(productsJsonString).getJSONArray("products");
			nextPage = new JSONObject(productsJsonString).getString("nextPage");
			priceSort.setTag(new JSONObject(productsJsonString).getString("priceSortUrl"));
			salesSort.setTag(new JSONObject(productsJsonString).getString("salesSortUrl"));
			timeSort.setTag(new JSONObject(productsJsonString).getString("timeSortUrl"));
			wishSort.setTag(new JSONObject(productsJsonString).getString("wishSortUrl"));
			
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

	/**
	 * 点击排序
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String url = v.getTag().toString();
		Intent intent = new Intent(this, ProductsListActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}

}
