package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import kotlinx.android.synthetic.main.fragment_access_chooser.*

class AccessPointChooserFragment: BaseFragment(R.layout.fragment_access_chooser) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = bundleOf("uuid" to arguments?.getString("uuid"))
        toExecuteSession.setOnClickListener {
            findNavController().navigate(R.id.action_accessChooser_to_executeSession, bundle)
        }
    }

}