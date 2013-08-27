package com.baiyjk.shopping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.baiyjk.shopping.fragment.AccountFragment;
import com.baiyjk.shopping.fragment.CategoryFragment;
import com.baiyjk.shopping.fragment.HomeFragment;
import com.baiyjk.shopping.fragment.ShoppingcartFragment;
import com.baiyjk.shopping.sqlite.MySqLiteHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainNewActivity extends FragmentActivity{
//	private Fragment[] mFragments;  
    private RadioGroup mRadioGroup;  
    private FragmentManager mFragmentManager;  
    private FragmentTransaction mFragmentTransaction;  
//    private RadioButton mHomeButton, mCategoryButton, mAccountButton, mCartButton;
	private Fragment mFragment;
	private Fragment currentFragment;
	private SQLiteOpenHelper dbHelper;
	private Context mContext;
	private TextView titlebar;
    
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.main_fragment);
        
        titlebar = (TextView)findViewById(R.id.titlebar);
//        mFragments = new Fragment[4];
        mContext = this;
        mFragmentManager = getSupportFragmentManager(); 
        mFragment = mFragmentManager.findFragmentById(R.id.fragement_main);
        
//        mFragments[0] = mFragmentManager.findFragmentById(R.id.fragement_main);  
//        mFragments[1] = mFragmentManager.findFragmentById(R.id.fragement_category);  
//        mFragments[2] = mFragmentManager.findFragmentById(R.id.fragement_account);
//        mFragments[3] = mFragmentManager.findFragmentById(R.id.fragement_cart);
        
        mFragmentTransaction = mFragmentManager.beginTransaction();  
//                .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);  
        mFragmentTransaction.show(mFragment).commit(); 
        setFragmentIndicator(); 
        
//        createDatabase();
    }  
  
    

	private void setFragmentIndicator() {  
    		currentFragment = mFragment;
        mRadioGroup = (RadioGroup) findViewById(R.id.tab_group);  
//        mHomeButton = (RadioButton) findViewById(R.id.main_tab_1);  
//        mCategoryButton = (RadioButton) findViewById(R.id.main_tab_2);  
//        mAccountButton = (RadioButton) findViewById(R.id.main_tab_3); 
//        mCartButton = (RadioButton)findViewById(R.id.main_tab_4);
  
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
        		
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                mFragmentTransaction = mFragmentManager.beginTransaction(); 
//                        .hide(mFragments[0]).hide(mFragments[1])  
//                        .hide(mFragments[2]).hide(mFragments[3]);
                switch (checkedId) {  
                case R.id.main_tab_1:
                		mFragment = mFragmentManager.findFragmentByTag("home");
                		if (currentFragment != mFragment) {
                			mFragmentTransaction.hide(currentFragment).show(mFragment).commit();
                			currentFragment = mFragment;
                		} 
                		titlebar.setText("百洋健康网");
                    Log.d("on click", "首页");
                    break;  
  
                case R.id.main_tab_2:
                		String tag2 = "category";
                		mFragment = mFragmentManager.findFragmentByTag(tag2);
                		if (currentFragment != mFragment) {
						if (mFragment != null) {
							mFragmentTransaction.hide(currentFragment).show(mFragment).commit();
						}else {
							mFragment = new CategoryFragment();
							mFragmentTransaction.hide(currentFragment).add(R.id.fragement_container, mFragment, tag2).commit();     					
						}
						currentFragment = mFragment;
					}
                		titlebar.setText("全部分类");
                    Log.d("on click", "分类");
                    break;  
  
                case R.id.main_tab_3:
	                	String tag3 = "account";
	            		mFragment = mFragmentManager.findFragmentByTag(tag3);
	            		if (currentFragment != mFragment) {
						if (mFragment != null) {
							mFragmentTransaction.hide(currentFragment).show(mFragment).commit();
						}else {
							mFragment = new AccountFragment();
							mFragmentTransaction.hide(currentFragment).add(R.id.fragement_container, mFragment, tag3).commit();     					
						}
						currentFragment = mFragment;
					}
	            		titlebar.setText("我的白洋");
                    Log.d("on click", "我的百洋");
                    break;  
  
                case R.id.main_tab_4:
	                	String tag4 = "shoppingcart";
	            		mFragment = mFragmentManager.findFragmentByTag(tag4);
	            		if (currentFragment != mFragment) {
						if (mFragment != null) {
							mFragmentTransaction.hide(currentFragment).show(mFragment).commit();
						}else {
							mFragment = new ShoppingcartFragment();
							mFragmentTransaction.hide(currentFragment).add(R.id.fragement_container, mFragment, tag4).commit();     					
						}
						currentFragment = mFragment;
					}
	            		titlebar.setText("购物车");
                		Log.d("on click", "购物车");
                		break;
                default:  
                    break;  
                }  
            }  
        });  
    }
	
}
