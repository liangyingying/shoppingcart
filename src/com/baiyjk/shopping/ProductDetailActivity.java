package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baiyjk.shopping.R.id;
import com.baiyjk.shopping.adapter.ProductDetaiViewPagerAdapter;
import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.model.Cart;
import com.baiyjk.shopping.sqlite.MySqLiteHelper;

public class ProductDetailActivity extends Activity{
	private String productUrl;
	private String productDetailJson;
	private final String TAG = "product detail page";
	private TextView titleTextView;
	private TextView nameTextView;
	private TextView nameDescTextView;
	private TextView priceTextView;
	private Button addToCartButton;
	private Button addToWishButton;
	private Button shareButton;
	
	private Context mContext;
	private LinearLayout dotContainer;
	private View dot;
	private int currentIndex = 0;
	private ArrayList<View> dotsList = new ArrayList<View>();
	private int productId;
	private TextView myCartNumber;
	private int userId = 999;//未登录用户默认ID
	private Cart myCart;
	private JSONObject retJsonObject;//加入购物车response
	private final int MSG_SUCCESS = 1;
	private final int MSG_FAILURE = 0;
	private ImageView backImage;
	private ImageView cartImage;
	private TextView marketpriceTextView;
	private View infoView;
	private View commentsView;
	private TextView brandView;
	private TextView specView;
	private TextView manufacturerView;
	private TextView medicinetypeView;
	private TextView prodcodeView;
	private TextView unitView;
	private TextView newfromdateView;
	private TextView descTextView;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_detail);
		
		Intent intent = getIntent();
		productUrl = intent.getStringExtra("url");
		String regex = "\\d+";//获取商品Id
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(productUrl);
		while(m.find()){
			productId = Integer.parseInt(m.group());
		}
		Log.d(TAG, "" + productId);
		mContext = this;
		initView();
