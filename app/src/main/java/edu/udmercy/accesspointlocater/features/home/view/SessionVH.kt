package edu.udmercy.accesspointlocater.features.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.home.model.SessionUI
import kotlinx.android.synthetic.main.cell_session.view.*

class SessionVH(itemView: View): RecyclerView.ViewHolder(itemView) {

    var itemClicked: OnItemClicked? = null

    var entity: SessionUI? = null
        set(value) {
            field = value
            value?.let { session ->
                itemView.titleTextView.text = session.name
                itemView.descriptionTextView.text = session.desc
                itemView.dateTextView.text = session.date
                if (session.isFinished) {
                    itemView.checkmarkImageView.visibility = View.VISIBLE
                } else {
                    itemView.checkmarkImageView.visibility = View.INVISIBLE
                }
                itemView.setOnClickListener { itemClicked?.invoke(session) }
            }
        }

    companion object {
        fun create(parent: ViewGroup, viewType: Int): SessionVH {
            return SessionVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_session, parent, false)
            )
        }
    }


}