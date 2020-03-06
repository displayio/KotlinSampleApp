package com.example.kotlinsampleapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.brandio.ads.*
import com.brandio.ads.ads.Ad
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.exceptions.DioSdkException
import com.brandio.ads.listeners.AdEventListener
import com.brandio.ads.listeners.AdLoadListener
import com.brandio.ads.listeners.AdRequestListener
import com.example.kotlinsampleapp.AdUnitType
import com.example.kotlinsampleapp.MainActivity
import com.example.kotlinsampleapp.MainActivity.Companion.AD_UNIT_NAME
import com.example.kotlinsampleapp.MainActivity.Companion.PLACEMENT_ID
import com.example.kotlinsampleapp.R

class AdUnitFragment : Fragment() {

    private var appId: String? = null
    private var placementId: String? = null
    private var adUnitName: String? = null
    private var requestId: String? = null
    private lateinit var loadedAd: Ad
    private lateinit var rootView: ViewGroup
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
        rootView = inflater.inflate(R.layout.fragment_main, container, false) as ViewGroup
        loadButton = rootView.findViewById<Button>(R.id.load_button);
        showButton = rootView.findViewById<Button>(R.id.show_button);

        loadButton.setOnClickListener { loadAd() }
        showButton.setOnClickListener { showAd() }

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
        return rootView
    }

    private fun loadAd() {
        val placement: Placement
        placement = try {
            Controller.getInstance().getPlacement(placementId)
        } catch (e: DioSdkException) {
            Log.e(adUnitName, " ${e.localizedMessage}")
            return
        }

        val adRequest = placement.newAdRequest()

        adRequest.setAdRequestListener(object : AdRequestListener {
            override fun onAdReceived(adProvider: AdProvider) {
                adProvider.setAdLoadListener(object : AdLoadListener {
                    override fun onLoaded(ad: Ad) {
                        loadedAd = ad
                        requestId = adRequest.id
                        setupShowButton(true)
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
                    Log.e(adUnitName, " ${e.localizedMessage}")
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

        adRequest.requestAd()
    }

    private fun showAd(){
        setupShowButton(false)

        when (adUnitName) {

            AdUnitType.INTERSTITIAL.name, AdUnitType.REWARDED_VIDEO.name -> {
                loadedAd.setEventListener(object : AdEventListener {
                    override fun onShown(ad: Ad) {
                        Log.i(MainActivity.TAG, "onShown")
                    }
                    override fun onFailedToShow(ad: Ad) {
                        Log.i(MainActivity.TAG, "onFailedToShow")
                    }
                    override fun onClicked(ad: Ad) {
                        Log.i(MainActivity.TAG, "onClicked")
                    }
                    override fun onClosed(ad: Ad) {
                        Log.i(MainActivity.TAG, "onClosed")
                    }
                    override fun onAdCompleted(ad: Ad) {
                        Log.i(MainActivity.TAG, "onAdCompleted")
                    }
                })
                loadedAd.showAd(activity)
            }

            AdUnitType.INFEED.name, AdUnitType.INTERSCROLLER.name -> {

//                val intent = Intent(activity, LoadInfeedActivity::class.java)    //TODO



//                intent.putExtra(PLACEMENT_ID, placementId)
//                intent.putExtra(AD_UNIT_NAME, adUnitName)
//                intent.putExtra(REQUEST_ID, requestId)
//                startActivity(intent)
            }

            AdUnitType.BANNER.name -> {
                try {
                    val bannerPlacement = Controller.getInstance().getPlacement(placementId) as BannerPlacement
                    val bannerView = bannerPlacement.getBanner(activity, requestId)

                    if (bannerView != null) {
                        val param: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        param.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        param.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        param.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        bannerView.layoutParams = param
                        rootView.addView(bannerView)
                    }

                } catch (e: DioSdkException) {
                    Log.e(adUnitName, " ${e.localizedMessage}")
                }
            }

            AdUnitType.MEDIUM_RECTANGLE.name -> {
                try {
                    val mRectPlacement = Controller.getInstance().getPlacement(placementId) as MediumRectanglePlacement
                    val mRectView = mRectPlacement.getMediumRectangle(activity, requestId)

                    if (mRectView != null) {
                        val param: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                        param.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        param.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        param.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        mRectView.layoutParams = param
                        rootView.addView(mRectView)
                    }

                } catch (e: DioSdkException) {
                    Log.e(adUnitName, " ${e.localizedMessage}")
                }
            }
        }

    }

    private fun setupShowButton(show: Boolean) {
        (activity as MainActivity).apply {
            sdkInitModel.showEnabled.value = show
        }
    }

    private fun setupLoadButton(show: Boolean) {
        (activity as MainActivity).apply {
            sdkInitModel.loadEnabled.value = show
        }
    }

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