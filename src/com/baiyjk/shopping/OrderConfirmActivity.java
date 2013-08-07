package com.baiyjk.shopping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class OrderConfirmActivity extends Activity{
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		
		mContext = this;
		initView();
	}
	
	private void initView(){
		
	}
}
