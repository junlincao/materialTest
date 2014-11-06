package com.caiyi.testfootball;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.caiyi.ui.ListViewHelper;
import com.caiyi.ui.ListViewHelper.NotSupportException;
import com.caiyi.ui.MulTabActionBar;
import com.caiyi.ui.SwitchTitle;
import com.caiyi.ui.SwitchTitle.OnPageChangeListener;

public class FootballActivity extends ActionBarActivity {

    static final String TAG = "FootballActivity";
    private MulTabActionBar mTabActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football);

        mTabActionbar = (MulTabActionBar) findViewById(R.id.mtab_title);
        setSupportActionBar(mTabActionbar.getToolBar());
        SwitchTitle st = mTabActionbar.getSwitchTitle();
        ViewPager vp = (ViewPager) findViewById(R.id.vp_pager);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ArrayList<CharSequence> titles = new ArrayList<CharSequence>();
        titles.add("page");
        titles.add("咖啡");
        titles.add("白咖啡");
        titles.add("原味");
        titles.add("怡宝");
        titles.add("马来西亚");
        titles.add("40g");
        titles.add("生产日期");
        titles.add("三合一");

        final MPagerAdapter mPageAdapter = new MPagerAdapter(titles);
        vp.setAdapter(mPageAdapter);
        st.setTitle(titles, vp);

        mTabActionbar.setCanHideOnScroll(true);
        st.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                ListView lv = (ListView) mPageAdapter.getmViews(arg0);
                mTabActionbar.setTouchHideView(lv);
//                try {
//                    int maxY = ListViewHelper.getScrollRange(lv);
//                    mTabActionbar.setCanHideOnScroll(maxY);
//                    int y = ListViewHelper.getScrollY(lv);
//                    mTabActionbar.onPageChanged(y);
//                } catch (NotSupportException e) {
//                    Log.e(TAG, e.getMessage(), e);
//                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

//        mTabActionbar.setTouchHideView(vp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ListView getPageListView(CharSequence title, int listCount) {
        ListView lv = new ListView(this);
//        lv.setPadding(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height), 0, 0);
//        lv.setClipToPadding(false);
        lv.setAdapter(new MTmpListAdapter(title, listCount));
//        lv.setOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//                    mTabActionbar.finishScroll();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//                    int totalItemCount) {
//                try {
//                    int y = ListViewHelper.getScrollY(view);
//
//                    Log.d(TAG, "OnScrollListener scrollPos=" + y);
//
//                    mTabActionbar.onScroll(y);
//                } catch (NotSupportException e) {
//                    Log.e(TAG, e.getMessage(), e);
//                }
//            }
//        });
        return lv;
    }

    private class MPagerAdapter extends PagerAdapter {
        private ArrayList<CharSequence> mTitles = new ArrayList<CharSequence>(0);
        private SparseArray<View> mViews;

        public MPagerAdapter(ArrayList<CharSequence> titles) {
            mTitles = titles;
            mViews = new SparseArray<View>(mTitles.size());
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        public View getmViews(int pos) {
            return mViews.get(pos);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getPageListView(mTitles.get(position), (position + 1) * 3);

            container.addView(v);
            mViews.put(position, v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
            mViews.remove(position);
        }

    }

    private class MTmpListAdapter extends BaseAdapter {
        ArrayList<String> datas = new ArrayList<String>();

        public MTmpListAdapter(CharSequence title, int count) {
            for (int i = 0; i < count; i++) {
                datas.add(title + " -> " + i);
            }
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvItem;
            if (convertView == null) {
                tvItem = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                tvItem = (TextView) convertView;
            }

            tvItem.setText(datas.get(position));
            return tvItem;
        }
    }
}
