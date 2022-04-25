package edu.udmercy.accesspointlocater.features.home.view

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.home.model.SessionUI

typealias OnItemClicked = (SessionUI) -> Unit
typealias OnItemRemoved = (SessionUI) -> Unit

class SessionRecyclerAdapter: ListAdapter<SessionUI, SessionVH>(SessionUI.DIFFER) {

    companion object {
        private const val TAG = "SessionRecyclerAdapter"
    }

    var onItemClicked: OnItemClicked? = null
    var onItemRemoved: OnItemRemoved? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionVH =
        SessionVH.create(parent, viewType).apply {
            itemClicked = onItemClicked
        }

    override fun onBindViewHolder(holder: SessionVH, position: Int) {
        holder.entity = getItem(position)

    }

    fun deleteItem(position: Int){
        val currentItems = currentList.toMutableList()
        val removedItem = currentItems.removeAt(position)
        submitList(currentItems)
        onItemRemoved?.invoke(removedItem)
        Log.d(TAG, "deleteItem: $position")
    }

}