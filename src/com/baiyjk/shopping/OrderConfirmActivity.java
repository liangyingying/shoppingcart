package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
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
	private TextView mPayTypeView;
	private TextView mPayMethodView;
	private TextView mInvoiceTitleView;
	private TextView mInvoiceContentNameView;
	private TextView mCustomDescView;
	private Button mSubmitButton;
	private String oldAddressId;
	private int requestCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_confirm);
		
		mContext = this;
		initView();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (intent == null) {//没有更改地址
			return;
		}
		Map<String, String> res = (Map<String, String>)intent.getSerializableExtra("result");
//		int index = intent.getIntExtra("index", -1);
		if (res == null) {
			return;
		}
		switch (requestCode) {
			case 0://收货人
				oldAddressId = res.get("addId");
				mReceiverNameView.setText(res.get("name"));
				mReceiverAddessView.setText(res.get("address"));
				mReceiverPhoneView.setText(res.get("phone"));
				break;
			case 1://TODO 配送
				break;
			default:
				break;
		}
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
		mPayTypeView = (TextView)findViewById(R.id.order_confirm_paytype);
		mPayMethodView = (TextView)findViewById(R.id.order_confirm_paymethod);
		mInvoiceTitleView = (TextView)findViewById(R.id.order_confirm_invoice_title);
		mInvoiceContentNameView = (TextView)findViewById(R.id.order_confirm_invoice_content_name);
		mCustomDescView = (TextView)findViewById(R.id.order_confirm_customdesc);
		mProductsContainer = (LinearLayout)findViewById(R.id.order_confirm_product_container);
		mSubmitButton = (Button)findViewById(R.id.order_confirm_submit);
		
		
		//orderId可能有值
		String url = "/showOrder.do?format=true&ordersign=0&orderId=";
		OrderConfirmTask task = new OrderConfirmTask();
		task.execute(url);
		
		// 修改收货人信息,跳转到收货人列表
		mReceiverView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(mContext, ReceiverActivity.class);
					intent.putExtra("fromActivity", "OrderConfirmActivity");
					intent.putExtra("oldAddressId", oldAddressId);
					requestCode = 0;
					startActivityForResult(intent, requestCode);
//				}
//				return false;
			}
		});
		
		//TODO 点击支付，修改支付信息。支付方式最多三种，货到付款，在线支付，银行电汇，用弹出框就好。
		// 注意货到付款只支持某些区域
		findViewById(R.id.order_confirm_pay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/** 
		 * 提交订单
		 * TODO 提交订单之前确保收货人，配送，支付方式，发票信息已确定；
		 * TODO /ajax/setAddress.do?addressId=receiver.addid 看起来貌似没有并要请求
		 * TODO 如果已有orderId，查看是否还可以更改 /sales/order/checkOrderPrint?orderId=?
		 * 		如果没有orderId,查看是否超出当日订单最大数，/ajax/orderMaxLimitValid.do
		 * TODO 查看商品库存是否还够 /ajax/orderProdStockValid.do
		 * TODO 都通过之后提交订单。包括数据addressChange，SHR_addid,ordersign,orderId,备注
		 * TODO 其余数据是在更新时单独保存 /ajax/setCurrAddr.do GET
		 */
		mSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "/submitOrder.do";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				OrderSubmitTask task = new OrderSubmitTask();
				task.execute(url, params);
			}
		});
	}
	class OrderSubmitTask extends AsyncTask<Object, Integer, Boolean>{
		private String response;
		@Override
		protected Boolean doInBackground(Object... params) {
			response = HttpFactory.getHttp().post(params[0].toString(), mContext, (List<NameValuePair>)params[1]);
			if (response.length() > 0) {
				return true;
			}
			return false;
		}
	
	}
	
	class OrderConfirmTask extends AsyncTask<String, Integer, Boolean>{
		private String responseString;
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			responseString = HttpFactory.getHttp().getRequest(params[0], mContext);
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
				
				//收货人姓名，电话，地址。除了收货人其他信息都有默认值。
				if (!hasDefaultReceiver) {
					mReceiverNameView.setText("您没有设置默认收货地址。");
					oldAddressId = null;
				}else {
					mReceiverNameView.setText(receiverObject.getString("receiver_name"));
					mReceiverPhoneView.setText(receiverObject.getString("telephone"));
					JSONObject addressJsonObject = receiverObject.getJSONObject("displayInfo");
					String address = addressJsonObject.getString("address_district_name")
							.concat(addressJsonObject.getString("address_city_name"))
							.concat(addressJsonObject.getString("address_area_name"))
							.concat(receiverObject.getString("address_info"));
					mReceiverAddessView.setText(address);
					oldAddressId = receiverObject.getString("addid");
				}
				
				//配送方式
				mShippingView.setText(shippingObject.getJSONObject("shipment").getString("dict_show"));
				mShippingTimeView.setText(shippingObject.getJSONObject("shiptime").getString("dict_show"));
				
				//支付方式
				mPayTypeView.setText(paymentObject.getString("pay_type_name"));
				mPayMethodView.setText(paymentObject.getString("payment"));
				
				//发票
				if(invoiceObject.getString("if_invoice") == "0"){
					mInvoiceContentNameView.setText("无发票");
					mInvoiceTitleView.setVisibility(View.GONE);
				}else {
					mInvoiceContentNameView.setText(invoiceObject.getString("title"));
					mInvoiceTitleView.setText(invoiceObject.getString("content_name"));
					mInvoiceTitleView.setTag(invoiceObject.getInt("title_mode"));
					mInvoiceContentNameView.setTag(invoiceObject.getString("content"));
				}
				
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
