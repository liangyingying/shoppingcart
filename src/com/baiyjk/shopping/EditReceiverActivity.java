package com.baiyjk.shopping;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

public class EditReceiverActivity extends Activity implements OnItemSelectedListener{
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_receiver);
		
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
			nameEditText.setText(hashMap.get("name"));
//			areaSpinner.setText(hashMap.get("area"));
			addressEditText.setText(hashMap.get("info"));
			postEditText.setText(hashMap.get("post"));
			phoneEditText.setText(hashMap.get("phone"));
			emailEditText.setText(hashMap.get("email") == null ? hashMap.get("email") : "");
			Log.d("email", hashMap.get("email"));
		}else {
			titleView.setText("新增收货信息");
		}
		
		//下拉列表选择地址		
		provinceAdapter = ArrayAdapter.createFromResource(
                this, R.array.provinces, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);
        //选择了省份
        provinceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				int item = provinceSpinner.getSelectedItemPosition();
				String selectedProvince = provinceSpinner.getSelectedItem().toString();
//				String selectedProvince = provinceSpinner.getSelectedItem().
				Log.d("select", selectedProvince);
				if (item != 0) {
					Resources res=getResources();

					int resource = res.getIdentifier(selectedProvince, "array", getPackageName());
					cityAdapter = ArrayAdapter.createFromResource(
			                mContext, resource, android.R.layout.simple_spinner_item);
					cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					cityAdapter.notifyDataSetChanged();
					citySpinner.setAdapter(cityAdapter);
					
					//选择了市
					citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View v, int position, long id) {
							// TODO Auto-generated method stub
							int item = citySpinner.getSelectedItemPosition();
							String selectedArea = citySpinner.getSelectedItem().toString();
							String selectedCity = provinceSpinner.getSelectedItem().toString();
							Log.d("select", selectedCity + "_" + selectedArea);
							Log.d("select item:",item + "");
							if (item != 0) {
								Resources res=getResources();

								int resourceId = res.getIdentifier(selectedCity + "_" + selectedArea, "array", getPackageName());
								Log.d("resourceId:", resourceId + "");
								areaAdapter = ArrayAdapter.createFromResource(
						                mContext, resourceId, android.R.layout.simple_spinner_item);
								areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								areaAdapter.notifyDataSetChanged();
								areaSpinner.setAdapter(areaAdapter);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							Log.d("select :", "选择了市后啥也没选！");
						}
					});
				}
			}
			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 保存数据
				
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int posotion,
			long id) {
//		selection.setText(items[position]);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
