package com.baiyjk.shopping.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.baiyjk.shopping.LoginActivity;
import com.baiyjk.shopping.http.HttpFactory;

public class PatchByLoginStatus extends AsyncTask<String, Integer, Boolean>{
//	private String response;
	private Context mContext;
	private Intent intent;
	private final String url = "/ajax/getLoginStatus.do";
	private Class mToActivity;
	private JSONObject loginStatus;
	
	public PatchByLoginStatus(Context context, Class toActivity) {
		super();
		mContext = context;
		mToActivity = toActivity;
	}
	
	public void patch(){
		execute(url);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		
		String response = HttpFactory.getHttp().getRequest(url, mContext);
		try {
			loginStatus = new JSONObject(response);
			if (loginStatus.getString("loginstatus").equals("OK")) {
				return true;
			}else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result){
		if (result) {
			Log.d("登录状态", "已登录");
			intent = new Intent();
			intent.setClass(mContext, mToActivity);
			mContext.startActivity(intent);
			
		}else {
			Log.d("登录状态", "没登录！！");
			intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			intent.putExtra("toActivity", mToActivity.getName());
			mContext.startActivity(intent);
			
		}
	}
	
}
