package com.example.vakery.ics;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class LecturersPagerAdapter extends FragmentPagerAdapter {
    //кол-во страниц пейджера
    final int PAGE_COUNT = 3;
    //массив с названиями для вкладок (берем из ресурса, используя контекст приложения)
    private String tabTitles[] = new String[] {Vars.getContext().getString(R.string.department),
            Vars.getContext().getString(R.string.not_department),
            Vars.getContext().getString(R.string.all)};


    public LecturersPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        return LecturersListFragment.newInstance(position + 1);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}