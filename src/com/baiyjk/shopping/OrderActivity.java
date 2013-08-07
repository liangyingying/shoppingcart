package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderActivity extends Activity {
	
	private Button backButton;
	private TextView orderIdView;
	private TextView orderValueView;
	private TextView orderStatusView;
	private LinearLayout mProductsContainer;
	private String mOrderId;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private TextView mOrderRecieverNameView;
	private TextView mOrderRecieverPhoneView;
	private TextView mOrderRecieverAddressView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		
		Intent intent = getIntent();
		mOrderId = intent.getStringExtra("orderId");
		mContext = this;
		mImageLoader = new ImageLoader(mContext);
		initView();
	}
	
	private void initView(){
		this.mLayoutInflater = LayoutInflater.from(mContext);
		backButton = (Button)findViewById(R.id.order_detail_back);
		orderIdView = (TextView)findViewById(R.id.order_detail_id);
		orderValueView = (TextView)findViewById(R.id.order_detail_value);
		orderStatusView = (TextView)findViewById(R.id.order_detail_status);
		mProductsContainer = (LinearLayout)findViewById(R.id.order_detail_product_container);
		
		mOrderRecieverNameView = (TextView)findViewById(R.id.order_receiver_name);
		mOrderRecieverAddressView = (TextView)findViewById(R.id.order_receiver_address);
		mOrderRecieverPhoneView = (TextView)findViewById(R.id.order_receiver_phone);
		
		String url = "/displayOrder.do?format=true&saleOrderId=" + mOrderId;
		
		GetOrderDetailTask task = new GetOrderDetailTask();
		task.execute(url);
		
		
	}
	
	class GetOrderDetailTask extends AsyncTask<String, Integer, Boolean>{

		String response;
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO 网络请求
			response = HttpFactory.getHttp().getUrlContext(params[0], mContext);
			if (response.length() > 0) {
				return true;
			}
			return false;
		}
		
		@Override  
        protected void onPostExecute(Boolean result) {
//			Log.d("order detail 返回值：", response);
            super.onPostExecute(result);
            JSONObject jsonObject;
			try {
				//订单
				jsonObject = new JSONObject(response);
				orderIdView.setText(jsonObject.getString("orderId").toString());
				String payMethod = jsonObject.getString("paymethod").toString();
				String orderStatus = jsonObject.getString("orderchinesestatus").toString();
				
				//收货人信息
				JSONObject receiver = jsonObject.getJSONObject("receiver");
				JSONObject addressJsonObject = receiver.getJSONObject("displayInfo");
				String address = addressJsonObject.getString("address_district_name")
						.concat(addressJsonObject.getString("address_city_name"))
						.concat(addressJsonObject.getString("address_area_name"))
						.concat(receiver.getString("address_info")); 
				mOrderRecieverAddressView.setText(address);
				mOrderRecieverNameView.setText(receiver.getString("receiver_name"));
				mOrderRecieverPhoneView.setText(receiver.getString("telephone"));
				
				//购物车
				JSONObject cartJsonObject = jsonObject.getJSONObject("cartSession");
				String orderSize = cartJsonObject.getString("qty");
				String orderPoint = cartJsonObject.getString("point");//积分
				double orderValue = cartJsonObject.getDouble("price");//原始金额
				double orderCutValue = cartJsonObject.getDouble("cutPrice");//促销优惠金额
				double orderShippingFee = cartJsonObject.getDouble("shippingFee");//运费
				double orderVirtualValue = 0;
				double orderCouponValue = 0;
				if (cartJsonObject.has("virtualAccount") && cartJsonObject.getJSONObject("virtualAccount").getInt("ifUsed") == 1) {//通过虚拟账户支付,付款金额
					orderVirtualValue = cartJsonObject.getJSONObject("virtualAccount").getDouble("cost");
				}
				if (!cartJsonObject.get("coupon").equals(null) && cartJsonObject.getJSONObject("coupon").has("cutMoneyCoupon")){//订单使用现金优惠券
					orderCouponValue = cartJsonObject.getJSONObject("coupon").getDouble("cutMoneyCoupon");
				}
				//还有单品券减金额coupon-》cutGiftCoupon;积分抵扣金额 pointsExchange;实际支付为计算值。
				double orderRealPay = orderValue + orderShippingFee - orderCutValue - orderCouponValue - orderVirtualValue;
				orderValueView.setText("" + orderRealPay);
				orderStatusView.setText(orderStatus);
				
				//商品
				ArrayList<JSONObject> prodObjectsList = new ArrayList<JSONObject>();
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
			
			try {
				productUrlView.setText(prodJsonObject.getString("link"));
				productNameView.setText(prodJsonObject.getString("name"));
				productPriceView.setText("￥" + prodJsonObject.getString("price"));
				productQtyView.setText(prodJsonObject.getString("qty") + "件");					
				mImageLoader.DisplayImage(prodJsonObject.getString("imageUrl"), productImageView);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			mProductsContainer.addView(productView);
		}
	}
}