//		
//		FragmentManager fragmentManager = getFragmentManager();
//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		BottomFragment fragment = new BottomFragment();
//		fragmentTransaction.add(R.id.fragment_container, fragment);
//		fragmentTransaction.commit();
	}

	private void initView(){
		dotContainer = (LinearLayout)findViewById(R.id.product_image_dot_container);
		dot = (View)findViewById(R.id.dot);
		
		cartImage = (ImageView)findViewById(R.id.product_detail_cart);
		titleTextView = (TextView)findViewById(R.id.product_detail_titlebar);
		
		nameTextView = (TextView)findViewById(R.id.product_detail_name);
		nameDescTextView = (TextView)findViewById(R.id.product_detail_name_desc);
		descTextView = (TextView)findViewById(R.id.product_detail_desc);
		priceTextView = (TextView)findViewById(R.id.product_detail_price);
		marketpriceTextView = (TextView)findViewById(R.id.product_detail_market_price);
		brandView = (TextView)findViewById(R.id.product_detail_brand);
		specView = (TextView)findViewById(R.id.product_detail_spec);
		manufacturerView = (TextView)findViewById(R.id.product_detail_manufacturer);
		medicinetypeView = (TextView)findViewById(R.id.product_detail_medicinetype);
		prodcodeView = (TextView)findViewById(R.id.product_detail_prodcode);
		unitView = (TextView)findViewById(R.id.product_detail_unit);
//		newfromdateView = (TextView)findViewById(R.id.product_detail_newfromdate);
		
//		infoView = findViewById(R.id.product_detail_info);//查看商品详情
//		commentsView = findViewById(R.id.product_detail_comments);//查看用户评价
		
		addToCartButton = (Button)findViewById(R.id.add_to_cart);
//		addToWishButton = (Button)findViewById(R.id.add_to_wish);
//		shareButton = (Button)findViewById(R.id.product_detail_share);
		backImage = (ImageView)findViewById(R.id.product_detail_back);
		
		backImage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//查看购物车 
		cartImage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				intent.putExtra("url", ((TextView)(v.findViewById(R.id.list_product_url))).getText().toString());
				intent.setClass(mContext, ShoppingcartActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		myCartNumber = (TextView)findViewById(R.id.product_detail_cart_number);
		//头部显示购物车商品数量。如果是已登录用户，更新UserId；
//		myCart = new Cart(userId);
//		myCart.setDbHelper(new MySqLiteHelper(mContext));
//		int size = myCart.getCartSize(userId);
//		if (size > 0)
//			myCartNumber.setText("" + size);
		
//		myCart = myCart.getCart(userId);
//		Log.d(TAG, "用户Id：" + myCart.getUserId());
//		Log.d(TAG, "商品：" + myCart.getProducts());
		
//		productImageContainer = (LinearLayout)findViewById(R.id.product_image_container);
		
		new Thread(new Runnable() {
		            
	        @Override  
	        public void run() {
		        	productDetailJson = HttpFactory.getHttp().getRequest("/" + productUrl + "/?format=true", mContext);
//		        	productDetailJson = HttpFactory.getHttp().getUrlContext("/");
//		        	Log.d(TAG, productDetailJson);
                runOnUiThread(new Runnable() {                    
                    private ViewPager mViewPager;
					private ProductDetaiViewPagerAdapter mViewPagerAdapter;

					@Override  
                    public void run() { 
						Log.d(TAG, "in run");
                    		JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(productDetailJson);
								titleTextView.setText(jsonObject.get("name").toString());
								nameTextView.setText(jsonObject.get("name").toString());
								nameDescTextView.setText(jsonObject.get("name_desc").toString());
								descTextView.setText(jsonObject.getString("short_description"));
								
								priceTextView.setText(jsonObject.get("price").toString());
								marketpriceTextView.setText(jsonObject.getString("market_price"));
								marketpriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );//加中划线
								brandView.setText(jsonObject.getString("brand_name"));
								specView.setText(jsonObject.getString("spec"));
								unitView.setText(jsonObject.getString("unit"));
								manufacturerView.setText(jsonObject.getString("manufacturer"));
								medicinetypeView.setText(jsonObject.getString("medicinetype"));
								prodcodeView.setText(jsonObject.getString("prod_code"));
								
								
								//获取商品的多张图片URL；
								JSONArray allImages = new JSONArray(jsonObject.get("allImageUrl").toString());
								ArrayList<String> allImagesUrl = new ArrayList<String>();
								for (int i = 0; i < allImages.length(); i++) {
									JSONObject imageJsonObject = new JSONObject(allImages.get(i).toString());
									allImagesUrl.add(imageJsonObject.getString("mini_url"));
								}
								
								//将图片URL转换成对应的ImageView	
								mViewPager = (ViewPager)findViewById(R.id.product_detail_images_viewpager);  
						        mViewPagerAdapter = new ProductDetaiViewPagerAdapter(mContext, allImagesUrl);  
						        mViewPager.setAdapter(mViewPagerAdapter);
						        
						        //图片下方的圆点，切换图片对应的圆点高亮
						        LayoutParams lp = dot.getLayoutParams();
						        dotsList.add(dot);//xml文件中高亮的那个
						        for (int i = 0; i < allImages.length() - 1; i++) {
									View dotItem = new View(mContext, null);
									dotItem.setLayoutParams(lp);
									dotItem.setBackgroundResource(R.drawable.dot_normal);

									dotContainer.addView(dotItem);
									dotsList.add(dotItem);
								}
						        
						        //viewPager的图片滑动时，对应的dot要高亮（不同图片）
						        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

									@Override
									public void onPageScrollStateChanged(
											int arg0) {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onPageScrolled(int arg0,
											float arg1, int arg2) {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onPageSelected(int position) {
										// TODO Auto-generated method stub
										dotsList.get(currentIndex).setBackgroundResource(R.drawable.dot_normal);
										dotsList.get(position).setBackgroundResource(R.drawable.dot_focused);
										currentIndex = position;
									}});
								 
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                    		
                    }
		        });
	        }
		}).start();
		
		//加入购物车。用户ID暂时用999。数量为1。
		addToCartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
//				int userId = 999;
				int productNumber = 1;
				
//				Cart cartItem = new Cart(userId, productId, productNumber);
//				cartItem.setDbHelper(new MySqLiteHelper(context));
				myCart.setProductId(productId);
				myCart.setProductNumber(productNumber);
				int cartSize = myCart.addToCart(myCart);

				Log.d(TAG, "" + cartSize);
				Toast.makeText(context,"成功放入购物车", Toast.LENGTH_SHORT).show();
				if (cartSize > 0) {
					myCartNumber.setText("" + cartSize);
					//and 设置带气泡背景图
				}
				
				*/
				
				
				//以上是保存到本地数据库，下面是通过cookie保存到服务器端
				new Thread(new Runnable() {
					@Override  
		            public void run() {
		            		String addCartUrl = "/addCart.do?qty=1&flag=false&productId=" + productId;
						
						//url = "/ajax/getCart.do" 包括content,qty两个字段；
						String ret = HttpFactory.getHttp().getRequest(addCartUrl, mContext);
						Log.d(TAG, ret);
						
						try {
							retJsonObject = new JSONObject(ret);
//							Log.d(TAG, retJsonObject.getString("qty"));//购物车商品数量
//							Log.d(TAG, retJsonObject.getString("amount"));//购物车合计金额
//							mHandler.obtainMessage(MSG_SUCCESS,bm).sendToTarget();
							
							myCartNumber.post(new Runnable() {//另外一种更简洁的发送消息给ui线程的方法。  
				                  
				                @Override  
				                public void run() {//run()方法会在ui线程执行  
				                		try {
											myCartNumber.setText("" + retJsonObject.getString("qty"));
											Toast.makeText(mContext, "成功放入购物车", Toast.LENGTH_SHORT).show();
											//and 设置带气泡背景图
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
				                }  
				            });
							//以上通过post方式调用隐式handler
							//下面方法调用new Handler()方式。亦可。
//							mHandler.obtainMessage(MSG_SUCCESS, "" + retJsonObject.getString("qty")).sendToTarget();
//							Toast.makeText(context, "成功放入购物车", Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
				}).start();
				
				
			}
		});
		
		addToWishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToWish();
			}
		});
		
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share();
			}
		});
	}
	
	/**
	 * 商品加入收藏
	 * @return
	 */
	protected boolean addToWish(){
		return false;
	}
	
	/**
	 * 分享商品
	 * 分享内容链接已经能取到，商品名和简要描述木有传过来
	 * 只能分享到手机已经安装的应用。
	 * 如果没有安装微博也能分享，需要申请key
	 */
	protected void share(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");  //分享的数据类型  
		intent.putExtra(Intent.EXTRA_SUBJECT, "subject");  //主题  
		intent.putExtra(Intent.EXTRA_TEXT,  "content");  //内容 
		intent.putExtra(Intent.EXTRA_STREAM, "此处放图片URI，全路径");
		startActivity(Intent.createChooser(intent, "title"));  //目标应用选择对话框的标题  
	}
	
	private Handler mHandler = new Handler() {  
        

		public void handleMessage (Message msg) {//此方法在ui线程运行  
            switch(msg.what) {
            case MSG_SUCCESS:  
				myCartNumber.setText(msg.obj.toString());  
                break;  
  
            case MSG_FAILURE :  
                Toast.makeText(mContext, "放入购物车失败", Toast.LENGTH_SHORT).show();  
                break;  
            }  
        }  
    };

	/*@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.product_detail_comments) {
			Intent intent = new Intent(mContext, ProductCommentActivity.class);
			String url = "商品评价独立请求";
			intent.putExtra("url", url);
			startActivity(intent);
		}
		
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
