package com.baiyjk.shopping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.Validate;

public class EditReceiverActivity extends Activity {
	private EditText nameEditText;
	private EditText addressEditText;
	private EditText postEditText;
	private EditText phoneEditText;
	private EditText emailEditText;
	private Button saveButton;
	private View backView;
	private TextView titleView;
	private Spinner provinceSpinner;
	private ArrayAdapter<CharSequence> provinceAdapter;
	private ArrayAdapter<CharSequence> cityAdapter;
	private ArrayAdapter<CharSequence> areaAdapter;
	private Spinner citySpinner;
	private Spinner areaSpinner;
	private Context mContext;
	private String addressId = "NO";//新增地址的情况，服务端通过"NO"来验证
	private Resources res;
	private String defaultProvince;
	private String defaultCity;
	private String defaultArea;
	private String provinceCode;
	private String cityCode;
	private String areaCode;
	

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_receiver);
		
		res = getResources();
		this.mContext = this;
		Intent intent = getIntent();
		HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("receiverMap");
		nameEditText = (EditText)findViewById(R.id.receiver_name_edittext);
		provinceSpinner = (Spinner)findViewById(R.id.receiver_province_spinner);
		citySpinner = (Spinner)findViewById(R.id.receiver_city_spinner);
		areaSpinner = (Spinner)findViewById(R.id.receiver_area_spinner);
		addressEditText = (EditText)findViewById(R.id.receiver_address_edittext);
		postEditText = (EditText)findViewById(R.id.receiver_post_edittext);
		phoneEditText = (EditText)findViewById(R.id.receiver_phone_edittext);
		emailEditText = (EditText)findViewById(R.id.receiver_email_edittext);
		
		saveButton = (Button)findViewById(R.id.receiver_save);
		backView = findViewById(R.id.receiver_edit_back);
		titleView = (TextView)findViewById(R.id.receiver_edit_titlebar);
		
		if (hashMap != null) {
			titleView.setText("编辑收货信息");
			this.addressId = hashMap.get("addId");
			nameEditText.setText(hashMap.get("name"));
			defaultProvince = hashMap.get("hanprovince");
			defaultCity = hashMap.get("hancity");
			defaultArea = hashMap.get("hanarea");
			addressEditText.setText(hashMap.get("info"));
			postEditText.setText(hashMap.get("post"));
			phoneEditText.setText(hashMap.get("phone"));
			emailEditText.setText(hashMap.get("email") == null ? hashMap.get("email") : "");
			provinceCode = hashMap.get("codeprovince");
			cityCode = hashMap.get("codecity");
			areaCode = hashMap.get("codearea");
			Log.d("email", hashMap.get("email"));
		}else {
			titleView.setText("新增收货信息");
		}
		
		//下拉列表选择地址-省/市	R.layout.address_spinner_item
		provinceAdapter = ArrayAdapter.createFromResource(
                this, R.array.provinces, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);
        if (hashMap != null) {//编辑，而非新增，设置默认省，市，县     
	        	String[] provinces = res.getStringArray(R.array.provinces);
	        	List<String> provincesList = Arrays.asList(provinces); 	        	
        		int position = provincesList.indexOf(defaultProvince);
        	    provinceSpinner.setSelection(position, true);
        	    
//          下拉列表选择地址-城市
            String selectedProvince = provinceSpinner.getSelectedItem().toString();
            int resource = res.getIdentifier(selectedProvince, "array", getPackageName());
        		cityAdapter = ArrayAdapter.createFromResource(
                        mContext, resource, android.R.layout.simple_spinner_item);
        		cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        		citySpinner.setAdapter(cityAdapter);
        		String[] cities = res.getStringArray(resource);
	        	List<String> citiesList = Arrays.asList(cities); 	        	
        		position = citiesList.indexOf(defaultCity);
        		citySpinner.setSelection(position, true);
        		
        		
        		//区/县
        		String selectedCity = citySpinner.getSelectedItem().toString();
        		resource = res.getIdentifier(selectedProvince + "_" + selectedCity, "array", getPackageName());
			areaAdapter = ArrayAdapter.createFromResource(
	                mContext, resource, android.R.layout.simple_spinner_item);
			areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			areaAdapter.notifyDataSetChanged();
			areaSpinner.setAdapter(areaAdapter);
			String[] areas = res.getStringArray(resource);
	        	List<String> areasList = Arrays.asList(areas); 	        	
	    		position = areasList.indexOf(defaultArea);
	    		areaSpinner.setSelection(position, true);
		}

        
//		若要自定义item和选中效果，自定义xml文件和Adapter
//      可参考http://stackoverflow.com/questions/17407626/custom-layout-for-spinner-item
//      选择了省份，更新城市列表，更新选中的省份
        provinceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
