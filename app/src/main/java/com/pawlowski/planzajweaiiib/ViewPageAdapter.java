package com.pawlowski.planzajweaiiib;

import com.pawlowski.planzajweaiiib.day.DayFragment;

import java.sql.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {

    MainActivity activity;
    DayFragment[] dayFragments = new DayFragment[5];

    public ViewPageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, MainActivity activity) {
        super(fragmentManager, lifecycle);
        this.activity = activity;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return dayFragments[position] = new DayFragment(position, activity);
    }

    public void refreshFragment(int position)
    {
        if(dayFragments[position] != null)
        {
            dayFragments[position].refresh();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}