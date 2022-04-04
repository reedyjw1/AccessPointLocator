package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment

class PlaceAccessPointsFragment : BaseFragment(R.layout.place_access_points_fragment) {



    private val viewModel by viewModels<PlaceAccessPointsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}