//				int item = provinceSpinner.getSelectedItemPosition();
				String selectedProvince = provinceSpinner.getSelectedItem().toString();
				Log.d("select", selectedProvince);
				
				res=getResources();
				int resource = res.getIdentifier(selectedProvince, "array", getPackageName());
				cityAdapter = ArrayAdapter.createFromResource(
		                mContext, resource, android.R.layout.simple_spinner_item);
				cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				cityAdapter.notifyDataSetChanged();
				citySpinner.setAdapter(cityAdapter);
				provinceCode = res.getString(res.getIdentifier(selectedProvince, "string", getPackageName()));
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
      //选择了城市,更新区县列表，更新选中的城市
		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View v, int position, long id) {
				
				int item = citySpinner.getSelectedItemPosition();
				String selectedCity = citySpinner.getSelectedItem().toString();
				String selectedProvince = provinceSpinner.getSelectedItem().toString();
				Log.d("select", selectedProvince + "_" + selectedCity);
				Log.d("select item:",item + "");
				
				res=getResources();

				int resourceId = res.getIdentifier(selectedProvince + "_" + selectedCity, "array", getPackageName());
				Log.d("resourceId:", resourceId + "");
				if (resourceId == 0) 
					return;
				
				areaAdapter = ArrayAdapter.createFromResource(
		                mContext, resourceId, android.R.layout.simple_spinner_item);
				areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				areaAdapter.notifyDataSetChanged();
				areaSpinner.setAdapter(areaAdapter);
				cityCode = res.getString(res.getIdentifier(selectedProvince + "_" + selectedCity, "string", getPackageName()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.d("select :", "选择了市后啥也没选！");
			}
		});
		
		//选择了区县，更新选中的区县
		areaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String selectedCity = citySpinner.getSelectedItem().toString();
				String selectedProvince = provinceSpinner.getSelectedItem().toString();
				String selectedArea = areaSpinner.getSelectedItem().toString();
				Log.d("select", selectedProvince + "_" + selectedCity + "_" + selectedArea);
				
				res=getResources();
				areaCode = res.getString(res.getIdentifier(selectedProvince + "_" + selectedCity + "_" + selectedArea, "string", getPackageName()));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
    
		saveButton.setOnClickListener(new OnClickListener() {
			List <NameValuePair> params;
		
			@Override
			public void onClick(View v) {
				// TODO 保存一条收货人数据，最多8条 /ajax/addAddr.do POST
				String url = "/ajax/addAddr.do";
				params = new ArrayList<NameValuePair>();
			    
			    
			    String name = nameEditText.getText().toString();
//			    String province = provinceSpinner.getSelectedItem().toString();
//			    String city = citySpinner.getSelectedItem().toString();
//			    String area = areaSpinner.getSelectedItem().toString();
			    String phone = phoneEditText.getText().toString();
			    String addressInfo = addressEditText.getText().toString();
			    String email = emailEditText.getText().toString();
			    String post = postEditText.getText().toString();
			    String error = null;
			    if (name.trim().length() == 0) {
					error = "收货人不能为空。";
				}else if (name.trim().length() > 32 || name.trim().length() < 2) {
					error = "收货人长度不符。";
				}else if (name.indexOf("<") != -1 || name.indexOf(">") != -1) {
					error = "收货人不能含特殊字符。";
				}else if (addressInfo.trim().length() == 0) {
					error = "地址不能为空。";
				}else if (addressInfo.trim().length() > 100 || addressInfo.trim().length() < 5) {
					error = "地址长度2-25个汉字。";
				}else if (addressInfo.indexOf("<") != -1 || addressInfo.indexOf(">") != -1) {
					error = "地址不能含特殊字符。";
				}else if (phone.trim().length() == 0) {
					error = "手机号码不能为空。";
				}else if (!Validate.checkPhoneNumber(phone)) {
					error = "手机格式不正确";
				}else if (post.trim().length() > 0 && !Validate.isPostCodeValid(post)) {
					error = "邮政编码格式不正确。";
				}
			    if (error != null) {
					Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
					return;
				}
			    //数据验证通过提交表单数据
			    params.add(new BasicNameValuePair("RECEIVER_NAME", name));
			    params.add(new BasicNameValuePair("EMAIL", email));
			    params.add(new BasicNameValuePair("POST", post));
			    params.add(new BasicNameValuePair("ADDRESS_DISTRICTID", provinceCode));
			    params.add(new BasicNameValuePair("ADDRESS_CITYID", cityCode));
			    params.add(new BasicNameValuePair("ADDRESS_AREAID", areaCode));
			    params.add(new BasicNameValuePair("ADDRESS_INFO", addressInfo));
			    params.add(new BasicNameValuePair("TELEPHONE", phone));
			    params.add(new BasicNameValuePair("addressId", addressId));
			    params.add(new BasicNameValuePair("PAYID", "1"));
			    params.add(new BasicNameValuePair("SEQID", "1")); 
			    
			    SaveReceiverTask task = new SaveReceiverTask();
			    task.execute(url, params);
			    
			}
		});

	}
	
	class SaveReceiverTask extends AsyncTask<Object, Integer, String>{

		private String response;

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			response = HttpFactory.getHttp().post(params[0].toString(), mContext, (List <NameValuePair>)params[1]);
			Log.d("response", response);
			return response;
		}
		
		@Override
		protected void onPostExecute(String result){
			if (result.equals("OK")) {
				//TODO 并退回到列表页。
				Toast.makeText(mContext, "保存成功。", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(mContext, ReceiverActivity.class);
				startActivity(intent);
				
//		        intent.putExtra("result", "");
////		        mIntent.putExtra("index", mPosition);
//				setResult(0, intent);
			}else{
				Toast.makeText(mContext, "保存失败。请重试。", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
