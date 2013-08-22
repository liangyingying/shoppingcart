package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.adapter.WishListAdapter;
import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.PatchByLoginStatus;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class WishActivity extends ListActivity {
	private Context mContext;
	private TextView loadingView;
	private List<Map<String, String>> wishList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContext = this;
		
		initView();
	}
	
	private void initView(){
		loadingView = (TextView)findViewById(R.id.wish_list_loading);
//		/myfavor.do?currentPage=?
		String url = "/myfavor.do?format=true";
		GetWishListTask task = new GetWishListTask();
		task.execute(url);
	}
	
	/**
	 * 点击跳转到商品详情页
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ProductDetailActivity.class);
		intent.putExtra("url", wishList.get(position).get("product_link"));
		startActivity(intent);
	}

	/**
	 * 获取收藏列表
	 *
	 */
	
	class GetWishListTask extends AsyncTask<String, Integer, Boolean>{
		
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response = HttpFactory.getHttp().getRequest(params[0], mContext);
			try {
				JSONArray jsonArray = new JSONArray(response);
				if (jsonArray.length() == 0) {
					return false;
				}else {
					wishList = new ArrayList<Map<String,String>>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = jsonArray.getJSONObject(i);
						Map<String, String> map = new HashMap<String, String>();
						map.put("product_name", obj.getString("product_name"));
						map.put("product_id", obj.getString("product_id"));
						map.put("product_price", obj.getString("product_price"));
						map.put("description", obj.getString("description"));
						map.put("product_link", obj.getString("productlink"));
						map.put("product_pic_link", obj.getString("pictureurllink"));
						wishList.add(map);					
					}
					return true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if (!result) {
				loadingView.setText("您没有收藏任何商品。");
			}else {
				loadingView.setVisibility(View.GONE);
				//TODO 填充ListView
				WishListAdapter adapter = new WishListAdapter(mContext, wishList, 
						R.layout.wish_product_item, 
						new String[]{"product_id", "product_name", "product_price"},
						new int[]{R.id.wish_product_id, R.id.wish_product_name, R.id.wish_product_price});
				setListAdapter(adapter);
			}
		}
	}
}
