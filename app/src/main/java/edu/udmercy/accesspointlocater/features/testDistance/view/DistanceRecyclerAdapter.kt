package edu.udmercy.accesspointlocater.features.testDistance.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import edu.udmercy.accesspointlocater.features.testDistance.model.DistanceUI

class DistanceRecyclerAdapter: ListAdapter<DistanceUI, DistanceVH>(DistanceUI.DIFFER) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistanceVH =
        DistanceVH.create(parent, viewType)

    override fun onBindViewHolder(holder: DistanceVH, position: Int) {
        holder.entity = getItem(position)
    }

}