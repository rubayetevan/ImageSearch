package com.pluang.imagesearch.views.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pluang.imagesearch.R

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}