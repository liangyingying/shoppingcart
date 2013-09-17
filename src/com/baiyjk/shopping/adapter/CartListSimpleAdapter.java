package com.baiyjk.shopping.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Identity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baiyjk.shopping.R;
import com.baiyjk.shopping.ShoppingcartActivity;
import com.baiyjk.shopping.http.HttpFactory;
import com.baiyjk.shopping.utils.ImageLoader;

public class CartListSimpleAdapter extends SimpleAdapter {

	private int resource;
	private List<Map<String, Object>> data;
	private Context context;
	Handler handler = new Handler();
	private CartListSimpleAdapter adapter;
	private LayoutInflater layoutinflater;
	private InputMethodManager inputMgr;

	private String productUrl;
	private String productId;
	protected final String TAG = "Cart Adapter";
	private final String CART_STATE_NORMAL = "cart_state_normal";
	private final String CART_STATE_EDIT = "cart_state_edit";
	private String mState = CART_STATE_NORMAL;

	private String cartJson;
	private String cartItemId;
	private String cartItemQty;
	private View cartItem;
	private EditText qtyView;
	Map<Integer, String> mMapQty;//动态保存购物车中各商品的数量
	Map<Integer, Boolean> mMapFocusMap;//记录当前获得焦点的编辑框，在软键盘弹出后调用getView()会使得编辑框失去焦点，手动设置焦点
	private ListView viewGroup;
	public CartListSimpleAdapter(Context context,
			List<Map<String, Object>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.resource = resource;
		this.context = context;
		this.adapter = this;
		this.data = data;
		this.layoutinflater = LayoutInflater.from(context);
		mMapQty = new HashMap<Integer, String>();
		mMapFocusMap = new HashMap<Integer, Boolean>();
		inputMgr = (InputMethodManager)context.
	            getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		viewGroup = (ListView)parent;
		if (convertView == null) {
			convertView = layoutinflater.inflate(this.resource, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// myView = layoutinflater.inflate(this.resource, null);

		viewHolder.position = position;
		viewHolder.nameView.setText(data.get(position).get("name").toString());
		viewHolder.urlView.setText(data.get(position).get("url").toString());
		viewHolder.idView.setText(data.get(position).get("productId").toString());
		viewHolder.qtyView.setText(data.get(position).get("qty").toString());
		viewHolder.priceView.setText(data.get(position).get("price").toString());
		if (this.mState.equals(CART_STATE_EDIT)) {
			viewHolder.deleteImageView.setVisibility(View.VISIBLE);
		}else {
			viewHolder.deleteImageView.setVisibility(View.GONE);
		}
		
		ImageLoader imageLoader = new ImageLoader(context);Log.d("load image", "image position: " + position);
		imageLoader.DisplayImage((String) data.get(position).get("image"), viewHolder.imageView);

		//防止布局发生改变，失去焦点。暂时注释。
		if (mMapFocusMap.containsKey(position)) {
			viewHolder.qtyView.requestFocus();
		}
		
		
		// qtyView.setInputType(InputType.TYPE_CLASS_PHONE);
		
		/*
		 * qtyView.setKeyListener(new NumberKeyListener() {
		 * 
		 * @Override public int getInputType() { // TODO 0无键盘 1英文键盘 2模拟键盘 3数字键盘
		 * return 3; }
		 * 
		 * @Override protected char[] getAcceptedChars() { // TODO 返回允许输入的字符
		 * return new char[]{'1', '2'}; } });
		 */

		// 点击商品图片查看商品详情
		/*
		 * myView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub // 这里需要用到商品详情页的URL Log.d(TAG,"点击商品：" + ((TextView)
		 * (v.findViewById(R.id.cart_product_url))).getText().toString()); //
		 * Intent intent = new Intent(context, // ProductDetailActivity.class);
		 * Intent intent = new Intent(); intent.putExtra("url", ((TextView)
		 * (v.findViewById(R.id.cart_product_url))).getText().toString());
		 * intent.setClass(context, ProductDetailActivity.class);
		 * context.startActivity(intent); } });
		 */

		return convertView;

	}

	public void setEditState(String state) {
		this.mState = state;
	}

	class ViewHolder {
		ImageView imageView;
		TextView nameView;
		TextView urlView;
		TextView idView;
		TextView priceView;
		ImageView deleteImageView;
		TextView minusView;
		TextView plusView;
		TextView qtyView;
		int position;

		public ViewHolder(View myView) {
			imageView = (ImageView) myView.findViewById(R.id.cart_product_image);
			nameView = (TextView) myView.findViewById(R.id.cart_product_name);
			urlView = (TextView) myView.findViewById(R.id.cart_product_url);
			idView = (TextView) myView.findViewById(R.id.cart_product_id);
			priceView = (TextView) myView.findViewById(R.id.cart_product_price);
			deleteImageView = (ImageView) myView.findViewById(R.id.cart_product_delete);
			minusView = (TextView) myView.findViewById(R.id.cart_product_minus);
			plusView = (TextView) myView.findViewById(R.id.cart_product_plus);
			qtyView = (TextView) myView.findViewById(R.id.cart_product_edittext);
			
			qtyView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final View layout = layoutinflater.inflate(R.layout.shoppingcart_dialog,null);
//					final String qty = data.get(position).get("qty").toString();
					final int pos = position;
					final View itemView = v;
					

//					final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
//					dialog.setContentView(R.layout.shoppingcart_dialog);
					EditText qtyEditText = (EditText)layout.findViewById(R.id.cart_product_edittext);
//					RelativeLayout dialogLayout = (RelativeLayout)layout.findViewById(R.id.shoppingcart_dialog);
					qtyEditText.setText(data.get(position).get("qty").toString());
					qtyEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
						//获取焦点时弹出软键盘
						@Override
						public void onFocusChange(View arg0, boolean arg1) {
							inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
									InputMethodManager.HIDE_IMPLICIT_ONLY);
							
						}
					});
					/**
					 * 弹出键盘的Done按钮摁下
					 */
//					qtyEditText.setOnEditorActionListener(new OnEditorActionListener(){
//
//						@Override
//						public boolean onEditorAction(TextView v, int actionId,
//								KeyEvent event) {
//							boolean handled = false;
//					        if (actionId == EditorInfo.IME_ACTION_DONE) {
////					            sendMessage();
//					            handled = true;
//					        }
//					        return handled;
//						}});
					
					new AlertDialog.Builder(context)
						.setTitle("修改购买数量").setView(layout)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String num = ((EditText)layout.findViewById(R.id.cart_product_edittext)).getText().toString();
								
								if(Integer.valueOf(num) < 1){//小于最小购买量
									Toast.makeText(context, "购买数小于最小购买量。", Toast.LENGTH_SHORT).show();
								}else if (Integer.valueOf(num) > 99){
									Toast.makeText(context, "购买数大于最大购买量。", Toast.LENGTH_SHORT).show();
								}else if(Integer.valueOf(num) != Integer.valueOf(data.get(pos).get("qty").toString())){
									//TODO 收起键盘，更改列表页的对应商品数量
									inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
											InputMethodManager.HIDE_IMPLICIT_ONLY);
									String pId = data.get(pos).get("productId").toString();
									Log.d(pId + "更改成->", num);
									String url = "/ajax/changeNum.do?format=true&ordersign=0&type=0&productId=" + pId + "&qty=" + num;
									ChangeNumTask task = new ChangeNumTask();
									task.execute(url, String.valueOf(pos), num);
									
								}
							}
						})
						.setNegativeButton("取消", null).show(); 
				}
			});
			

			// 删除商品
			deleteImageView.setTag(position);

			// and 绑定事件
			deleteImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 点击删除购物车某个商品，同时更新购物车的总计（个数和购物车价格）
					View itemView = (View) v.getParent();
					cartItemId = ((TextView) (itemView.findViewById(R.id.cart_product_id))).getText()
							.toString();
					cartItemQty = ((TextView) (itemView.findViewById(R.id.cart_product_edittext))).getText()
							.toString();
					new Thread(new Runnable() {

						@Override
						public void run() {
							cartJson = HttpFactory.getHttp().getRequest(
									"/ajax/delete.do/?format=true&type=0&ordersign=0&productId="
											+ cartItemId + "&qty="
											+ cartItemQty, context);
							Log.d("click delete button", cartItemId + ":" + cartJson);

						}
					}).start();
					// 暂没考虑没有删除成功的情况。没考虑购物车总计信息
					int position = Integer.parseInt(v.getTag().toString());
					data.remove(position);
					notifyDataSetChanged();
				}
			});
