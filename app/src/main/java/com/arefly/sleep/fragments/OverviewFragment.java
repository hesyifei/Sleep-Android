package com.arefly.sleep.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.GregorianCalendar;
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
        Calendar yesterdayCal = GregorianCalendar.getInstance();
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

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.overview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Get Sleep app on Google Play Store today!\n\nhttps://play.google.com/store/apps/details?id=com.arefly.sleep");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}