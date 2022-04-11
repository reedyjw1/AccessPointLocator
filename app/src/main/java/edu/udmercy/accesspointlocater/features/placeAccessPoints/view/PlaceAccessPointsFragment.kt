package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.inputMAC.view.MACAddressDialog
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.TouchPointListener
import edu.udmercy.accesspointlocater.features.viewSession.view.ViewSessionFragment
import kotlinx.android.synthetic.main.fragment_place_access_points.*
import kotlinx.android.synthetic.main.fragment_view_session.*

class PlaceAccessPointsFragment : BaseFragment(R.layout.fragment_place_access_points) {


    // Things to do
    // load in images for correct session
    // hookup buttons to move between floor images
    // after placing a point ask for its mac address
    // save point to viewModel
    // save point to database
    // if point is deleted, delete from database


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

    private val apLocationObserever =
        Observer { points: MutableList<Pair<String, PointF>> ->
            apLocationPlacer.touchPoints = points.map { it.second }.toMutableList()
            apLocationPlacer.invalidate()

        }

    private val floorTextObserver =
        Observer { floorNumber: Int ->
            placerfloorNumberBtn.text = resources.getString(R.string.floorNumber, floorNumber+1)
        }

    //listen for addition and removal of ap points on map
    private val touchPointListener = object: TouchPointListener {
        override fun onPointAdded(point: PointF) {
            inflateMACDialog(point)
        }

        override fun onPointRemoved(point: PointF) {
           viewModel.apPoints.value?.removeIf { it.second == point }
            Log.d(TAG, "inflateMACDialog: Point Removed - List = ${viewModel.apPoints.value}")
        }

    }

    private fun inflateMACDialog(point: PointF){
        var received = false
        childFragmentManager.setFragmentResultListener("macAddress", viewLifecycleOwner, { requestKey, data ->
           if(requestKey == "macAddress" && !received){
               val item = data.getString("result")
               item?.let {
                   Log.d(TAG, "inflateMACDialog: Result: $it")
                   val newPoint = Pair(it, point)
                   val allPoints = viewModel.apPoints.value
                   allPoints?.add(newPoint)
                   Log.d(TAG, "inflateMACDialog: Point Added - List = $allPoints")
                   allPoints?.let{ points ->
                       viewModel.apPoints.postValue(points)
                   }
                   received = true
               }
           }
        })
        MACAddressDialog().show(childFragmentManager, "macAddress")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.initializeImages(uuid)
        apLocationPlacer.listener = touchPointListener
        placerPreviousFloorBtn.setOnClickListener {
            viewModel.changeFloor(uuid, -1)
        }
        placerNextFloorBtn.setOnClickListener {
            viewModel.changeFloor(uuid, 1)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentDisplayImage.removeObserver(imageObserver)
        viewModel.currentFloorNumber.removeObserver(floorTextObserver)
        viewModel.apPoints.removeObserver(apLocationObserever)
    }

    override fun onResume() {
        super.onResume()
        viewModel.apPoints.observe(this, apLocationObserever)
        viewModel.currentDisplayImage.observe(this, imageObserver)
        viewModel.currentFloorNumber.observe(this, floorTextObserver)
    }

}