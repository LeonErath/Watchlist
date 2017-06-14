package com.example.leon.kotlinapplication.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.fragments.CinemaFragment
import com.example.leon.kotlinapplication.fragments.MyListFragment
import com.example.leon.kotlinapplication.fragments.PopularFragment

/**
 * Created by Leon on 04.06.17.
 */
class ViewPagerAdapter(fm: FragmentManager?, var a: MainActivity) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return PopularFragment(a)
            1 -> return CinemaFragment(a)
            2 -> return MyListFragment(a)
            else -> return PopularFragment(a)
        }
    }


    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return super.getPageTitle(position)
    }

}