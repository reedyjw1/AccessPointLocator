package edu.udmercy.accesspointlocater.features.viewSession.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import kotlinx.android.synthetic.main.cell_ap_data.view.*
import kotlinx.android.synthetic.main.cell_session.view.*
import kotlin.coroutines.coroutineContext

class AccessPointInfoVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    var entity: AccessPointInfo? = null
        set(value) {
            field = value
            value?.let { info ->
                // Assigns data of cell in list to UI elements so that they can be displayed to user
                itemView.accessPointNumber.text = itemView.resources.getString(R.string.apTitleLabel, info.apNumber)
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