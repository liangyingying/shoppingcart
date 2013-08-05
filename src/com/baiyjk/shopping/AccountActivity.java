package com.baiyjk.shopping;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.adapter.ProductDetaiViewPagerAdapter;
import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我的百洋
 * @author lyy
 *
 */
public class AccountActivity extends Activity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		
		mContext = this;
		initView();
	}
	
	private void initView(){
		this.mLayoutinflater = LayoutInflater.from(mContext);
		accountImageView = (ImageView)findViewById(R.id.account_image);
		usernameTextView = (TextView)findViewById(R.id.account_name_grade);
		
		pointTextView = (TextView)findViewById(R.id.account_point);
		moneyTextView = (TextView)findViewById(R.id.account_money);
		
//		accountOrderView = findViewById(R.layout.order_item);
		mybaiyang = (LinearLayout)findViewById(R.id.mybaiyang);
		
		new Thread(new Runnable() {
			
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = "/mybaiyang.do?format=true";
				mResponse = HttpFactory.getHttp().getUrlContext(url, mContext);
				runOnUiThread(new Runnable() {                    
                   
					@Override  
                    public void run() {
						Log.d("我的百洋", mResponse);
						try {
							JSONObject jsonObject = new JSONObject(mResponse);
							usernameTextView.setText(jsonObject.getString("username") + "  等级：" 
									+ jsonObject.getString("userGrade"));
							pointTextView.setText("积分：" + jsonObject.getString("userPoint"));
//							moneyTextView
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
								ViewPager viewPager = (ViewPager)accountOrderView.findViewById(R.id.account_product_images_viewpager);
								
								orderIdTextView.setText("订单号：" + orderObject.getString("orderId"));
								orderSizeTextView.setText("共" + orderObject.getString("orderSize") + "件");
								orderStatusTextView.setText(orderObject.getString("orderStatus"));
								orderValueTextView.setText("￥" + orderObject.getString("orderValue"));
								orderPayTextView.setText(orderObject.getString("orderPay"));
								orderTimeTextView.setText(orderObject.getString("time"));
								if (orderObject.getString("payLink").equals("YES")) {
									orderButton.setText("去支付");
									orderButton.setTag(orderObject.getString("orderId"));
								}else if (orderObject.getString("previewLink").equals("YES")) {
									orderButton.setText(orderObject.getString("去评价"));
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
//									iv.setBackgroundResource(R.drawable.y);
									imageLoader.DisplayImage(allImages.get(j).toString(), iv);
									viewPager.addView(iv);
								}
								//将图片URL转换成对应的ImageView  
//						        mViewPagerAdapter = new ProductDetaiViewPagerAdapter(mContext, allImagesUrl);  
//						        viewPager.setAdapter(mViewPagerAdapter);
								
				        			
								
								mybaiyang.addView(accountOrderView);
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}).start();
	}
	
}
