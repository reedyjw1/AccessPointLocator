package edu.udmercy.accesspointlocater.features.home.model

import androidx.recyclerview.widget.DiffUtil

/**
 * Data Model to be used on the UI. Session data will be mapped to this and presented on the UI
 * The companion object is utilized by the recycler adapter to automatically know when the data changes
 */
data class SessionUI (
    val uid: String,
    val name: String,
    val desc: String,
    val date: String,
    val isFinished: Boolean,
    val apsAreKnown: Boolean
){
    companion object{
        val DIFFER = object: DiffUtil.ItemCallback<SessionUI>(){
            override fun areItemsTheSame(oldItem: SessionUI, newItem: SessionUI): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: SessionUI, newItem: SessionUI): Boolean {
                return oldItem == newItem
            }

        }
    }
}