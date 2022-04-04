package edu.udmercy.accesspointlocater.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.home.model.SessionUI
import kotlinx.android.synthetic.main.fragment_session.*

class SessionFragment: BaseFragment(R.layout.fragment_session) {

    private companion object {
        private const val TAG = "SessionFragment"
    }
    
    private val viewModel by viewModels<SessionViewModel>()
    private val adapter by lazy {
        SessionRecyclerAdapter().apply {
            onItemClicked = {
                val bundle = bundleOf("uuid" to it.uid)
                if(it.isFinished) {
                    findNavController().navigate(R.id.action_sessionList_to_viewSession, bundle)
                } 
                else if(it.apsAreKnown) {
                    Log.d(TAG, "sessionAdapter: Ap Locations are Known")
                }
                else {
                    findNavController().navigate(R.id.action_sessionList_to_accessChooser, bundle)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val sessionListObserver =
        Observer { list: List<SessionUI>? ->
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        fab.setOnClickListener { findNavController().navigate(R.id.action_sessionList_to_createSession) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.sessionList.observe(this, sessionListObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.sessionList.removeObserver(sessionListObserver)
    }
}