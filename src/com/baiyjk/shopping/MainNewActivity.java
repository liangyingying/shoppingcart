package com.baiyjk.shopping;

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

public class MainNewActivity extends FragmentActivity{
	private Fragment[] mFragments;  
    private RadioGroup mRadioGroup;  
    private FragmentManager mFragmentManager;  
    private FragmentTransaction mFragmentTransaction;  
//    private RadioButton mHomeButton, mCategoryButton, mAccountButton, mCartButton;
    
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.main_fragment);  
        mFragments = new Fragment[4];  
        mFragmentManager = getSupportFragmentManager();  
        mFragments[0] = mFragmentManager.findFragmentById(R.id.fragement_main);  
        mFragments[1] = mFragmentManager.findFragmentById(R.id.fragement_category);  
        mFragments[2] = mFragmentManager.findFragmentById(R.id.fragement_account);
        mFragments[3] = mFragmentManager.findFragmentById(R.id.fragement_cart);
        
        mFragmentTransaction = mFragmentManager.beginTransaction()  
                .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);  
        mFragmentTransaction.show(mFragments[0]).commit();  
        setFragmentIndicator();  
    }  
  
    private void setFragmentIndicator() {  
  
        mRadioGroup = (RadioGroup) findViewById(R.id.tab_group);  
//        mHomeButton = (RadioButton) findViewById(R.id.main_tab_1);  
//        mCategoryButton = (RadioButton) findViewById(R.id.main_tab_2);  
//        mAccountButton = (RadioButton) findViewById(R.id.main_tab_3); 
//        mCartButton = (RadioButton)findViewById(R.id.main_tab_4);
  
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
  
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                mFragmentTransaction = mFragmentManager.beginTransaction()  
                        .hide(mFragments[0]).hide(mFragments[1])  
                        .hide(mFragments[2]).hide(mFragments[3]);
                switch (checkedId) {  
                case R.id.main_tab_1:  
                    mFragmentTransaction.show(mFragments[0]).commit(); 
                    Log.d("on click", "首页");
                    break;  
  
                case R.id.main_tab_2:  
                    mFragmentTransaction.show(mFragments[1]).commit();
                    Log.d("on click", "分类");
                    break;  
  
                case R.id.main_tab_3:  
                    mFragmentTransaction.show(mFragments[2]).commit();
                    Log.d("on click", "我的百洋");
                    break;  
  
                case R.id.main_tab_4:
                		mFragmentTransaction.show(mFragments[3]).commit();
                		Log.d("on click", "购物车");
                		break;
                default:  
                    break;  
                }  
            }  
        });  
    }
}
