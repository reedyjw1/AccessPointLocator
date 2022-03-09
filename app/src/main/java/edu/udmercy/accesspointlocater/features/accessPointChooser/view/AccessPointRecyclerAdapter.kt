package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import edu.udmercy.accesspointlocater.features.home.model.SessionUI

typealias OnAccessPointClicked = (AccessPointUI) -> Unit

class AccessPointRecyclerAdapter: ListAdapter<AccessPointUI, AccessPointVH>(AccessPointUI.DIFFER) {

    var onAccessPointClicked: OnAccessPointClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessPointVH =
        AccessPointVH.create(parent, viewType).apply {
            itemClicked = onAccessPointClicked
        }

    override fun onBindViewHolder(holder: AccessPointVH, position: Int) {
        holder.entity = getItem(position)
    }

}