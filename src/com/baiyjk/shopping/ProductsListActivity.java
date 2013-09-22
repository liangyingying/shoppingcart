package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baiyjk.shopping.adapter.ProdListSimpleAdapter;
import com.baiyjk.shopping.http.HttpFactory;

public class ProductsListActivity extends ListActivity implements OnScrollListener, OnClickListener{
	private TextView mTv;
	private Context context;
	private final String TAG = "products_list";
	private String responseJsonString;
	private List<Map<String, Object>> attributesList;
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
		mBack.setOnClickListener(this);
		//筛选按钮
		mFilterButton.setOnClickListener(this);
		
		//加载商品数据
		context = this;
		new Thread(new Runnable() {
            
            @Override  
            public void run() {
            		url += "/?format=true";
                final String strContext = responseJsonString = HttpFactory.getHttp().getRequest(url, context);  
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
						responseJsonString = null;
						responseJsonString = HttpFactory.getHttp().getRequest(url, context);  
						//更多商品更新到UI
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Log.d(TAG, responseJsonString);
								if(responseJsonString != null){
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
	/**
	 * 解析Http Response
	 * @return
	 */
	private List<Map<String, Object>> getData(){
		List<Map<String, Object>> productsList = new ArrayList<Map<String, Object>>();
		
		try{
			JSONObject jsonObject = new JSONObject(responseJsonString);
			JSONArray productsJsonArray = jsonObject.getJSONArray("products");
			
			//设置排序URL和翻页URL
			nextPage = jsonObject.getString("nextPage");
			priceSort.setTag(jsonObject.getString("priceSortUrl"));
			salesSort.setTag(jsonObject.getString("salesSortUrl"));
			timeSort.setTag(jsonObject.getString("timeSortUrl"));
			wishSort.setTag(jsonObject.getString("wishSortUrl"));
			//商品列表
			for (int i = 0; i < productsJsonArray.length(); i++) {
				JSONObject productJsonObject = productsJsonArray.getJSONObject(i);
				map = new HashMap<String, Object>();
				String urlString = productJsonObject.get("main_first_category_seo_name").toString() + "/" + productJsonObject.get("product_id") + ".html";
				map.put("url", urlString);
//				map.put("id", productJsonObeject.get("product_id"));
				map.put("name", productJsonObject.get("product_name"));
				map.put("info", productJsonObject.get("product_name_desc"));
				map.put("price", "￥" + productJsonObject.get("price"));
				map.put("image", productJsonObject.get("small_image"));
				
				productsList.add(map);
			}
			//过滤属性列表
			JSONArray attributesJsonArray = jsonObject.getJSONArray("attributes");
			attributesList= new ArrayList<Map<String, Object>>();
			for (int i = 0; i < attributesJsonArray.length(); i++) {
				JSONObject attribute = attributesJsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("key", attribute.getString("key"));//属性名,例如品牌，价格
				map.put("filter_char", attribute.getString("filter_char"));
				map.put("active_count", attribute.getString("active_count"));
				JSONArray values = attribute.getJSONArray("value");
				List<Map<String, String>> valueList = new ArrayList<Map<String,String>>();
				for (int j = 0; j < values.length(); j++) {
					Map<String, String> valueMap = new HashMap<String, String>();
					if (values.getJSONObject(i).getInt("id") == -1) {//全部
						continue;
					}
					valueMap.put("id", values.getJSONObject(i).getString("id"));
					valueMap.put("option_value", values.getJSONObject(i).getString("option_value"));
					valueMap.put("url", values.getJSONObject(i).getString("url"));//sort-886-1-2
					valueMap.put("is_active", values.getJSONObject(i).getString("is_active"));
					valueMap.put("active_index", values.getJSONObject(i).getString("active_index"));
					valueMap.put("url_position", values.getJSONObject(i).getString("url_position"));
					valueList.add(valueMap);
				}
				map.put("value", valueList);
				attributesList.add(map);
			}
		}catch (JSONException e) {
			// TODO: handle exception
		}
		return productsList;
	}

	
	@Override
	public void onClick(View v) {
		//过滤按钮
		if (v.getId() == R.id.product_list_filter) {
			Log.d(TAG, attributesList.toString());
			if (attributesList == null) {
				return;
			}
			//TODO 展示过滤属性
			
		}else if (v.getId() == R.id.product_list_back) {//后退
			finish();
		}else{//排序
			String url = v.getTag().toString();
			Intent intent = new Intent(this, ProductsListActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
		}
	}

}
