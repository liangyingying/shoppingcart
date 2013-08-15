package com.baiyjk.shopping.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baiyjk.shopping.EditReceiverActivity;
import com.baiyjk.shopping.R;
import com.baiyjk.shopping.http.HttpFactory;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReceiversAdapter extends SimpleAdapter{

	private LayoutInflater layoutinflater;
	private int mResource;
	private Context mContext;
	private List<Map<String, String>> mData;

	public ReceiversAdapter(Context context,
			List<Map<String, String>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.layoutinflater = LayoutInflater.from(context);
		this.mResource = resource;
		this.mContext = context;
		this.mData = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		convertView = layoutinflater.inflate(this.mResource, null);
		View v = super.getView(position, convertView, parent);
//		TextView addIdView = (TextView)convertView.findViewById(R.id.receiver_item_id);
		Map<String, String> item = this.mData.get(position);
		View editView = convertView.findViewById(R.id.receiver_item_edit);
//		int addId = Integer.parseInt(addIdView.getText().toString());
		String addId = item.get("addId");
		Map<String, Integer> tag = new HashMap<String, Integer>();
		tag.put("addId", Integer.parseInt(addId));
		tag.put("position", Integer.valueOf(position));
		convertView.setTag(tag);
		
		editView.setTag(item);
		//默认地址画勾，去掉此功能，因为设置的默认地址并不作为订单的第一地址。仅标识当前正使用/要使用的地址。
		ImageView isUsed = (ImageView)convertView.findViewById(R.id.receiver_item_default);
//		if (mData.get(position).get("defaultAddress").equals("1")) {
//			isUsed.setVisibility(View.VISIBLE);
//		}
//		Log.d("正在使用ID：", item.get("usedAddressId"));
		if (item.get("usedAddressId").equals(addId)) {
			isUsed.setVisibility(View.VISIBLE);
		}
		
		//点击整条，选中为默认地址
		/*
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 设为默认地址， /ajax/setDefaultAddr.do?addressId=?
				Map<String, Integer> tag = (Map<String, Integer>)v.getTag();				
				int addId = tag.get("addId");
				int position = tag.get("position");
				
				Log.d("设置为默认地址：", tag.get("addId").toString());
				for (int i = 0; i < mData.size(); i++) {
					Map<String, String> map = mData.get(i);
					if (i == position && map.get("defaultAddress").equals("0")){				
						map.put("defaultAddress", "1");
					}else if (i != position && map.get("defaultAddress").equals("1")) {
						map.put("defaultAddress", "0");
					} 					
				}
				notifyDataSetChanged();
			}
		});
		*/
		//点击编辑，编辑该条
		editView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HashMap<String, String> tag = (HashMap<String, String>)arg0.getTag();
				Intent intent = new Intent();
				intent.setClass(mContext, EditReceiverActivity.class);
				intent.putExtra("receiverMap", tag);
				mContext.startActivity(intent);
			}
		});
		
		return v;
	}
}
