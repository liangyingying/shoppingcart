package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderConfirmActivity extends Activity{
	private Context mContext;
	private TextView mReceiverNameView;
	private TextView mReceiverPhoneView;
	private TextView mReceiverAddessView;
	private TextView mShippingView;
	private TextView mShippingTimeView;
	private LinearLayout mProductsContainer;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private LinearLayout mReceiverView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_confirm);
		
		mContext = this;
		initView();
	}
	
	private void initView(){
		this.mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(mContext);
		
		mReceiverView = (LinearLayout)findViewById(R.id.order_confirm_receiver);
		mReceiverNameView = (TextView)findViewById(R.id.order_confirm_receiver_name);
		mReceiverPhoneView = (TextView)findViewById(R.id.order_confirm_receiver_phone);
		mReceiverAddessView = (TextView)findViewById(R.id.order_confirm_receiver_address);
		mShippingView = (TextView)findViewById(R.id.order_confirm_shipping);
		mShippingTimeView = (TextView)findViewById(R.id.order_confirm_shipping_time);
		mProductsContainer = (LinearLayout)findViewById(R.id.order_confirm_product_container);
		
		//orderId可能有值
		String url = "/showOrder.do?format=true&ordersign=0&orderId=";
		OrderConfirmTask task = new OrderConfirmTask();
		task.execute(url);
		
		//修改收货人信息
		mReceiverView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	class OrderConfirmTask extends AsyncTask<String, Integer, Boolean>{
		private String responseString;
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			responseString = HttpFactory.getHttp().getUrlContext(params[0], mContext);
			if (responseString.length() > 0) {
				return true;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			// TODO 解析response
			try {
				JSONObject responseObject = new JSONObject(responseString);
				JSONObject receiverObject = responseObject.getJSONObject("receiver");
				boolean hasDefaultReceiver = !responseObject.has("SHRNull");
				JSONObject shippingObject = responseObject.getJSONObject("shipping");
				JSONObject paymentObject = responseObject.getJSONObject("payment");
				JSONObject invoiceObject = responseObject.getJSONObject("invoice");
				
				//收货人姓名，电话，地址
				mReceiverNameView.setText(receiverObject.getString("receiver_name"));
				mReceiverPhoneView.setText(receiverObject.getString("telephone"));
				JSONObject addressJsonObject = receiverObject.getJSONObject("displayInfo");
				String address = addressJsonObject.getString("address_district_name")
						.concat(addressJsonObject.getString("address_city_name"))
						.concat(addressJsonObject.getString("address_area_name"))
						.concat(receiverObject.getString("address_info"));
				mReceiverAddessView.setText(address);
				
				//商品 此处代码copy的OrderActivity,商品图片的加载应该在AddView之后？？
				ArrayList<JSONObject> prodObjectsList = new ArrayList<JSONObject>();
				JSONObject cartJsonObject = responseObject.getJSONObject("cartSession");
				JSONObject typesJsonObject = cartJsonObject.getJSONObject("row");
				Iterator<String> iterator = typesJsonObject.keys();//[0,1,5,6,7,8]
				for (int i = 0; i < typesJsonObject.length(); i++) {
					String productType = iterator.next();
					if (typesJsonObject.get(productType).equals(null)) {//不是每种类型的商品都有的
						continue;
					}
					Log.d("Loop 1: type = " + productType, typesJsonObject.get(productType).toString());
//					JSONArray productsJsonArray = typesJsonObject.getJSONArray(productType);
					JSONObject productsJsonObject = typesJsonObject.getJSONObject(productType);
					Iterator<String> iterator2 = productsJsonObject.keys();
					//type=0:[8008641,8008624,...]
					//type=8(赠品):[112029,...] 活动号Promotion_Activity ID
					while (iterator2.hasNext()) {
					
						String index = iterator2.next();
//						Log.d("Loop 2: productId = " + productId, productsJsonObject.getJSONObject(productId).toString());
						JSONObject prodJsonObject;
						if (Integer.parseInt(productType) == 0) {
							//普通商品
							prodJsonObject = productsJsonObject.getJSONObject(index).getJSONObject("result");
							prodObjectsList.add(prodJsonObject);
						}else if (Integer.parseInt(productType) == 8) {
							//买赠活动，可能赠多个商品
							Iterator<String> iterator3 = productsJsonObject.getJSONObject(index).keys();
							while (iterator3.hasNext()) {
								prodJsonObject = productsJsonObject.getJSONObject(index).getJSONObject(iterator3.next());
								prodObjectsList.add(prodJsonObject);
							}
						}else if (Integer.parseInt(productType) == 6) {
							//单品券
							prodJsonObject = productsJsonObject.getJSONObject(index).getJSONObject("result");
							prodObjectsList.add(prodJsonObject);
						}/*else if (Integer.parseInt(productType) == 5) {
							//优惠套装,套装怎么展示是个问题？测试环境下，订单号：1130424666621
							JSONObject productList = productsJsonObject.getJSONObject(index)
								.getJSONObject("result").getJSONObject("productList");
							Iterator<String> iterator3 = productList.keys();
							while (iterator3.hasNext()) {
								prodJsonObject = productList.getJSONObject(iterator3.next());
								prodObjectsList.add(prodJsonObject);
							}
						}*///else if (Integer.parseInt(productType) == 1(X元N),7) {}
						
						
					}
					
				}
				for (int k = 0; k < prodObjectsList.size(); k++) {
					inflateProductView(prodObjectsList.get(k));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		private void inflateProductView(JSONObject prodJsonObject){
			View productView = mLayoutInflater.inflate(R.layout.order_detail_product_item, null);
			
			TextView productUrlView = (TextView)productView.findViewById(R.id.order_detail_product_url);
			ImageView productImageView = (ImageView)productView.findViewById(R.id.order_detail_product_image);
			TextView productNameView = (TextView)productView.findViewById(R.id.order_detail_product_name);
			TextView productPriceView = (TextView)productView.findViewById(R.id.order_detail_product_price);
			TextView productQtyView = (TextView)productView.findViewById(R.id.order_detail_product_qty);
			String imageLink = "";
			try {
				productUrlView.setText(prodJsonObject.getString("link"));
				productNameView.setText(prodJsonObject.getString("name"));
				productPriceView.setText("￥" + prodJsonObject.getString("price"));
				productQtyView.setText(prodJsonObject.getString("qty") + "件");					
				imageLink = prodJsonObject.getString("imageUrl");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			mProductsContainer.addView(productView);
			mImageLoader.DisplayImage(imageLink, productImageView);
		}
		
	}
}
