package com.baiyjk.shopping.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.CouponActivity;
import com.baiyjk.shopping.OrderActivity;
import com.baiyjk.shopping.R;
import com.baiyjk.shopping.ReceiverActivity;
import com.baiyjk.shopping.WishActivity;
import com.baiyjk.shopping.adapter.ProductDetaiViewPagerAdapter;
import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;
import com.baiyjk.shopping.utils.PatchByLoginStatus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class AccountFragment extends Fragment{
	private Context mContext;
	private String mResponse;
	private ImageView accountImageView;
	private TextView usernameTextView;
	private TextView pointTextView;
	private TextView moneyTextView;
	private View accountOrderView;
	private LinearLayout mybaiyang;
	private LayoutInflater mLayoutinflater;
//	private ViewPager mViewPager;
	private ProductDetaiViewPagerAdapter mViewPagerAdapter;
	private TextView loadingView;
	private RadioButton wishButton;
	private RadioButton addressButton;
	private RadioButton couponButton;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.account, container, false);
		mContext = v.getContext();
		initView(v);
		return v;
	}

	private void initView(View v){
		this.mLayoutinflater = LayoutInflater.from(mContext);
		loadingView = (TextView)v.findViewById(R.id.account_loading);
		accountImageView = (ImageView)v.findViewById(R.id.account_image);
		usernameTextView = (TextView)v.findViewById(R.id.account_name_grade);
		
		pointTextView = (TextView)v.findViewById(R.id.account_point);
		moneyTextView = (TextView)v.findViewById(R.id.account_money);
		
		wishButton = (RadioButton)v.findViewById(R.id.account_tab_wish);
		addressButton = (RadioButton)v.findViewById(R.id.account_tab_address);
		couponButton = (RadioButton)v.findViewById(R.id.account_tab_coupon);
		

		mybaiyang = (LinearLayout)v.findViewById(R.id.mybaiyang);
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO 一次返回了所有的订单，还需改进
				String url = "/mybaiyang.do?format=true";
				mResponse = HttpFactory.getHttp().getRequest(url, mContext);
				getActivity().runOnUiThread(new Runnable() {               
                   
					@Override  
                    public void run() {
						loadingView.setVisibility(View.GONE);
						Log.d("我的百洋Json", mResponse);
						try {
							JSONObject jsonObject = new JSONObject(mResponse);
							usernameTextView.setText(jsonObject.getString("username") + "  等级：" 
									+ jsonObject.getString("userGrade"));
							pointTextView.setText("积分：" + jsonObject.getString("userPoint"));

							String levelId = jsonObject.getString("levelId");
							//1.动态设置头像							
							Resources res=getResources();
							int imageResource = res.getIdentifier("level_" + levelId,"drawable", mContext.getPackageName());
							//2.用反射机制实现动态调用R资源
//							Field field = R.drawable.class.getField("level_" + levelId);
//							int imageResource = field.getInt(new R.drawable());
							
							accountImageView.setBackgroundResource(imageResource);

							JSONArray jsonArray = new JSONArray(jsonObject.getString("orders"));
							ImageLoader imageLoader = new ImageLoader(mContext);
							
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject orderObject = new JSONObject(jsonArray.get(i).toString());
								View accountOrderView = mLayoutinflater.inflate(R.layout.order_item, null);
								
								
								TextView orderIdTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_id));
								TextView orderSizeTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_size));
								TextView orderStatusTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_status));
								TextView orderValueTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_value));
								TextView orderPayTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_pay));
								TextView orderTimeTextView = (TextView)(accountOrderView.findViewById(R.id.account_order_time));
								Button orderButton = (Button)(accountOrderView.findViewById(R.id.account_order_button));
								LinearLayout imagesContainer = (LinearLayout)accountOrderView.findViewById(R.id.account_product_images_container);
//								HorizontalScrollView scrollView = (HorizontalScrollView)accountOrderView.findViewById(R.id.account_product_images_scrollview);
//								scrollView.setHorizontalScrollBarEnabled(false);
								
								orderIdTextView.setText(orderObject.getString("orderId"));
								orderSizeTextView.setText("共" + orderObject.getString("orderSize") + "件");
								orderStatusTextView.setText(orderObject.getString("orderStatus"));
								orderValueTextView.setText("￥" + orderObject.getString("orderValue"));
								orderPayTextView.setText(orderObject.getString("orderPay"));
								orderTimeTextView.setText(orderObject.getString("time"));
								if (orderObject.getString("payLink").equals("YES")) {
									orderButton.setText("去支付");
									orderButton.setTag(orderObject.getString("orderId"));
								}else if (orderObject.getString("previewLink").equals("YES")) {
									orderButton.setText("去评价");
									orderButton.setTag(orderObject.getString("orderId"));
								}else {
									orderButton.setVisibility(View.GONE);
								}

								//获取订单的商品图片URL；
								JSONArray allImages = new JSONArray(orderObject.get("productImages").toString());
								ArrayList<String> allImagesUrl = new ArrayList<String>();
								for (int j = 0; j < allImages.length(); j++) {
									allImagesUrl.add(allImages.get(j).toString());
									ImageView iv = new ImageView(mContext);
									LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
									lp.setMargins(2, 1, 2, 1);
									iv.setLayoutParams(lp);
									imageLoader.DisplayImage(allImages.get(j).toString(), iv);
									imagesContainer.addView(iv);
								}
								
				        			accountOrderView.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// TODO 跳转到订单详情页
											String orderId = ((TextView)(v.findViewById(R.id.account_order_id))).getText().toString();
											Log.d("点击订单：", orderId);
											Intent intent = new Intent();
											intent.putExtra("orderId", orderId);
											intent.setClass(mContext, OrderActivity.class);
											mContext.startActivity(intent);
											
											
										}
									});
				        			
								mybaiyang.addView(accountOrderView);
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
//						反射机制的Exception
//						catch (NoSuchFieldException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IllegalArgumentException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IllegalAccessException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}
				});
			}
		}).start();
		
		wishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 我的收藏
				Log.d("我的百洋", "我的收藏");
				PatchByLoginStatus patch = new PatchByLoginStatus(mContext, WishActivity.class);
				patch.patch();
				
			}
		});
		
		addressButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("我的百洋", "我的收获地址");
//				Intent intent = new Intent(mContext, ReceiverActivity.class);
//				startActivity(intent);
				PatchByLoginStatus patch = new PatchByLoginStatus(mContext, ReceiverActivity.class);
				patch.patch();
			}
		});

		couponButton.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO 我的优惠券
				Log.d("我的百洋", "我的优惠券");
				PatchByLoginStatus patch = new PatchByLoginStatus(mContext, CouponActivity.class);
				patch.patch();
			}
		});
	}
}
