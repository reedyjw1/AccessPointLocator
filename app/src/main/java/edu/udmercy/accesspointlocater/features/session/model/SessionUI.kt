package edu.udmercy.accesspointlocater.features.session.model

import androidx.recyclerview.widget.DiffUtil

data class SessionUI (
    val uid: String,
    val name: String,
    val desc: String,
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