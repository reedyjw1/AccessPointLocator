package edu.udmercy.accesspointlocater.features.viewSession.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import kotlinx.android.synthetic.main.cell_ap_data.view.*
import kotlinx.android.synthetic.main.cell_session.view.*

class AccessPointInfoVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    var entity: AccessPointInfo? = null
        set(value) {
            field = value
            value?.let { info ->
                itemView.accessPointNumber.text = info.floorNumber.toString()
                itemView.accessPointSSID.text = info.ssid
            }
        }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): AccessPointInfoVH {
            return AccessPointInfoVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_ap_data, parent, false)
            )
        }
    }

}