package com.baiyjk.shopping.http;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

public interface HttpApi {
	public String getRequest(String strUrl, Context context);
	public String post(String url, Context context, List <NameValuePair> params);
}
