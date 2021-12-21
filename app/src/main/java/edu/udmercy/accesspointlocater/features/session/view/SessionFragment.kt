package edu.udmercy.accesspointlocater.features.session.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.udmercy.accesspointlocater.R

class SessionFragment: Fragment(R.layout.fragment_session) {

    private companion object {
        private const val TAG = "SessionFragment"
    }
    
    private val viewModel by viewModels<SessionViewModel>()
}