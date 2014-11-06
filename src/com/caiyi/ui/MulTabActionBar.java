package com.caiyi.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.caiyi.testfootball.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MulTabActionBar extends LinearLayout implements OnGestureListener {

    private int mActionBarHeight;
    private int mSwitchTabHeight;
    /** 滚动状态0代表未滚动，1代表向下滚动(查看下面内容)，-1向上滚动(查看上面内容) **/
    private int mScrollState = 0;
    private Toolbar mActionBar;
    private SwitchTitle mSwitchTitle;
    private boolean mCanHideOnScroll;

    private int mLastY;
    private int mBackgoundColor;

    GestureDetector mGestureDetector;
    public static final int ANIMATION_DURATION = 300;
    public static final String TAG = "MulTabActionBar";
    boolean hasSetTouchuHideView = false;

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
    public void setCanHideOnScroll(int maxScroll) {
        setCanHideOnScroll(maxScroll > mActionBarHeight);
    }

    /** 设置是否可以根据滚动状态隐藏actionBar **/
    public void setCanHideOnScroll(boolean canHide) {
        if (mCanHideOnScroll == canHide) {
            return;
        }
        mCanHideOnScroll = canHide;
        runHeightAnimation(getLayoutParams(), mActionBarHeight);
    }

    public void onPageChanged(int scrollPos) {
        mLastY = scrollPos;
    }

    /** 滑动动作触发 **/
    public void onScrollTo(int nowY) {
        if (!mCanHideOnScroll || hasSetTouchuHideView) {
            return;
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        int lastH = lp.height;
        if (nowY == mLastY || (nowY < mLastY && lastH == mActionBarHeight)
                || (nowY > mLastY && lastH == mSwitchTabHeight)) {
            return;
        }

        lp.height = Math.min(mActionBarHeight, Math.max(mSwitchTabHeight, lastH + mLastY - nowY));

//        Log.d(TAG, "scrollPos=" + scrollPos + "; height=" + lp.height);

        mScrollState = nowY > mLastY ? 1 : -1;
        mLastY = nowY;
        setLayoutParams(lp);
    }

    /** actionUp动作触发，结束actionBar高度改变 **/
    public void finishScroll() {
        if (!mCanHideOnScroll) {
            return;
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (mScrollState == 1 && lp.height != -mActionBarHeight) {
            runHeightAnimation(lp, mSwitchTabHeight);
        } else if (mScrollState == -1 && lp.height != 0) {
            runHeightAnimation(lp, mActionBarHeight);
        }
    }

    /** 改变actionBar高度到指定值 **/
    private void runHeightAnimation(final ViewGroup.LayoutParams lp, int to) {
//        Log.d(TAG, "runHeightAnimation from=" + lp.height + "; to=" + to);

        if (mActionBar.getTag() != null) {
            ((ValueAnimator) mActionBar.getTag()).cancel();
        }

        ValueAnimator oa = ObjectAnimator.ofInt(lp.height, to);
        oa.setDuration(ANIMATION_DURATION);
        oa.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.height = (int) animation.getAnimatedValue();
                setLayoutParams(lp);
            }
        });
        mActionBar.setTag(oa);
        oa.start();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (isInEditMode()) {
            return;
        }
        setGravity(Gravity.BOTTOM);
        setOrientation(VERTICAL);
        Resources res = getResources();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.mulTabActionBar, 0, 0);
        mSwitchTabHeight = a.getDimensionPixelSize(R.styleable.mulTabActionBar_mtabTabHeight,
                res.getDimensionPixelSize(R.dimen.switch_title_height));
        mBackgoundColor = a.getColor(R.styleable.mulTabActionBar_mtabBackground, res.getColor(R.color.green_500));
        a.recycle();

        // 直接new Toolbar(...) 有很多麻烦的样式问题，故写在xml中了
        mActionBar = (Toolbar) LayoutInflater.from(context).inflate(R.layout.action_bar, this, false);
        mActionBar.setBackgroundColor(mBackgoundColor);
        mActionBarHeight = res.getDimensionPixelSize(R.dimen.actionbar_height) + mSwitchTabHeight;

        mSwitchTitle = new SwitchTitle(context, attrs, defStyleAttr);
        mSwitchTitle.setBackgroundColor(mBackgoundColor);
        ImageView ivShadow = new ImageView(context, attrs, defStyleAttr);
        ivShadow.setScaleType(ScaleType.FIT_XY);

        addView(mActionBar);
        addView(mSwitchTitle, new LayoutParams(LayoutParams.MATCH_PARENT, mSwitchTabHeight));

        mGestureDetector = new GestureDetector(getContext(), this);
    }

    public void setTouchHideView(View v) {
        if (v == null) {
            hasSetTouchuHideView = false;
            return;
        }

        hasSetTouchuHideView = true;
        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        }
        v.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCanHideOnScroll) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        finishScroll();
                    } else {
                        mGestureDetector.onTouchEvent(event);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (Math.abs(distanceY) > Math.abs(distanceX)) {
            Log.d(TAG, "onScroll -> distanceY=" + distanceY);

            ViewGroup.LayoutParams lp = getLayoutParams();
            int lastH = lp.height;
            if ((distanceY > 0 && lastH == mSwitchTabHeight) || (distanceY < 0 && lastH == mActionBarHeight)) {
                return false;
            }
            lp.height = Math.min(mActionBarHeight, Math.max(mSwitchTabHeight, (int) (lastH - distanceY)));
            mScrollState = distanceY > 0 ? 1 : -1;
            setLayoutParams(lp);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
