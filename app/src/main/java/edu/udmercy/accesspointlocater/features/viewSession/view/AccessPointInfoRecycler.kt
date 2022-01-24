package edu.udmercy.accesspointlocater.features.viewSession.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo

class AccessPointInfoRecycler:  ListAdapter<AccessPointInfo, AccessPointInfoVH>(AccessPointInfo.DIFFER) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessPointInfoVH =
        AccessPointInfoVH.create(parent, viewType)

    override fun onBindViewHolder(holder: AccessPointInfoVH, position: Int) {
        holder.entity = getItem(position)
    }
}