package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.adapter.CartListSimpleAdapter;
import com.baiyjk.shopping.adapter.ReceiversAdapter;
import com.baiyjk.shopping.http.HttpFactory;

import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

// /myaddress.do?format=true
// /ajax/addAddr.do POST resposne="OK" or "NO"
public class ReceiverActivity extends ListActivity {
	private Context mContext;
	private List<Map<String, String>> list;
	private TextView loadingView;
	private View backView;
	private View addAddressView;
	private ListView mListView;
	private String mFromActivity;
	private String mOldAddressId;//原来地址
	private Intent mIntent;
	private String newAddressId = null;//新设置的地址
	private int mPosition;
	private Map<String, String> activityResultMap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receivers);
		
		//如果是来自订单确认页面
		Intent intent = getIntent();
		mFromActivity = intent.getStringExtra("fromActivity");
		mOldAddressId = intent.getStringExtra("oldAddressId");
		
		mContext = this;
		loadingView = (TextView)findViewById(R.id.receivers_loading);
		
		backView = findViewById(R.id.receivers_back);
		addAddressView = findViewById(R.id.receivers_add);
		mListView = this.getListView();
		
		String urlString = "/myaddress.do?format=true";
		GetReceiversTask task = new GetReceiversTask();
		task.execute(urlString);
		
		//新增收货人按钮
		addAddressView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, EditReceiverActivity.class);
				mContext.startActivity(intent);	
//				startActivityForResult(intent, 0);
			}
		});
		
		//点击每条信息，设置默认地址
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				v.findViewById(R.id.receiver_item_default).setVisibility(View.VISIBLE);
				Map<String, String> address = list.get(position);				
				String addId = address.get("addId");
				
				Log.d("我要使用地址：", addId + ":" + position + ":" + address.get("defaultAddress"));
				for (int i = 0; i < list.size(); i++) {
					Map<String, String> map = list.get(i);
					Log.d("notifydatachage:", i + ":" + position + ":" + map.get("defaultAddress"));
					
//					Log.d("test2", (map.get("defaultAddress").equals("0")) + "");
//					Log.d("test3", (map.get("defaultAddress").equals("null")) + "");
					if (i == position){				
//						map.put("defaultAddress", "1");
//						Log.d("change->", map.get("addId"));
//						mPosition = i;//标记activityResult中将要修改的值
						newAddressId = map.get("addId");		
					}
					map.put("usedAddressId", addId);
				}
				activityResultMap = address;
				BaseAdapter adapter = (BaseAdapter)mListView.getAdapter();
				adapter.notifyDataSetChanged();
			}
		});		
	}
	
    
    @Override
    public void onBackPressed (){
    		Log.d("地址列表", "你按了后退键");
//		for (int i = 0; i < list.size(); i++) {
//			Map<String, String> map = list.get(i);
//			if (map.get("defaultAddress").equals("1")) 						
//				newDefaultAddressId = map.get("addId");				
//		}
		//后退时，如果更换了地址Id ,不再设置成默认地址，仅更新订单的地址信息  		
		if (newAddressId != null && !newAddressId.equals(mOldAddressId)) {
//			String url = "/ajax/setDefaultAddr.do?addressId=".concat(newAddressId);
//			SetDefaultAddressTask task = new SetDefaultAddressTask();
//			task.execute(url);
			
			if (mFromActivity.equals("OrderConfirmActivity")) {
				mIntent = new Intent();
		        mIntent.putExtra("result", (HashMap<String, String>)activityResultMap);
//		        mIntent.putExtra("index", mPosition);
				this.setResult(0, mIntent);
			}
		}
		super.onBackPressed();
    }

	class GetReceiversTask extends AsyncTask<String, Integer, Boolean>{
		private String response;
		private Map<String, String> map;
		
		@Override
		protected Boolean doInBackground(String... params) {
			response = HttpFactory.getHttp().getRequest(params[0], mContext);
			JSONArray responseArray;
			try {
				responseArray = new JSONArray(response);
				int length = responseArray.length();
				if ( length > 0) {
					list = new ArrayList<Map<String, String>>();					
					for (int i = 0; i < length; i++) {
						JSONObject addressObj = responseArray.getJSONObject(i);
						map = new HashMap<String, String>();
						String area = addressObj.getString("hanprovice")
								.concat(addressObj.getString("hancity"))
								.concat(addressObj.getString("hanarea"));
						String info = addressObj.getString("address_info");
						String address = area + info;
						
						map.put("addId", addressObj.getString("addid"));
						map.put("name", addressObj.getString("receiver_name"));
						map.put("phone", addressObj.getString("telephone"));						
						map.put("address", address);
						map.put("email", addressObj.getString("email2"));
						map.put("post", addressObj.getString("post"));
//						map.put("area", area);
						map.put("info", info);
						map.put("defaultAddress", addressObj.getString("default_addr"));
						map.put("hanarea", addressObj.getString("hanarea"));
						map.put("hancity", addressObj.getString("hancity"));
						map.put("hanprovince", addressObj.getString("hanprovice"));
						map.put("codearea", addressObj.getString("address_areaid"));
						map.put("codecity", addressObj.getString("address_cityid"));
						map.put("codeprovince", addressObj.getString("address_districtid"));
						map.put("usedAddressId", mOldAddressId);
						list.add(map);
					}
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
				loadingView.setVisibility(View.GONE);
				ReceiversAdapter adapter = new ReceiversAdapter(
						mContext, list,
						R.layout.receiver_item, new String[] {"addId", "name", "phone", "address" }, 
							new int[] {
								R.id.receiver_item_id,
								R.id.receiver_item_name,
								R.id.receiver_item_phone,
								R.id.receiver_item_address});
				
				setListAdapter(adapter);
			}else{
				loadingView.setText("还没有添加任何收货人信息");
			}
		}
		
	}
	
	class SetDefaultAddressTask extends AsyncTask<String, Integer, Boolean>{
		
		@Override
		protected Boolean doInBackground(String... params) {
			String response = HttpFactory.getHttp().getRequest(params[0].toString(), mContext);
			return response.equals("OK") ? true : false;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			if (result) {
				//TODO 设置成功，画勾，同时取消别的勾
//				view.findViewById(R.id.receiver_item_default).setVisibility(View.VISIBLE);
			}
		}
		
	}
	
}
