package com.artesdesign.util

import androidx.fragment.app.Fragment
import com.artesdesign.activities.ShoppingActivity
import com.artesdesign.augmentedReality.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.GONE
}
fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE
}