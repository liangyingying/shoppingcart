package com.baiyjk.shopping.fragment;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.R;
import com.baiyjk.shopping.adapter.MyViewPagerAdapter;
import com.baiyjk.shopping.http.HttpFactory;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class HomeFragment extends Fragment {
	private ViewPager mViewPager;   
    private MyViewPagerAdapter mViewPagerAdapter;   
    private int[] imageIds;
    private ArrayList<JSONObject> images;
    private ArrayList<View> dots;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //
    private int oldPosition = 0;//
//    private tab1BroadcastReceiver receiver;
    private IntentFilter intentFilter;
	private EditText searchEditText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);
        
        //搜索输入框，/search.do?keyword=**&sort=*&page=*
  		//TODO 部分手机不支持imeOptions,So,还需要添加按钮！
  		searchEditText = (EditText)v.findViewById(R.id.keyword);
  		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
  			
  			@Override
  			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
  				// TODO Auto-generated method stub
  				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
  					Log.d("home page", "search");
  					Intent intent = new Intent();
  					intent.putExtra("url", "/search.do?format=true&keyword=" + v.getText());
  					startActivity(intent);
  					
  				}
  				return false;
  			}
  		});
  		
//        this.imageIds = new int[]{
//				R.drawable.viewpager_1,
//				R.drawable.viewpager_2,
//				R.drawable.viewpager_3
//		};
        this.images = new ArrayList<JSONObject>();
//		this.images = new ArrayList<ImageView>();
		this.dots = new ArrayList<View>();
		String top3ActpageUrl = "/top3Actpage?format=true";
		String top3ActpageInfo = HttpFactory.getHttp().getRequest(top3ActpageUrl, v.getContext());
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
		
		this.dots.add(v.findViewById(R.id.dot_0));
		this.dots.add(v.findViewById(R.id.dot_1));
		this.dots.add(v.findViewById(R.id.dot_2));
		
        mViewPager = (ViewPager)v.findViewById(R.id.homeAdViewPager);  
        mViewPagerAdapter = new MyViewPagerAdapter(v.getContext(), images);  
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

        return v;
    }

    @Override
    public void onPause() {

        super.onPause();
    }
    
    @Override
	public void onStart(){
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
	}
	
	@Override
	public void onStop(){
		super.onStop();
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
