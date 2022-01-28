package com.example.kotlinsampleapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.example.kotlinsampleapp.MainActivity.Companion.AD_IS_DISPLAYING
import com.example.kotlinsampleapp.MainActivity.Companion.REQUEST_ID
import com.example.kotlinsampleapp.R

class AdUnitFragment : Fragment() {


    private var appId: String? = null
    private lateinit var placementId: String
    private var adUnitName: String? = null
    private lateinit var requestId: String
    private lateinit var loadedAd: Ad
    private lateinit var rootView: ViewGroup
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var recyclerView: RecyclerView

    private var adIsDisplaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appId = arguments?.getString(APP_ID)
        placementId = arguments?.getString(PLACEMENT_ID)!!
        adUnitName = arguments?.getString(AD_UNIT_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false) as ViewGroup
        loadButton = rootView.findViewById<Button>(R.id.load_button);
        showButton = rootView.findViewById<Button>(R.id.show_button);
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

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

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(AD_IS_DISPLAYING, false)){
                requestId = savedInstanceState.getString(REQUEST_ID).toString();
                showAd()
            }
        }
        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (adIsDisplaying) {
            outState.putBoolean(AD_IS_DISPLAYING, adIsDisplaying)
            outState.putString(REQUEST_ID, requestId)
        }
        super.onSaveInstanceState(outState)
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

    private fun showAd() {
        setupShowButton(false)
        adIsDisplaying = true

        loadedAd.setEventListener(object : AdEventListener() {
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
                adIsDisplaying = false
                Log.i(MainActivity.TAG, "onClosed")
            }

            override fun onAdCompleted(ad: Ad) {
                Log.i(MainActivity.TAG, "onAdCompleted")
            }
        })

        when (adUnitName) {

            AdUnitType.INTERSTITIAL.name -> {
                loadedAd.showAd(activity)
            }

            AdUnitType.INFEED.name -> {
                recyclerView.adapter = InfeedRVAdapter(12, placementId, requestId)
            }

            AdUnitType.HEADLINE_VIDEO_SNAP.name, AdUnitType.HEADLINE_VIDEO_NO_SNAP.name -> {
                recyclerView.adapter = HeadlineRVAdapter(12, placementId, requestId)
            }

            AdUnitType.INTERSCROLLER.name -> {
                val buttonContainerHeight = rootView.findViewById<LinearLayout>(R.id.button_container).height
                recyclerView.adapter = InterScrollerRVAdapter(12, placementId, requestId, buttonContainerHeight)
            }

            AdUnitType.BANNER.name -> {
                try {
                    val bannerPlacement =
                        Controller.getInstance().getPlacement(placementId) as BannerPlacement
                    val bannerView = bannerPlacement.getBanner(activity, requestId)

                    if (bannerView != null) {
                        val param: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                        )
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
                    val mRectPlacement = Controller.getInstance()
                        .getPlacement(placementId) as MediumRectanglePlacement
                    val mRectView = mRectPlacement.getMediumRectangle(activity, requestId)

                    if (mRectView != null) {
                        val param: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT
                        )
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
            if (sdkInitModel != null) {
                sdkInitModel.showEnabled.value = show
            }
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