//			

			// 通过-/+更改购物车某商品数量；
			OnClickListener l = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View itemView = (View) v.getParent();
					String itemCurrentQty = ((TextView) (itemView
							.findViewById(R.id.cart_product_edittext)))
							.getText().toString();
					if (v.getId() == R.id.cart_product_minus) {// 数量减1
						int itemTargetQty = Integer.parseInt(itemCurrentQty) - 1;
						if (itemTargetQty > 0) {
							// 发送请求
							// /ajax/changeNum.do?format=true&ordersign=0&type=0&productId=?&qty=?
						} else {
							return;
						}
					} else if (v.getId() == R.id.cart_product_plus) {// 数量加1
						int itemTargetQty = Integer.parseInt(itemCurrentQty) + 1;
						// 发送请求
					}
				}
			};
			minusView.setOnClickListener(l);
			plusView.setOnClickListener(l);

		}
	}
	
	private List<Map<String, Object>> getData() {
		data = new ArrayList<Map<String, Object>>();
//		Log.d(TAG, cartJson);
		try {
			JSONObject cartJsonObject = new JSONObject(cartJson);
			// 暂时只取['row']['0']
			JSONObject listJsonObject = cartJsonObject.getJSONObject("row").getJSONObject("0");

			Log.d(TAG, listJsonObject.toString());
			Iterator<String> iterator = listJsonObject.keys();
			for (int i = 0; i < listJsonObject.length(); i++) {
				String productId = iterator.next();
				JSONObject product = listJsonObject.getJSONObject(productId);
				JSONObject result = product.getJSONObject("result");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("productId", productId);
				map.put("url", result.get("link"));
				map.put("name", result.get("name"));
				map.put("qty", result.get("qty"));
				map.put("price", "￥" + result.get("price"));
				map.put("image", result.get("imageUrl"));
				map.put("editstate", CART_STATE_NORMAL);
				data.add(map);
			}

//			map = new HashMap<String, Object>();
//			map.put("editstate", mState);//默认不展示删除按钮，状态值保存在list的最后一项
//			list.add(map);

//			cartSize = cartJsonArray.getJSONObject(0).getInt("qty");
//			cartValue = cartJsonArray.getJSONObject(0).getLong("price");
//
//			cartSizeTv.setText("共 " + cartSize + " 件");
//			cartValueTv.setText("￥ " + cartValue);
//
//			Log.d(TAG, map.toString());
		} catch (JSONException e) {
			// TODO: handle exception
			Log.d("shoppingcart debug", e.getMessage());
		}

		return data;
	}
	
	//HTTP 更改商品的购买数量
	class ChangeNumTask extends AsyncTask<String, Integer, Boolean>{
		String response;
		String params[];
		@Override
		protected Boolean doInBackground(String... params) {
			this.params = params;
			response = HttpFactory.getHttp().getRequest(params[0].toString(), context);
			Log.d("修改商品数量", response);
			//TODO 修改商品总数和金额
			
//			Intent intent = new Intent(context, ShoppingcartActivity.class);
//			context.startActivity(intent);
			if (response.length() > 0) {
				return true;
			}
			return false;
		}
		
		/**
		 * 根据返回局部更新对应的商品个数，和购物车总计
		 * ==> 此处键盘由显示到隐藏，界面发生变化，listView必然重新加载。更新数据源。
		 */
		@Override
		protected void onPostExecute(Boolean result){
			View view = viewGroup.getChildAt(Integer.valueOf(params[1]) - viewGroup.getFirstVisiblePosition());
			inputMgr.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
			data.get(Integer.valueOf(params[1])).put("qty", params[2]);
			if (result) {
				
				TextView theItem = (TextView)view.findViewById(R.id.cart_product_edittext);				
				theItem.setText(params[2].toString());
				try {
					JSONObject obj = new JSONObject(response);
					LinearLayout root = (LinearLayout)(viewGroup.getParent().getParent());
					((TextView)(root.findViewById(R.id.cart_size))).setText("共" + obj.get("qty").toString() + "件");
					((TextView)(root.findViewById(R.id.cart_value))).setText("￥" + obj.get("price").toString());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
