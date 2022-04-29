package edu.udmercy.accesspointlocater.features.testDistance.model

import androidx.recyclerview.widget.DiffUtil

/**
 * Data Model to be used on the UI. APLocation data will be mapped to this and presented on the UI
 * The companion object is utilized by the recycler adapter to automatically know when the data changes
 */
data class DistanceUI(
    val macAddress: String,
    val rssi: Int,
    val frequency: Int,
    val distance: Double
){
    companion object{
        val DIFFER = object: DiffUtil.ItemCallback<DistanceUI>(){
            override fun areItemsTheSame(oldItem: DistanceUI, newItem: DistanceUI): Boolean {
                return oldItem.macAddress == newItem.macAddress
            }

            override fun areContentsTheSame(oldItem: DistanceUI, newItem: DistanceUI): Boolean {
                return oldItem == newItem
            }

        }
    }
}