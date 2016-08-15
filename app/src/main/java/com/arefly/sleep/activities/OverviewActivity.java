package com.arefly.sleep.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.arefly.sleep.R;
import com.arefly.sleep.adapters.TabPagerAdapter;
import com.arefly.sleep.fragments.DayInfoFragment;
import com.arefly.sleep.fragments.StatisticsFragment;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eflyjason on 3/8/2016.
 */
public class OverviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("OverviewActivity onCreate()");
        setContentView(R.layout.activity_overview);


        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        //fragments.add(StatisticsFragment.newInstance(""));

        fragments.add(new DayInfoFragment());
        titles.add("Yesterday");

        fragments.add(new StatisticsFragment());
        titles.add("Statistics");

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (viewPager != null) {
            viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), titles, fragments, this));
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }


    }
}