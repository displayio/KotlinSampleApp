package com.example.kotlinsampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.brandio.ads.Controller
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.SdkInitListener
import com.example.kotlinsampleapp.ui.main.SDKInitViewModel
import com.example.kotlinsampleapp.ui.main.SectionsPagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class MainActivity : AppCompatActivity() {
     lateinit var sdkInitModel: SDKInitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        sdkInitModel = ViewModelProviders.of(this).get(SDKInitViewModel::class.java).apply {
            loadEnabled.value = false
            showEnabled.value = false
        }

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {
                sdkInitModel.showEnabled.value = false
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        if (!Controller.getInstance().isInitialized) {
            Controller.getInstance().init(
                this,
                null,
                APP_ID,
                object : SdkInitListener {
                    override fun onInit() {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "SDK initialized and ready.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        sdkInitModel.loadEnabled.value = true
                    }

                    override fun onInitError(p0: DIOError?) {
                        val snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            "Failed to initialize SDK. ${p0?.message}",
                            Snackbar.LENGTH_INDEFINITE
                        )
                        snackbar.setAction("dismiss") {
                            snackbar.dismiss()
                        }
                        snackbar.show()
                        sdkInitModel.loadEnabled.value = false
                        sdkInitModel.showEnabled.value = false
                    }
                }
            )
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val PLACEMENT_ID = "placementId"
        const val AD_UNIT_NAME = "adUnitType"
        const val APP_ID = "6494"
    }
}