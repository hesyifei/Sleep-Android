package com.arefly.sleep.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arefly.sleep.R;
import com.arefly.sleep.activities.MainActivity;
import com.arefly.sleep.adapters.TabPagerAdapter;
import com.arefly.sleep.data.helpers.SleepDurationRecordHelper;
import com.orhanobut.logger.Logger;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by eflyjason on 15/8/2016.
 */
public class OverviewFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("OverviewFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("OverviewFragment onCreateView()");
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i("OverviewFragment onViewCreated()");

        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        MainActivity.setupDrawer(mainActivity, view);
        mainActivity.setTitle(getString(R.string.overview_fragment_name));


        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();


        //fragments.add(StatisticsFragment.newInstance(""));

        Fragment dayInfoFragment = new DayInfoFragment();
        DateFormat dateFormat = SleepDurationRecordHelper.SIMPLE_DATE_FORMAT;
        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DATE, -1);
        String dateToBeChecked = dateFormat.format(yesterdayCal.getTime());

        Bundle bundle = new Bundle();
        bundle.putString(SleepDurationRecordHelper.DATE_DATA_TO_BE_PASSED_ID, dateToBeChecked);
        dayInfoFragment.setArguments(bundle);

        fragments.add(dayInfoFragment);
        titles.add("Yesterday");


        fragments.add(new StatisticsFragment());
        titles.add("Statistics");


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        if (viewPager != null) {
            viewPager.setAdapter(new TabPagerAdapter(getChildFragmentManager(), titles, fragments));
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

}