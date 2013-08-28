package com.baiyjk.shopping.adapter;



import com.baiyjk.shopping.R;
import com.baiyjk.shopping.http.HttpFactory;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;


public class PullToRefreshListView extends ListView{

	private static final String TAG = "custom listview";
	// "上拉加载更多"的各种View
	private ViewGroup mPushUpToLoadMoreView;
	private TextView mPushUpToLoadMoreLabel;
	private TextView mPushUpToLoadMoreFailedLabel;
	private ProgressBar mPushUpToLoadMoreProgressBar;
	
	private Context mContext;
	private LayoutInflater mInflater;
	private RotateAnimation mFlipAnimation;
	private boolean loadingMore = false;
		

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		
//		setOnScrollListener(this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		// 初始化需要用到的动画
		mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		
		// 初始化"上拉加载更多"
		mPushUpToLoadMoreView = (ViewGroup) mInflater.inflate(R.layout.pull_up_to_refresh_footer, null);
		mPushUpToLoadMoreLabel = (TextView) mPushUpToLoadMoreView.findViewById(R.id.push_up_to_load_more_label);
		mPushUpToLoadMoreFailedLabel = (TextView) mPushUpToLoadMoreView.findViewById(R.id.pull_up_to_refresh_failed_label);
		mPushUpToLoadMoreProgressBar = (ProgressBar) mPushUpToLoadMoreView.findViewById(R.id.push_up_to_load_more_progress);

		// 添加 "上推加载更多"的View
		addFooterView(mPushUpToLoadMoreView);
	}

}
