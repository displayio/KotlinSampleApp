package com.example.kotlinsampleapp.ui.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.brandio.ads.Controller
import com.brandio.ads.HeadlineVideoPlacement
import com.brandio.ads.containers.HeadlineVideoAdContainer
import com.brandio.ads.exceptions.DioSdkException
import com.brandio.ads.listeners.HeadlineVideoSnapListener
import com.example.kotlinsampleapp.R


class HeadlineVideoRVAdapter(
    var adPosition: Int,
    private val placementId: String,
    private val requestId: String,
    private var items: ArrayList<Int?> = listOf(1..40).flatten() as ArrayList<Int?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var context: Context? = null

    init {
        items.add(adPosition, null)
    }

    @NonNull
    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        context = parent.context.applicationContext
        return when (viewType) {
            TYPE_AD -> AdViewHolder(
                HeadlineVideoAdContainer.getAdView(context)
            )
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
                val headlineVideoPlacement = Controller.getInstance()
                    .getPlacement(placementId) as HeadlineVideoPlacement
                val container =
                    headlineVideoPlacement.getHeadLineVideoContainer(context, requestId)
                container.bindTo(holder.itemView as ViewGroup)
            } catch (e: DioSdkException) {
                Log.e(
                    TAG,
                    e.localizedMessage
                )
            }
        }
    }

    internal inner class ItemViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    internal inner class AdViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    companion object {
        private const val TAG = "HeadlineListAdapter"
        private const val TYPE_AD = 0
        private const val TYPE_CONTENT = 1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : HeadlineVideoSnapListener(adPosition) {
            override fun removeAdPositionFromList(adPosition: Int) {
                items.removeAt(adPosition)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
