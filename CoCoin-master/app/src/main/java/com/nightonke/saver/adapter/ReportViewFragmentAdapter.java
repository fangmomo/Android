package com.nightonke.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nightonke.saver.fragment.ReportViewFragment;

// Todo optimize this

public class ReportViewFragmentAdapter extends FragmentStatePagerAdapter {

    public ReportViewFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return ReportViewFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ReportViewFragment.REPORT_TITLE;
    }
}
