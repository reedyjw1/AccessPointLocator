package edu.udmercy.accesspointlocater.features.accessPointChooser.model

import androidx.recyclerview.widget.DiffUtil

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