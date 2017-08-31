package com.android.indicator;


import android.support.v4.view.ViewPager;

/**
 * 简化和ViewPager绑定
 */

public class ViewPagerHelper {
    public static void bind(final NFCustomIndicator NFCustomIndicator, ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                NFCustomIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                NFCustomIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                NFCustomIndicator.onPageScrollStateChanged(state);
            }
        });
    }
}
