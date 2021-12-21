package edu.udmercy.accesspointlocater.features.session.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.fragment_session.*

class SessionFragment: Fragment(R.layout.fragment_session) {

    private companion object {
        private const val TAG = "SessionFragment"
    }
    
    private val viewModel by viewModels<SessionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { findNavController().navigate(R.id.action_sessionList_to_createSession) }
    }
}