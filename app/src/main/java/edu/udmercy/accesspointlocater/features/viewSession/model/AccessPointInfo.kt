package edu.udmercy.accesspointlocater.features.viewSession.model

import androidx.recyclerview.widget.DiffUtil

data class AccessPointInfo(
    val floorNumber: String,
    val ssid: String,
    val uuid: String
) {

    companion object{
        val DIFFER = object: DiffUtil.ItemCallback<AccessPointInfo>(){
            override fun areItemsTheSame(oldItem: AccessPointInfo, newItem: AccessPointInfo): Boolean {
                return oldItem.ssid == newItem.ssid
            }

            override fun areContentsTheSame(oldItem: AccessPointInfo, newItem: AccessPointInfo): Boolean {
                return oldItem == newItem
            }

        }
    }

}