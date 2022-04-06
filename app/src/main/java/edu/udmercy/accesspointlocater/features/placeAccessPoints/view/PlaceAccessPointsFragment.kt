package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment

class PlaceAccessPointsFragment : BaseFragment(R.layout.fragment_place_access_points) {

    companion object {
        private const val TAG = "PlaceAccessPointsFragment"
    }


    private val viewModel by viewModels<PlaceAccessPointsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: return
    }



}