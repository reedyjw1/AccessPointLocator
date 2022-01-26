package edu.udmercy.accesspointlocater.features.viewSession.model

import androidx.recyclerview.widget.DiffUtil

data class AccessPointInfo(
    val floorNumber: Int,
    val ssid: String,
    val uuid: String,
    val apNumber: Int,
    val xCoordinate: Double,
    val yCoordinate: Double,
    val zCoordinate: Double
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