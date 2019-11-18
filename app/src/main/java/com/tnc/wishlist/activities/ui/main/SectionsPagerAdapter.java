package com.tnc.wishlist.activities.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tnc.wishlist.R;
import com.tnc.wishlist.fragments.ApprovedChildFragment;
import com.tnc.wishlist.fragments.ApprovedWishFragment;
import com.tnc.wishlist.fragments.PendingChildFragment;
import com.tnc.wishlist.fragments.PendingWishFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.approved_childO,R.string.pending_childO,R.string.approved_wishO
            ,R.string.pending_wishO};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position==0){
            return new ApprovedChildFragment();
        }
        if(position==1){
            return new PendingChildFragment();
        }
        if(position==2){
            return new ApprovedWishFragment();
        }
        else{
            return new PendingWishFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 4;
    }
}