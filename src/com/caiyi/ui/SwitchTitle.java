package com.caiyi.ui;

import java.util.ArrayList;

import com.caiyi.testfootball.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SwitchTitle extends HorizontalScrollView {

	private int mTxtActiveColor;
	private int mTxtDisableColor;
	private int mTabLineHeight;
	private int mTabLineColor;
	private int mTxtSize;
	private int mTabPadding;
	private int mExtraPadding = 0;

	private TabContainer mTabContainer;

	@TargetApi(Build.VERSION_CODES.L)
	public SwitchTitle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr);
	}

	public SwitchTitle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	public SwitchTitle(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public SwitchTitle(Context context) {
		this(context, null);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		Resources res = context.getResources();
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.switchTitle, defStyleAttr, 0);

		mTxtActiveColor = a.getColor(R.attr.tabTxtActiveColor, res.getColor(R.color.switchTitleTxtActive));
		mTxtDisableColor = a.getColor(R.attr.tabTxtDisableColor, res.getColor(R.color.switchTitleTxtDisable));
		mTabLineHeight = a.getDimensionPixelSize(R.attr.tabLineHeight,
				res.getDimensionPixelSize(R.dimen.switchTitleLineHeight));
		mTabLineColor = a.getColor(R.attr.tabLineColor, res.getColor(R.color.switchTitleline));
		mTxtSize = a.getDimensionPixelSize(R.attr.tabTxtSize, res.getDimensionPixelSize(R.dimen.switchTitleTxtSize));
		mTxtSize = a.getDimensionPixelSize(R.attr.tabPadding, res.getDimensionPixelSize(R.dimen.switchTitleTabPadding));
		a.recycle();
		mTabContainer = new TabContainer(context);

		addView(mTabContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	/** 设置标题 **/
	public void setTitle(ArrayList<CharSequence> titles) {
		mTabContainer.removeAllViews();
		for (CharSequence c : titles) {
			TabText tt = new TabText(getContext());
			tt.setText(c);
			mTabContainer.addView(tt, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		}
	}

	private class TabContainer extends LinearLayout {

		public TabContainer(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// TODO Auto-generated method stub
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
			// TODO Auto-generated method stub
			super.measureChildren(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/** tab TextView **/
	private class TabText extends TextView {
		public TabText(Context context) {
			super(context);
			init(context);
		}

		private void init(Context context) {
			setTextSize(mTxtSize);
			setTextColor(mTxtDisableColor);
			setGravity(Gravity.CENTER);
			setPadding(mTabPadding + mExtraPadding, 0, mTabPadding + mExtraPadding, 0);
		}

	}
}
