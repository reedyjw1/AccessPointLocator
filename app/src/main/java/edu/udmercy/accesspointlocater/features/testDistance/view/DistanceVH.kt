package edu.udmercy.accesspointlocater.features.testDistance.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.testDistance.model.DistanceUI
import kotlinx.android.synthetic.main.cell_session.view.*

class DistanceVH(itemView: View): RecyclerView.ViewHolder(itemView) {


    var entity: DistanceUI? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            value?.let { data ->
                itemView.titleTextView.text = data.macAddress
                itemView.descriptionTextView.text = "RSSI: ${data.rssi} Frequency: ${data.frequency} Distance: ${data.distance}"
            }
        }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): DistanceVH {
            return DistanceVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_session, parent, false)
            )
        }
    }

}