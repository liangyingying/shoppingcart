package com.baiyjk.shopping.fragment;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.baiyjk.shopping.R;
import com.baiyjk.shopping.adapter.MyViewPagerAdapter;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class HomeFragment extends Fragment {
	private ViewPager mViewPager;   
    private MyViewPagerAdapter mViewPagerAdapter;   
    private int[] imageIds;
    private ArrayList<ImageView> images;
    private ArrayList<View> dots;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //
    private int oldPosition = 0;//
//    private tab1BroadcastReceiver receiver;
    private IntentFilter intentFilter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);
        this.imageIds = new int[]{
				R.drawable.viewpager_1,
				R.drawable.viewpager_2,
				R.drawable.viewpager_3
		};
		this.images = new ArrayList<ImageView>();
		this.dots = new ArrayList<View>();
		for(int i = 0; i < this.imageIds.length; i++){
			ImageView imageView = new ImageView(v.getContext());
			imageView.setBackgroundResource(this.imageIds[i]);
			this.images.add(imageView);
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
