package com.example.kotlinsampleapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.brandio.ads.Controller
import com.brandio.ads.InterscrollerPlacement
import com.brandio.ads.containers.InterscrollerContainer
import com.brandio.ads.exceptions.DioSdkException
import com.example.kotlinsampleapp.R
import kotlin.properties.Delegates


class InterScrollerRVAdapter(
    adPosition: Int,
    private var placementId: String,
    private var requestId: String,
    private var adjustmentHeight: Int,
    private var items: ArrayList<Int?> = (1..40).toList() as ArrayList<Int?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var interScrollerHeight by Delegates.notNull<Int>()
    private val TAG = "InterScrollerRVAdapter"
    private val TYPE_AD = 0
    private val TYPE_CONTENT = 1

    init {
        items.add(adPosition, null)
    }

    @NonNull
    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val context = parent.context.applicationContext
        if (viewType == TYPE_AD) {
            val adView: ViewGroup = InterscrollerContainer.getAdView(context)
            val adHolder = AdHolder(adView)
            adHolder.setParent(parent)
            try {
                val placement =
                    Controller.getInstance().getPlacement(placementId) as InterscrollerPlacement
                placement.parentRecyclerView = parent as RecyclerView
            } catch (e: DioSdkException) {
                e.printStackTrace()
            }

            return adHolder
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.infeed_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) {
            TYPE_AD
        } else {
            TYPE_CONTENT
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_AD && holder is AdHolder) {
            try {
                val placement =
                    Controller.getInstance().getPlacement(placementId) as InterscrollerPlacement
                val container =
                    placement.getContainer(Controller.getInstance().context, requestId, position)
                container.setInterscrollerHeight(holder.parent.height - adjustmentHeight)
                container.bindTo(holder.itemView as ViewGroup)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal class AdHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        lateinit var parent: ViewGroup
        internal fun setParent(parent: ViewGroup) {
            this.parent = parent
        }
    }

    internal class ViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)
}
