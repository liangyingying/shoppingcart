package com.baiyjk.shopping;
/**
 * TODO 后台需提供搜索和前三条专题的手机端功能。
 */
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.adapter.MyViewPagerAdapter;
import com.baiyjk.shopping.http.HttpApi;
import com.baiyjk.shopping.http.HttpFactory;

import android.R.string;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class HomeActivity extends Activity {

	private ViewPager mViewPager;   
    private MyViewPagerAdapter mViewPagerAdapter;   
    private int[] imageIds;
//    private string[] images;
    private ArrayList<JSONObject> images;
    private ArrayList<View> dots;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //
    private int oldPosition = 0;//
	private EditText searchEditText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		setupViews(); 
		
		//搜索输入框，/search.do?keyword=**&sort=*&page=*
		//TODO 部分手机不支持imeOptions,So,还需要添加按钮！
		searchEditText = (EditText)findViewById(R.id.keyword);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					Log.d("home page", "search");
					Intent intent = new Intent();
					intent.putExtra("url", "/search.do?keyword=" + v.getText());
					startActivity(intent);
					
				}
				return false;
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	protected void onStart(){
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	/**
	 * 图片轮播。
	 * 选取调品专题页前3个。
	 * 下载图片成功前显示默认图片。
	 * 
	 */
	private void setupViews(){
		this.images = new ArrayList<JSONObject>();
		this.dots = new ArrayList<View>();
//		this.imageIds = new int[]
//		{
//				R.drawable.viewpager_1,
//				R.drawable.viewpager_2,
//				R.drawable.viewpager_3
//		};
		
		String top3ActpageUrl = "/top3Actpage?format=true";
		String top3ActpageInfo = HttpFactory.getHttp().getRequest(top3ActpageUrl, this);
		try {
			JSONArray jsonArray = new JSONArray(top3ActpageInfo);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				this.images.add(jsonObject);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		for(int i = 0; i < this.imageIds.length; i++){
//			ImageView imageView = new ImageView(this);
//			imageView.setBackgroundResource(this.imageIds[i]);
//			this.images.add(imageView);
//		}
		this.dots.add(findViewById(R.id.dot_0));
		this.dots.add(findViewById(R.id.dot_1));
		this.dots.add(findViewById(R.id.dot_2));
		
        mViewPager = (ViewPager)findViewById(R.id.homeAdViewPager);  
        mViewPagerAdapter = new MyViewPagerAdapter(this, images);  
        mViewPager.setAdapter(mViewPagerAdapter);
        
        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_unselected);
				dots.get(position).setBackgroundResource(R.drawable.dot_selected);
				
				oldPosition = position;
				currentItem = position;
			}
        	
        });
    } 
	
	private class ViewPagerTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			currentItem = (currentItem + 1) % imageIds.length;
			handler.obtainMessage().sendToTarget();
		}
		
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			mViewPager.setCurrentItem(currentItem);
		}
	};
}
