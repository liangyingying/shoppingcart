package com.baiyjk.shopping;

import com.baiyjk.shopping.http.HttpFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

/**
 * 此Activity应判断登录状态，已登录访问我的百洋；
 * 未登录，若已设置自动登录，自动登录；
 * 否则显示登录框
 * @author lyy
 *
 */
public class LoginActivity extends Activity {
	private Button loginButton;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox isRememberCheckBox;
	private Context context;
	String username;
	String password;
	int autoLogin;
	private SharedPreferences sp;
	private CheckBox autoLoginCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		context = this;
		initView();
	}

	private void initView() {
		sp = this.getSharedPreferences("baiyjk_preference", Context.MODE_PRIVATE); 
		loginButton = (Button) findViewById(R.id.login_button);
		usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
		passwordEditText = (EditText) findViewById(R.id.login_password_edittext);
		autoLoginCheckBox = (CheckBox) findViewById(R.id.login_auto);

//		根据sp保存的数据设置自动登录
//		http://blog.csdn.net/liuyiming_/article/details/7704923
		if (sp.getBoolean("auto_login", false)) {
			
			autoLoginCheckBox.setChecked(true);
			usernameEditText.setText(sp.getString("username", ""));
			passwordEditText.setText(sp.getString("password", ""));
		}
		
		//监听自动登录复选框事件
		autoLoginCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("Login", "" + isChecked);
				sp.edit().putBoolean("auto_login", isChecked).commit();				
			}
		});
		
		//监听登录按钮点击事件
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击登录按钮
				username = usernameEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				autoLogin = autoLoginCheckBox.isChecked() ? 1 : 0;
				
				if (username.length() < 6) {
					usernameEditText.setError("用户名错误");
				}else if (password.length() < 6) {
					passwordEditText.setError("密码错误");
				}else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String url = "/loginSubmit.do?format=true&log[name]=" + username
									+ "&log[pwd]=" + password
									+ "&chkRememberUsername=" + autoLogin;
							String result = HttpFactory.getHttp()
									.getUrlContext(url, context);
							if (result.length() == 1 && Integer.parseInt(result) == 1) {//登录成功，暂时跳转到我的百洋页面
								//登录成功和记住密码框为选中状态才保存用户信息
								if (autoLoginCheckBox.isChecked()) {
									Editor editor = sp.edit();
									editor.putString("username", username);
									editor.putString("password", password);
									editor.commit();
								}
								Intent intent = new Intent(context, AccountActivity.class);
								context.startActivity(intent);
								
							}else {
								usernameEditText.setError("用户名错误");
								passwordEditText.setError("密码错误");
							}
						}
					}).start();
				}

			}
		});
	}
}
