package com.baiyjk.shopping.adapter;

import java.util.List;
import java.util.Map;

import com.baiyjk.shopping.R;
import com.baiyjk.shopping.adapter.CartListSimpleAdapter.ViewHolder;
import com.baiyjk.shopping.utils.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WishListAdapter extends SimpleAdapter{

	private Context mContext;
	private List<Map<String, String>> mData;
	private LayoutInflater mInflator;
	private int mResource;

	public WishListAdapter(Context context,
			List<Map<String, String>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mContext = context;
		mData = data;
		mInflator = LayoutInflater.from(context);
		mResource = resource;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Map<String, String> getItem(int position) {
		return mData.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(mResource, parent);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.nameView.setText(mData.get(position).get("product_name"));
		viewHolder.urlView.setText(mData.get(position).get("product_link"));
		viewHolder.idView.setText(mData.get(position).get("product_id"));
		viewHolder.priceView.setText(mData.get(position).get("product_price"));
		
		ImageLoader loader = new ImageLoader(mContext);
		loader.DisplayImage(mData.get(position).get("product_pic_link"), viewHolder.imageView);
		
		return super.getView(position, convertView, parent);
	}

	class ViewHolder{
		ImageView imageView;
		TextView nameView;
		TextView urlView;
		TextView idView;
		TextView priceView;
		public ViewHolder(View myView) {
			imageView = (ImageView) myView.findViewById(R.id.wish_product_image);
			nameView = (TextView) myView.findViewById(R.id.wish_product_name);
			urlView = (TextView) myView.findViewById(R.id.wish_product_url);
			idView = (TextView) myView.findViewById(R.id.cart_product_id);
			priceView = (TextView) myView.findViewById(R.id.wish_product_price);
		}
	}
}
