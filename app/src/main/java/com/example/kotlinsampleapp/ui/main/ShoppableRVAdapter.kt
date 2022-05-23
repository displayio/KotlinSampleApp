
package com.example.kotlinsampleapp.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.brandio.ads.Controller
import com.brandio.ads.InfeedPlacement
import com.brandio.ads.ShoppablePlacement
import com.brandio.ads.containers.InfeedAdContainer
import com.brandio.ads.exceptions.DioSdkException
import com.example.kotlinsampleapp.R
import kotlin.collections.ArrayList

class ShoppableRVAdapter(
    adPosition: Int,
    private var placementId: String,
    private var requestId: String,
    private var items: ArrayList<Int?> = listOf(1..40).flatten() as ArrayList<Int?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "ShoppableRVAdapter"
    private val TYPE_AD = 0
    private val TYPE_CONTENT = 1

    init {
        items.add(adPosition, null)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context.applicationContext
        return when (viewType) {
            TYPE_AD -> AdViewHolder(InfeedAdContainer.getAdView(context))
            else -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.infeed_list_item, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) {
            TYPE_AD
        } else {
            TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_AD && holder is AdViewHolder) {
            try {
                val placement =
                    Controller.getInstance().getPlacement(placementId) as ShoppablePlacement
                val shoppableView =
                    placement.getShoppableAdView(Controller.getInstance().context, requestId)
               if (shoppableView != null) {
                   (holder.itemView as RelativeLayout).addView(shoppableView)
               } else {
                   (holder.itemView as RelativeLayout).removeAllViews()
               }
            } catch (e: DioSdkException) {
                Log.e(TAG, e.localizedMessage)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal class ItemViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    internal class AdViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)
}
