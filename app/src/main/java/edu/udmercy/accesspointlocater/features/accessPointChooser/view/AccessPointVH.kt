package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.annotation.SuppressLint
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
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            value?.let { data ->
                // For the data in this list element, display it to the screen
                itemView.titleTextView.text = data.macAddress
                itemView.descriptionTextView.text = "RSSI: ${data.rssi} Frequency: ${data.frequency}"
                itemView.setOnClickListener { itemClicked?.invoke(data) }
                if (data.selected) {
                    itemView.checkmarkImageView.visibility = View.VISIBLE
                } else {
                    itemView.checkmarkImageView.visibility = View.INVISIBLE
                }
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