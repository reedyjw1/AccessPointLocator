package edu.udmercy.accesspointlocater.features.accessPointChooser.model

import androidx.recyclerview.widget.DiffUtil

/**
 * Data Model to be used on the UI. APLocation data will be mapped to this and presented on the UI
 * The companion object is utilized by the recycler adapter to automatically know when the data changes
 */
data class AccessPointUI(
    val macAddress: String,
    val rssi: Int,
    val frequency: Int,
    val selected: Boolean
){
    companion object{
        val DIFFER = object: DiffUtil.ItemCallback<AccessPointUI>(){
            override fun areItemsTheSame(oldItem: AccessPointUI, newItem: AccessPointUI): Boolean {
                return oldItem.macAddress == newItem.macAddress
            }

            override fun areContentsTheSame(oldItem: AccessPointUI, newItem: AccessPointUI): Boolean {
                return oldItem == newItem
            }

        }
    }
}