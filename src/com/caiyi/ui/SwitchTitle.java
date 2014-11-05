package com.caiyi.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caiyi.testfootball.R;

public class SwitchTitle extends HorizontalScrollView implements OnClickListener {

    private int mTxtActiveColor;
    private int mTxtDisableColor;
    private int mTabLineHeight;
    private int mTabLineColor;
    private int mTxtSize;
    private int mTabPadding;
    private int mExtraPadding = 0;

    private TabContainer mTabContainer;

    private static final String TAG = "SwitchTitle";

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

    private ViewPager mViewPager;
    private OnPageChangeListener mOnPageChange;

    public void setOnPageChangeListener(OnPageChangeListener lis) {
        mOnPageChange = lis;
    }

    public interface OnPageChangeListener {
        void onPageSelected(int arg0);

        void onPageScrolled(int arg0, float arg1, int arg2);

        void onPageScrollStateChanged(int arg0);
    }

    /** 设置标题 **/
    public void setTitle(ArrayList<CharSequence> titles, ViewPager vp) {
        if (mViewPager != vp) {
            mViewPager.setOnPageChangeListener(null);
            mViewPager = vp;
        }
        mTabContainer.removeAllViews();
        for (CharSequence c : titles) {
            TabText tt = new TabText(getContext());
            tt.setText(c);
            mTabContainer.addView(tt, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {

                if (mOnPageChange != null) {
                    mOnPageChange.onPageSelected(arg0);
                }
                Log.d(TAG, "onPageSelected->arg0=" + arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mOnPageChange != null) {
                    mOnPageChange.onPageScrolled(arg0, arg1, arg2);
                }

                Log.d(TAG, "onPageScrolled->arg0=" + arg0 + "; arg1=" + arg1 + "; arg2=" + arg2);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

                if (mOnPageChange != null) {
                    mOnPageChange.onPageScrollStateChanged(arg0);
                }
                Log.d(TAG, "onPageScrollStateChanged->arg0=" + arg0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    private class TabContainer extends LinearLayout {
        public TabContainer(Context context) {
            super(context);
        }

        @Override
        protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
            super.measureChildren(widthMeasureSpec, heightMeasureSpec);

            int sumChildWidth = 0;
            for (int i = 0; i < getChildCount(); i++) {
                sumChildWidth += getChildAt(i).getMeasuredWidth();
            }

            if (getWidth() > sumChildWidth) {
                mExtraPadding = (getWidth() - sumChildWidth) / (2 * getChildCount());

                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).setPadding(mTabPadding + mExtraPadding, 0, mTabPadding + mExtraPadding, 0);
                }
            }
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
