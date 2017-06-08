package com.example.leon.kotlinapplication.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.leon.kotlinapplication.fragments.AllMovieList
import com.example.leon.kotlinapplication.fragments.GenreMovieList
import com.example.leon.kotlinapplication.activities.MainActivity

/**
 * Created by Leon on 04.06.17.
 */
class ViewPagerAdapter(fm: FragmentManager?, var a: MainActivity) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return AllMovieList(a)
            1 -> return GenreMovieList()
            else -> return AllMovieList(a)
        }
    }


    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return super.getPageTitle(position)
    }

}