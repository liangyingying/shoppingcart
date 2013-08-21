package com.baiyjk.shopping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.baiyjk.shopping.http.HttpFactory;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

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
	private Context mContext;
	String username;
	String password;
	int autoLogin;
	private SharedPreferences sp;
	private CheckBox autoLoginCheckBox;
	private String mToActivity;
	private Button qqLogin;
	private Button aliLogin;
	private Tencent mTencent;
	private Activity mActivity;
	private static final String SCOPE = "get_simple_userinfo, add_share";
//	private static final String APP_ID = "100507208";//new
	private static final String APP_ID = "100250112";
	private static final String APP_KEY = "b286f2a3d595b059e56a89aa9b23cd39";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Intent intent = getIntent();
		mToActivity = intent.getStringExtra("toActivity");
		mActivity = this;
		mContext = this;
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(APP_ID, mContext);
		// 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
		initView();
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       mTencent.onActivityResult(requestCode, resultCode, data) ;
	 }

	private void initView() {
		sp = this.getSharedPreferences("baiyjk_preference", Context.MODE_PRIVATE); 
		loginButton = (Button) findViewById(R.id.login_button);
		usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
		passwordEditText = (EditText) findViewById(R.id.login_password_edittext);
		autoLoginCheckBox = (CheckBox) findViewById(R.id.login_auto);
		qqLogin = (Button)findViewById(R.id.qq_login_button);
		aliLogin = (Button)findViewById(R.id.zfb_login_button);

//		根据sp保存的数据设置自动登录
//		http://blog.csdn.net/liuyiming_/article/details/7704923
		if (sp.getBoolean("auto_login", false)) {
			
			autoLoginCheckBox.setChecked(true);
			usernameEditText.setText(sp.getString("username", ""));
			passwordEditText.setText(sp.getString("password", ""));
		}
		
		qqLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mTencent.isSessionValid()) {
		            IUiListener listener = new BaseUiListener() {
		                @Override
		                protected void doComplete(JSONObject values) {
		                		super.doComplete(values);
//		                    updateLoginButton();
		                		Log.d("QQ登录", values.toString());
		                		mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
		                        Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", false), null);
		                }
		            };
		            mTencent.login(mActivity, SCOPE, listener);
		        } else {
		            mTencent.logout(mContext);
//		            updateLoginButton();
		        }
			}
		});

		
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
									.getRequest(url, mContext);
							if (result.length() == 1 && Integer.parseInt(result) == 1) {//登录成功，暂时跳转到我的百洋页面
								//登录成功和记住密码框为选中状态才保存用户信息
								if (autoLoginCheckBox.isChecked()) {
									Editor editor = sp.edit();
									editor.putString("username", username);
									editor.putString("password", password);
									editor.commit();
								}
								//登录成功。跳转到目标Activity。默认是我的百洋。
								Class c = AccountActivity.class;
								if (mToActivity != null) {
									try {
										Log.d("目标activity = ", mToActivity);
										c = Class.forName(mToActivity);
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}
								Log.d("登录操作", "成功，跳转至"+c.toString());
								Intent intent = new Intent(mContext, c);
								mContext.startActivity(intent);
								
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
	
	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(JSONObject response) {
//			mBaseMessageText.setText("onComplete:");
//			mMessageText.setText(response.toString());
			doComplete(response);
		}
		protected void doComplete(JSONObject values) {
//			Log.d("qq response", values.toString());
//			在这个json字符串中会包含三个参数，分别是openid、access_token、expires_in。
//			正常情况下返回码为0，表示调用成功。
//			{"ret":"0",
//        		"pay_token":"9120A99D0FB0E13CF28E8367CCA73E40",
//        		"pf":"openmobile_android",
//        		"appid":"100507208",
//        		"openid":"45028B4D1049F724939A6BA46422C07A",
//        		"expires_in":"7776000",
//        		"pfkey":"56899204b6a1887c61f6f6e4068d57f9",
//        		"access_token":"A8686D7921365B019524C3836EC2EA51"}
			SharedPreferences pref = mContext.getSharedPreferences("baiyjk_preference", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			
			try {
				editor.putString("openid", values.getString("openid"));
				editor.putString("access_token", values.getString("access_token"));
				//access_token的有效期为3个月
				editor.putLong("expires_in", System.currentTimeMillis() + Long.parseLong(values.getString("expires_in")) * 1000);
				
				editor.commit();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Override
		public void onError(UiError e) {
//			showResult("onError:", "code:" + e.errorCode + ", msg:"
//			+ e.errorMessage + ", detail:" + e.errorDetail);
		}
		@Override
		public void onCancel() {
//			showResult("onCancel", "");
		}
	}
	
	private class BaseApiListener implements IRequestListener {
        private String mScope = SCOPE;
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
//            showResult("IRequestListener.onComplete:", response.toString());
            doComplete(response, state);
        }

        protected void doComplete(JSONObject response, Object state) {
            try {
                int ret = response.getInt("ret");
                if (ret == 100030) {//用户未对应用进行授权，需要引导用户重新登录授权，并且在登录的Scope参数里，加上相应的API名称
                    if (mNeedReAuth) {
                        Runnable r = new Runnable() {
                            public void run() {
                                mTencent.reAuth(LoginActivity.this, mScope, new BaseUiListener());
                            }
                        };
                        LoginActivity.this.runOnUiThread(r);
                    }
                }else if (ret == 0) {
					Log.d("QQ昵称：", response.getString("nickname"));
					Log.d("QQ信息", response.toString());
					//此处单独用server 接口较好。
//					http://www.baiyjk.com/qqLoginResult.do?
//					oauth_token=9091370465137899870
//							&openid=53C2A8AF04889C9674F4FF9862B64985
//							&oauth_signature=jojV5vG94KWrsBO7ZOOyYL98jyk%3D
//							&oauth_vericode=516116048
//							&timestamp=1377077959
				}
                
                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
                // 如果cgi返回的值没有token, 则会清空原来的token
                // String token = response.getString("access_token");
                // String expire = response.getString("expires_in");
                // String openid = response.getString("openid");
                // mTencent.setAccessToken(token, expire);
                // mTencent.setOpenId(openid);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("QQ获取用户信息", response.toString());
            }

        }

        @Override
        public void onIOException(final IOException e, Object state) {
//            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
//            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
//            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
//            showResult("IRequestListener.onConnectTimeoutException:", arg0.getMessage());

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
//            showResult("IRequestListener.SocketTimeoutException:", arg0.getMessage());
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
//            showResult("IRequestListener.onUnknowException:", arg0.getMessage());
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
//            showResult("IRequestListener.HttpStatusException:", arg0.getMessage());
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
//            showResult("IRequestListener.onNetworkUnavailableException:", arg0.getMessage());
        }
    }
}
