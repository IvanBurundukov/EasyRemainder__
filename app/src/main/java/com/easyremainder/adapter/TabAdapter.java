package com.easyremainder.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.easyremainder.fragment.CurrentTaskFragment;
import com.easyremainder.fragment.DoneTaskFragment;




public class TabAdapter extends FragmentStatePagerAdapter {
    public static final String tag="mylog2";
private int numberOfTabs;
public static final int CURRENT_TASK_FRAGMENT_POSITION=0;
public static final int DONE_TASK_FRAGMENT_POSITION=1;

private  CurrentTaskFragment currentTaskFragment;
private DoneTaskFragment doneTaskFragment;

    /**
     * @param fm
     * @deprecated
     */
    public TabAdapter(FragmentManager fm,int numberOfTabs) {
        super(fm);
        Log.d(tag,"start_inicil");
        this.numberOfTabs=numberOfTabs;
        currentTaskFragment = new CurrentTaskFragment();
        doneTaskFragment = new DoneTaskFragment();


        this.numberOfTabs=numberOfTabs;
        Log.d(tag,"end_inicil");
    }

    @Override
    public Fragment getItem(int i) {
       switch (i){
            case 0:return currentTaskFragment;
            case 1: return doneTaskFragment;
             default:  return null;
    }


    }

    @Override
    public int getCount() {
        Log.d(tag,"getCount");
        return numberOfTabs;
    }
}
