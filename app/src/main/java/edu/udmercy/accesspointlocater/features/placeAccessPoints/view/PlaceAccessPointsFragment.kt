package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import kotlinx.android.synthetic.main.fragment_place_access_points.*
import kotlinx.android.synthetic.main.fragment_view_session.*

class PlaceAccessPointsFragment : BaseFragment(R.layout.fragment_place_access_points) {


    // Things to do
    // load in images for correct session
    // hookup buttons to move between floor images
    // allow for multiple placements per floor
    // save placements as AP location object for session


    companion object {
        private const val TAG = "PlaceAccessPointsFragment"
    }

    private val viewModel by viewModels<PlaceAccessPointsViewModel>()

    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            if(bitmap != null) {
                apLocationPlacer.setImage(ImageSource.bitmap(bitmap.image))
            }
        }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.initializeImages(uuid)
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentDisplayImage.removeObserver(imageObserver)
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentDisplayImage.observe(this, imageObserver)
    }

}