package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

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
	private String oldPayMethod;
	private String newPayMethod;
	private TextView mInvoiceTitleView;
	private TextView mInvoiceContentNameView;
	private TextView mCustomDescView;
	private Button mSubmitButton;
	private String oldAddressId;
	private int requestCode;
	private LinearLayout mPayView;
	private int mPaymentType;
	private String[] arrs = new String[]{"支付宝"};
	private int mPayId;
	
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
				mReceiverNameView.setText(res.get("name"));
				mReceiverAddessView.setText(res.get("address"));
				mReceiverPhoneView.setText(res.get("phone"));
//				TODO 该区域是否支持货到付款 /ajax/setCurrAddr.do
				if (!res.get("addId").equals(oldAddressId)) {
					//displayId:1,addid:?,shr
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("radio_address", res.get("addid")));
					params.add(new BasicNameValuePair("SHR_receiver_name", res.get("name")));
					params.add(new BasicNameValuePair("SHR_address_districtid", res.get("codeprovince")));
					params.add(new BasicNameValuePair("SHR_address_cityid", res.get("codecity")));
					params.add(new BasicNameValuePair("SHR_address_areaid", res.get("codearea")));
					params.add(new BasicNameValuePair("SHR_address_info", res.get("info")));					
					params.add(new BasicNameValuePair("SHR_post", res.get("post")));
					params.add(new BasicNameValuePair("SHR_telephone", res.get("phone")));
					params.add(new BasicNameValuePair("SHR_email2", res.get("email")));
					params.add(new BasicNameValuePair("displayId", Integer.toString(1)));
					params.add(new BasicNameValuePair("addid", res.get("addid")));
					params.add(new BasicNameValuePair("SHR_district_name", res.get("hanprovince")));
					params.add(new BasicNameValuePair("SHR_city_name", res.get("hancity")));				
					params.add(new BasicNameValuePair("SHR_area_name", res.get("hanarea")));
					params.add(new BasicNameValuePair("ordersign", Integer.toString(0)));
					params.add(new BasicNameValuePair("orderId", "0"));
					params.add(new BasicNameValuePair("receiverFlag", Boolean.toString(true)));
					params.add(new BasicNameValuePair("format", "true"));
					String url = "/ajax/setCurrAddr.do";
					SetAddressTask task = new SetAddressTask();
					task.execute(url, params);
				}
				oldAddressId = res.get("addId");
				break;
			case 1://TODO 支付方式
				
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
//		mPayView = (LinearLayout)findViewById(R.id.order_confirm_pay);
//		mPayTypeView = (TextView)findViewById(R.id.order_confirm_paytype);
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
		
		// 点击支付，修改支付信息。支付方式最多2种，货到付款，在线支付，用弹出框就好。
		//  注意货到付款只支持某些区域, TODO 还和额度有关，购物车总额小于100的不支持
		findViewById(R.id.order_confirm_pay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int checkedItem = 0;
				String checkedPayMethod = oldPayMethod;
				if (mPaymentType == 1) {
					arrs = new String[]{"货到付款", "支付宝"};					
				}
				if (oldPayMethod.equals("支付宝") && mPaymentType == 1) {
					checkedItem = 1;
				}
				newPayMethod = oldPayMethod;
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("请选择支付方式：").setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(arrs, checkedItem, 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								if (arrs[0].equals("货到付款")) {
									newPayMethod = arrs[which];
								}
								dialog.dismiss();
							}
						})
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (newPayMethod != null && !newPayMethod.equals(oldPayMethod)) {
								mPayMethodView.setText(newPayMethod);//此处最好有loading图显示
								String payid = "2";
								String paymode = "66";
								if (newPayMethod.equals("货到付款")) {
									payid = "1";
									paymode = "1";
								}
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("Edit_payid", payid));//1,1
								params.add(new BasicNameValuePair("Edit_paymode", paymode));//2,66
//								params.add(new BasicNameValuePair("outLine_bank", null));
								params.add(new BasicNameValuePair("displayId", Integer.toString(3)));
								params.add(new BasicNameValuePair("ordersign", Integer.toString(0)));
								params.add(new BasicNameValuePair("orderId", "0"));
								params.add(new BasicNameValuePair("receiverFlag", Boolean.toString(true)));
								params.add(new BasicNameValuePair("format", "true"));
								String url = "/ajax/setCurrAddr.do";
								SetAddressTask task = new SetAddressTask();
								task.execute(url, params);
							}
							
						}
					})
					.setNegativeButton("取消", null).show();

			}
		});
		
		// TODO 编辑发票信息
		findViewById(R.id.order_confirm_invoice).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/** 
		 * 提交订单
		 * TODO 提交订单之前确保收货人，配送，支付方式，发票信息已确定；
		 * TODO /ajax/setAddress.do?addressId=receiver.addid 看起来貌似没有必要请求
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
				
				//配送方式 固定，快递送货上门
//				mShippingView.setText(shippingObject.getJSONObject("shipment").getString("dict_show"));
//				mShippingTimeView.setText(shippingObject.getJSONObject("shiptime").getString("dict_show"));
				
				//支付方式
				mPayTypeView.setText(paymentObject.getString("pay_type_name"));
				mPayMethodView.setText(paymentObject.getString("payment"));
				oldPayMethod = paymentObject.getString("payment");
				mPayId = paymentObject.getInt("payid");
				mPaymentType = paymentObject.getInt("paymentType1");//是否支持货到付款
				
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
	
	class SetAddressTask extends AsyncTask<Object, Integer, Boolean>{
		JSONObject obj;
		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO 
			String response = HttpFactory.getHttp().post(params[0].toString(), mContext, (List<NameValuePair>)params[1]);
			try {
				JSONObject resObject = new JSONObject(response);
				if (resObject.has("shippingFee")) {
					obj = resObject;
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				//TODO 更新对应数据
			}
			
		}
		
	}
}
