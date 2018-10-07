package com.example.leon.kotlinapplication.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.example.leon.kotlinapplication.fragments.CinemaFragment
import com.example.leon.kotlinapplication.fragments.MyListFragment
import com.example.leon.kotlinapplication.fragments.PopularFragment

/**
 * Created by Leon on 04.06.17.
 */
class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return PopularFragment()
            1 -> return CinemaFragment()
            2 -> return MyListFragment()
            else -> {
                Log.e("ViewPagerAdapter", "Error getItem()")
                return PopularFragment()
            }
        }
    }


    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return super.getPageTitle(position)!!
    }

}