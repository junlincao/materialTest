package com.caiyi.testfootball;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caiyi.ui.SwitchTitle;

public class FootballActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football);

        SwitchTitle st = (SwitchTitle) findViewById(R.id.st_titles);
        ViewPager vp = (ViewPager) findViewById(R.id.vp_pager);

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
        vp.setAdapter(new MPagerAdapter(titles));
        st.setTitle(titles, vp);
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

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(FootballActivity.this);
            tv.setGravity(Gravity.CENTER);
            tv.setText(mTitles.get(position));

            container.addView(tv);
            mViews.put(position, tv);
            return tv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
            mViews.remove(position);
        }

    }
}
