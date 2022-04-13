package edu.udmercy.accesspointlocater.features.home.model

import androidx.recyclerview.widget.DiffUtil

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