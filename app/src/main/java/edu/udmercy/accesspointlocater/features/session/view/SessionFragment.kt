package edu.udmercy.accesspointlocater.features.session.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.session.model.SessionUI
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.android.synthetic.main.fragment_session.*

class SessionFragment: Fragment(R.layout.fragment_session) {

    private companion object {
        private const val TAG = "SessionFragment"
    }
    
    private val viewModel by viewModels<SessionViewModel>()
    private val adapter by lazy {
        SessionRecyclerAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val sessionListObserver =
        Observer { list: List<SessionUI>? ->
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { findNavController().navigate(R.id.action_sessionList_to_createSession) }
        recyclerView.adapter = adapter
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