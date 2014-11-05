package com.caiyi.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.caiyi.testfootball.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MulTabActionBar extends LinearLayout {

	private int mActionBarHeight;
	private int mSwitchTabHeight;
	/** 滚动状态0代表未滚动，1代表向下滚动(查看下面内容)，-1向上滚动(查看上面内容) **/
	private int mScrollState = 0;
	private Toolbar mActionBar;
	private SwitchTitle mSwitchTitle;
	private boolean mCanHideOnScroll;

	private int mLastScroll;
	private int mBackgoundColor;

	public static final int ANIMATION_DURATION = 300;

	@TargetApi(Build.VERSION_CODES.L)
	public MulTabActionBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MulTabActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	public MulTabActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public MulTabActionBar(Context context) {
		this(context, null);
	}

	/** @return 获取toolbar对象 **/
	public Toolbar getToolBar() {
		return mActionBar;
	}

	/** @return 获取switchTitle对象 **/
	public SwitchTitle getSwitchTitle() {
		return mSwitchTitle;
	}

	/** 根据可以滚动的最大距离判断是否可以隐藏actionBar **/
	public void setMaxScroll(int maxScroll) {
		mCanHideOnScroll = maxScroll > getHeight();
	}

	/** 设置是否可以根据滚动状态隐藏actionBar **/
	public void setCanHideOnScroll(boolean canHide) {
		if (mCanHideOnScroll == canHide) {
			return;
		}
		mCanHideOnScroll = canHide;
		runHeightAnimation((LayoutParams) mActionBar.getLayoutParams(), mActionBarHeight);
	}

	/** 滑动动作触发 **/
	public void onScroll(int scrollPos) {
		if (!mCanHideOnScroll) {
			return;
		}
		LayoutParams lp = (LayoutParams) mActionBar.getLayoutParams();
		int nH = lp.height;
		if ((scrollPos > mLastScroll && nH == 0) || (scrollPos < mLastScroll && nH == mActionBarHeight)) {
			return;
		}

		lp.height = Math.max(0, Math.min(mActionBarHeight, nH - scrollPos + mLastScroll));
		mScrollState = nH - lp.height;
		mActionBar.setLayoutParams(lp);
		mLastScroll = scrollPos;
	}

	/** actionUp动作触发，结束actionBar高度改变 **/
	public void finishScroll() {
		if (!mCanHideOnScroll) {
			return;
		}
		LayoutParams lp = (LayoutParams) mActionBar.getLayoutParams();
		if (mScrollState == 1 && lp.height != 0) {
			runHeightAnimation(lp, 0);
		} else if (mScrollState == -1 && lp.height != mActionBarHeight) {
			runHeightAnimation(lp, mActionBarHeight);
		}
	}

	/** 改变actionBar高度到指定值 **/
	private void runHeightAnimation(final LayoutParams lp, int to) {
		if (mActionBar.getTag() != null) {
			((ValueAnimator) mActionBar.getTag()).cancel();
		}

		ValueAnimator oa = ObjectAnimator.ofInt(lp.height, to);
		oa.setDuration(ANIMATION_DURATION);
		oa.setInterpolator(new AccelerateInterpolator());
		oa.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				lp.height = (int) animation.getAnimatedValue();
				mActionBar.setLayoutParams(lp);
			}
		});
		mActionBar.setTag(oa);
		oa.start();
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		setOrientation(VERTICAL);
		Resources res = getResources();
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.mulTabActionBar, 0, 0);
		mActionBarHeight = a.getInt(R.styleable.mulTabActionBar_mtabActionBarHeight,
				res.getDimensionPixelSize(R.dimen.actionbar_height));
		mSwitchTabHeight = a.getInt(R.styleable.mulTabActionBar_mtabTabHeight,
				res.getDimensionPixelSize(R.dimen.switch_title_height));
		int shadowResId = a.getResourceId(R.styleable.mulTabActionBar_mtabShadow, 0);
		mBackgoundColor = a.getColor(R.styleable.mulTabActionBar_mtabBackground, res.getColor(R.color.green_500));
		a.recycle();

		mActionBar = new Toolbar(context, attrs, defStyleAttr);
		mActionBar.setId(R.id.action_bar);
		mActionBar.setBackgroundColor(mBackgoundColor);
		mActionBar.setTitleTextColor(Color.WHITE);
		mSwitchTitle = new SwitchTitle(context, attrs, defStyleAttr);
		mSwitchTitle.setBackgroundColor(mBackgoundColor);
		ImageView ivShadow = new ImageView(context, attrs, defStyleAttr);
		ivShadow.setScaleType(ScaleType.FIT_XY);
		ivShadow.setImageResource(shadowResId);

		addView(mActionBar, new LayoutParams(LayoutParams.MATCH_PARENT, mActionBarHeight));
		addView(mSwitchTitle, new LayoutParams(LayoutParams.MATCH_PARENT, mSwitchTabHeight));
		addView(ivShadow, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

}
