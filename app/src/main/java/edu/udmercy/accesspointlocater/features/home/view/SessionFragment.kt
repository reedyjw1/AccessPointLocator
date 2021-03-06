package edu.udmercy.accesspointlocater.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
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
            // Depending on the current state of the session, go to the correct fragment
            onItemClicked = {
                val bundle = bundleOf("uuid" to it.uid)
                if(it.isFinished) {
                    findNavController().navigate(R.id.action_sessionList_to_viewSession, bundle)
                } 
                else if(it.apsAreKnown) {
                    findNavController().navigate(R.id.action_sessionList_to_KnownAPLocationsPlacer, bundle)
                }
                else {
                    findNavController().navigate(R.id.action_sessionList_to_Execute, bundle)
                }
            }
            onItemRemoved = {
                viewModel.deleteSession(it.uid)
                Log.d(TAG, "DeleteSession - UUID: ${it.uid}")
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
        // Sets up NavBar and button listeners
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        val touchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        touchHelper.attachToRecyclerView(recyclerView)
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