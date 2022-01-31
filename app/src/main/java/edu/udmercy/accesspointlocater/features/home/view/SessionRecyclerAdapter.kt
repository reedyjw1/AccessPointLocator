package edu.udmercy.accesspointlocater.features.home.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.home.model.SessionUI

typealias OnItemClicked = (SessionUI) -> Unit

class SessionRecyclerAdapter: ListAdapter<SessionUI, SessionVH>(SessionUI.DIFFER) {

    var onItemClicked: OnItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionVH =
        SessionVH.create(parent, viewType).apply {
            itemClicked = onItemClicked
        }

    override fun onBindViewHolder(holder: SessionVH, position: Int) {
        holder.entity = getItem(position)
    }

}