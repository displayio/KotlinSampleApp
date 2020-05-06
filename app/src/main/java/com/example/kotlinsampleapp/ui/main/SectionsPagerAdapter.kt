package com.example.kotlinsampleapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlinsampleapp.AdUnitType
import com.example.kotlinsampleapp.MainActivity
import com.example.kotlinsampleapp.R

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return AdUnitFragment.newInstance(
            MainActivity.APP_ID,
            AdUnitType.values()[position].placementId,
            AdUnitType.values()[position].name
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return AdUnitType.values()[position].adUnitTitle
    }

    override fun getCount(): Int {
        return AdUnitType.values().size
    }
}