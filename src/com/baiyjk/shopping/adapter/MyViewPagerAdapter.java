package com.baiyjk.shopping.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.ActpageActivity;
import com.baiyjk.shopping.ViewPagerHomeAdsItem;
import com.baiyjk.shopping.utils.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 首页广告导航图的适配器
 * @author lyy
 *
 */
public class MyViewPagerAdapter extends PagerAdapter{

    private Context mContext;  
    private ArrayList<JSONObject> images;
//    private String mTop3ActpageInfo;
//    private HashMap<Integer, ViewPagerHomeAdsItem> mHashMap;  
      
    public MyViewPagerAdapter(Context context, ArrayList<JSONObject> images) {  
        this.mContext = context;  
        this.images = images;
//        mTop3ActpageInfo = top3ActpageInfo;
//        mHashMap = new HashMap<Integer, ViewPagerHomeAdsItem>();
    }   
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.images.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object; 
	}
	@Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
		container.removeView(container.getChildAt(position)); 
    } 
	
    @Override 
    public Object instantiateItem(ViewGroup container, int position){
    	ImageLoader imageLoader = new ImageLoader(mContext);
    	ImageView imageView = new ImageView(mContext);
    	try {
			imageLoader.DisplayImage(images.get(position).getString("imageLink"), imageView);
			imageView.setTag(images.get(position).getString("imageUrl"));
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.e("ads click", "ads click+");
					Intent intent = new Intent();
					intent.setClass(mContext, ActpageActivity.class);
					intent.putExtra("url", v.getTag().toString());
					mContext.startActivity(intent);
				}
			});
//		    	imageView.setBackground(background);
//			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	container.addView(imageView);
    		
    	return container.getChildAt(position);
    }

}
