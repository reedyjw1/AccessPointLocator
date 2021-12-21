package edu.udmercy.accesspointlocater.features.session.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.session.model.SessionUI

class SessionRecyclerAdapter: ListAdapter<SessionUI, SessionVH>(SessionUI.DIFFER) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionVH =
        SessionVH.create(parent, viewType)

    override fun onBindViewHolder(holder: SessionVH, position: Int) {
        holder.entity = getItem(position)
    }

}