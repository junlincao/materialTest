package com.caiyi.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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

    private int mLineLeft = 0;
    private int mLineRight = 0;

    private TabContainer mTabContainer;

    private int mNowSelectedPos = 0;

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
        this(context, attrs, 0);
    }

    public SwitchTitle(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (isInEditMode()) {
            return;
        }

        Resources res = context.getResources();
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.switchTitle, defStyleAttr, 0);

        mTxtActiveColor = a.getColor(R.styleable.switchTitle_tabTxtActiveColor,
                res.getColor(R.color.switchTitleTxtActive));
        mTxtDisableColor = a.getColor(R.styleable.switchTitle_tabTxtDisableColor,
                res.getColor(R.color.switchTitleTxtDisable));
        mTabLineHeight = a.getDimensionPixelSize(R.styleable.switchTitle_tabLineHeight,
                res.getDimensionPixelSize(R.dimen.switchTitleLineHeight));
        mTabLineColor = a.getColor(R.styleable.switchTitle_tabLineColor, res.getColor(R.color.switchTitleline));
        mTxtSize = a.getDimensionPixelSize(R.styleable.switchTitle_tabTxtSize,
                res.getDimensionPixelSize(R.dimen.switchTitleTxtSize));
        mTabPadding = a.getDimensionPixelSize(R.styleable.switchTitle_tabPadding,
                res.getDimensionPixelSize(R.dimen.switchTitleTabPadding));
        a.recycle();
        mTabContainer = new TabContainer(context);

        addView(mTabContainer, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        if (mTabContainer.getMeasuredWidth() < w) {
            int childHMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
                    + getPaddingBottom(), mTabContainer.getLayoutParams().height);
            int childWMeasureSpec = MeasureSpec.makeMeasureSpec(w - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY);
            mTabContainer.measure(childWMeasureSpec, childHMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        View selTab = mTabContainer.getChildAt(mNowSelectedPos);
        mLineLeft = selTab.getLeft();
        mLineRight = selTab.getRight();
        mTabContainer.invalidate();
    }

    /** 设置标题 **/
    public void setTitle(ArrayList<CharSequence> titles, ViewPager vp) {
        if (titles == null || vp == null || titles.size() == 0) {
            throw new IllegalArgumentException();
        }

        mViewPager = vp;
        mTabContainer.removeAllViews();
        mNowSelectedPos = vp.getCurrentItem();
        for (int i = 0; i < titles.size(); i++) {
            TabText tt = new TabText(getContext(), i);
            tt.setText(titles.get(i));
            if (mNowSelectedPos == i) {
                tt.setTextColor(mTxtActiveColor);
            } else {
                tt.setTextColor(mTxtDisableColor);
            }
            tt.setOnClickListener(this);
            mTabContainer.addView(tt, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        }

        mViewPager.setOnPageChangeListener(new MOnPageChangeListener());
    }

    private boolean mIsClickAnimation = false;

    @Override
    public void onClick(View v) {
        if (v instanceof TabText) {
            int pos = ((TabText) v).getPos();
            mIsClickAnimation = true;
            mViewPager.setCurrentItem(pos);

            ((TabText) mTabContainer.getChildAt(mNowSelectedPos)).setTextColor(mTxtDisableColor);
            ((TabText) mTabContainer.getChildAt(pos)).setTextColor(mTxtActiveColor);
            mNowSelectedPos = pos;
            // TODO 动画
        }
    }

    /*******************************************************************************************/
    /************************************** TabContainer **************************************/
    /*******************************************************************************************/

    /** tab 容器， 选中的项底部含有下划线 **/
    private class TabContainer extends LinearLayout {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public TabContainer(Context context) {
            super(context);
            setOrientation(HORIZONTAL);
            setFillViewport(true);
            setWillNotDraw(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setColor(mTabLineColor);
            mPaint.setStyle(Style.FILL);
            canvas.drawRect(mLineLeft, getHeight() - mTabLineHeight, mLineRight, getHeight(), mPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            if (widthMode == MeasureSpec.UNSPECIFIED) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else if (widthMode == MeasureSpec.EXACTLY) {
                final int w = MeasureSpec.getSize(widthMeasureSpec);
                final int childCount = getChildCount();
                int exPadding = (w - getMeasuredWidth()) / childCount;

                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);

                    int childHMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
                            + getPaddingBottom(), child.getLayoutParams().height);
                    int childWMeasureSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() + exPadding,
                            MeasureSpec.EXACTLY);
                    child.measure(childWMeasureSpec, childHMeasureSpec);
                }

                setMeasuredDimension(w, MeasureSpec.getSize(heightMeasureSpec));
            }
        }
    }

    /** tab TextView **/
    private class TabText extends TextView {
        private int mPos;

        public TabText(Context context, int pos) {
            super(context);
            mPos = pos;
            init(context);
        }

        private int getPos() {
            return mPos;
        }

        private void init(Context context) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTxtSize);
            setGravity(Gravity.CENTER);
            setPadding(mTabPadding, 0, mTabPadding, mTabLineHeight);
        }
    }

    private class MOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int descPos;
        private int tmpPos;

        @Override
        public void onPageSelected(int arg0) {
            if (mOnPageChange != null) {
                mOnPageChange.onPageSelected(arg0);
            }
            descPos = arg0;
            ((TabText) mTabContainer.getChildAt(mNowSelectedPos)).setTextColor(mTxtDisableColor);
            ((TabText) mTabContainer.getChildAt(arg0)).setTextColor(mTxtActiveColor);
            mNowSelectedPos = arg0;
        }

        @Override
        public void onPageScrolled(int currentPage, float pageOffset, int offsetPixels) {
            if (mOnPageChange != null) {
                mOnPageChange.onPageScrolled(currentPage, pageOffset, offsetPixels);
            }
            if (mIsClickAnimation) {
                return;
            }

            View destV;
            View srcV;
            if (currentPage == tmpPos) { // 页面+ 动作
                if (tmpPos == mTabContainer.getChildCount() - 1) {
                    return;
                }
                destV = mTabContainer.getChildAt(currentPage + 1);
                srcV = mTabContainer.getChildAt(currentPage);
            } else if (currentPage == tmpPos - 1) { // 页面- 动作
                if (tmpPos == 0) {
                    return;
                }
                srcV = mTabContainer.getChildAt(currentPage);
                destV = mTabContainer.getChildAt(tmpPos);
            } else {
                return;
            }

            mLineRight = srcV.getRight() + (int) ((destV.getRight() - srcV.getRight()) * pageOffset);
            mLineLeft = srcV.getLeft() + (int) ((destV.getLeft() - srcV.getLeft()) * pageOffset);

            mTabContainer.invalidate(srcV.getLeft(), mTabContainer.getHeight() - mTabLineHeight
                    , destV.getRight(), mTabContainer.getHeight());

//            Log.d(TAG, "mLineLeft=" + mLineLeft + "; mLineRight=" + mLineRight);
//            Log.d(TAG, "onPageScrolled->currentPage=" + currentPage + "; pageOffset=" + pageOffset
//                    + "; offsetPixels=" + offsetPixels);
//            Log.d(TAG, "-----------viewPage currentPage=" + mViewPager.getCurrentItem());
        }

        @Override
        public void onPageScrollStateChanged(int status) {
            if (status == 1) { // 开始滑动
                tmpPos = mViewPager.getCurrentItem();
                View v = mTabContainer.getChildAt(tmpPos);
                int scrollPos = v.getLeft() - (SwitchTitle.this.getWidth() - v.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
            } else if (status == 0) { // 结束滑动
                tmpPos = descPos;
                mIsClickAnimation = false;
            }

            if (mOnPageChange != null) {
                mOnPageChange.onPageScrollStateChanged(status);
            }
//            Log.d(TAG, "onPageScrollStateChanged->arg0=" + status);
        }
    }
}
