package com.example.opentalk.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.opentalk.Activity_Tab_Fragment.Fragment_friend_add;
import com.example.opentalk.Activity_Tab_Fragment.Fragment_friend_wait;

public class TabPagerAdapter_frined_add extends FragmentStatePagerAdapter {
    //Count number of tabs
    private int tabCount;

    public TabPagerAdapter_frined_add(FragmentManager fm,int tabCount){
        super(fm,tabCount);
        this.tabCount=tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: {
                Fragment_friend_add fragment_friend_add = new Fragment_friend_add();
                return fragment_friend_add;
            }
            case 1: {
                Fragment_friend_wait fragment_friend_wait = new Fragment_friend_wait();
                return fragment_friend_wait;
            }
            default:{
                return null;
            }
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
