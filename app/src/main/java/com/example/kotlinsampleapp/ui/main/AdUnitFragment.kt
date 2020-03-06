package com.example.kotlinsampleapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.brandio.ads.AdProvider
import com.brandio.ads.Controller
import com.brandio.ads.Placement
import com.brandio.ads.ads.Ad
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.exceptions.DioSdkException
import com.brandio.ads.listeners.AdLoadListener
import com.brandio.ads.listeners.AdRequestListener
import com.example.kotlinsampleapp.MainActivity
import com.example.kotlinsampleapp.MainActivity.Companion.AD_UNIT_NAME
import com.example.kotlinsampleapp.MainActivity.Companion.PLACEMENT_ID
import com.example.kotlinsampleapp.R

class AdUnitFragment : Fragment() {

    private var appId: String? = null
    private var placementId: String? = null
    private var adUnitName: String? = null
    private var loadedAd: Ad? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appId = arguments?.getString(APP_ID)
        placementId = arguments?.getString(PLACEMENT_ID)
        adUnitName = arguments?.getString(AD_UNIT_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_main, container, false)
        loadButton = root.findViewById<Button>(R.id.load_button);
        showButton = root.findViewById<Button>(R.id.show_button);

        loadButton.setOnClickListener { loadAd() }

        (activity as MainActivity).apply {

            sdkInitModel.loadEnabled.observe(viewLifecycleOwner,
                Observer {
                    loadButton.isEnabled = it
                })
            sdkInitModel.showEnabled.observe(viewLifecycleOwner,
                Observer {
                    showButton.isEnabled = it
                })
        }
        return root
    }

    private fun loadAd() {
        val placement: Placement
        placement = try {
            Controller.getInstance().getPlacement(placementId)
        } catch (e: DioSdkException) {
            Log.e(adUnitName, e.localizedMessage)
            return
        }

        val adRequest = placement.newAdRequest()

        adRequest.setAdRequestListener(object : AdRequestListener {
            override fun onAdReceived(adProvider: AdProvider) {
                adProvider.setAdLoadListener(object : AdLoadListener {
                    override fun onLoaded(ad: Ad) {
                        loadedAd = ad
                        (activity as MainActivity).apply {
                            sdkInitModel.showEnabled.value = true
                        }
                    }

                    override fun onFailedToLoad(error: DIOError) {
                        Toast.makeText(
                            activity,
                            "Ad for placement $placementId failed to load",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
                try {
                    adProvider.loadAd()
                } catch (e: DioSdkException) {
                    Log.e(adUnitName, e.localizedMessage)
                }
            }

            override fun onNoAds(error: DIOError) {
                Toast.makeText(
                    activity,
                    "No Ads placement $placementId",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        adRequest.requestAd()    }
    
    companion object {
        private const val APP_ID = "appId"

        @JvmStatic
        fun newInstance(appId: String, placementId: String, adUnitName: String): AdUnitFragment {
            return AdUnitFragment().apply {
                arguments = Bundle().apply {
                    putString(APP_ID, appId)
                    putString(AD_UNIT_NAME, adUnitName)
                    putString(PLACEMENT_ID, placementId)
                }
            }
        }
    }
}