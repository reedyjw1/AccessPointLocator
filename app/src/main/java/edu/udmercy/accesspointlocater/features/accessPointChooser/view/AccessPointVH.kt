package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import edu.udmercy.accesspointlocater.features.home.model.SessionUI
import edu.udmercy.accesspointlocater.features.home.view.SessionVH
import kotlinx.android.synthetic.main.cell_session.view.*

class AccessPointVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    var itemClicked: OnAccessPointClicked? = null

    var entity: AccessPointUI? = null
        set(value) {
            field = value
            value?.let { data ->
                itemView.titleTextView.text = data.macAddress
                itemView.descriptionTextView.text = data.rssi.toString()
                itemView.setOnClickListener { itemClicked?.invoke(data) }
            }
        }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): AccessPointVH {
            return AccessPointVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_session, parent, false)
            )
        }
    }

}