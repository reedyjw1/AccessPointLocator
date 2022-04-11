package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.inputMAC.view.MACAddressDialog
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.APPointLocation
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.TouchPointListener
import edu.udmercy.accesspointlocater.features.viewSession.view.ViewSessionFragment
import kotlinx.android.synthetic.main.fragment_place_access_points.*
import kotlinx.android.synthetic.main.fragment_view_session.*

class PlaceAccessPointsFragment : BaseFragment(R.layout.fragment_place_access_points) {


    // Things to do
    // load in images for correct session - DONE
    // hookup buttons to move between floor images - DONE
    // after placing a point ask for its mac address - DONE
    // save point to viewModel - DONE
    // change points per floor - DONE
    // save point to database



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
        Observer { points: MutableList<APPointLocation> ->
            val currentFloor = viewModel.currentFloorNumber.value
            currentFloor?.let { floor ->
                //filter the points to correct floor, then map only the points to the list of touch points
                apLocationPlacer.touchPoints = points.filter { it.floor == floor }.map { it.point }.toMutableList()
                apLocationPlacer.invalidate()
            }
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
            val wasValueRemoved = viewModel.apPoints.value?.removeIf { it.point == point }
            if (wasValueRemoved == true){
                Log.d(TAG, "inflateMACDialog: Point Removed - $point")
            }

        }

    }

    private fun inflateMACDialog(point: PointF){
        var received = false
        childFragmentManager.setFragmentResultListener("macAddress", viewLifecycleOwner, { requestKey, data ->
           if(requestKey == "macAddress" && !received){
               val item = data.getString("result")
               item?.let { mac ->
                   Log.d(TAG, "inflateMACDialog: Result: $mac")
                   viewModel.currentFloorNumber.value?.let { floor ->
                       val newPoint = APPointLocation(point, floor, mac)
                       val allPoints = viewModel.apPoints.value
                       allPoints?.add(newPoint)
                       allPoints?.forEach {
                           val values = it.logValues()
                           Log.d(TAG, "inflateMACDialog: Point Added - List = $values}")
                       }

                       allPoints?.let{ points ->
                           viewModel.apPoints.postValue(points)
                       }
                       received = true
                   }
               }
           }
        })
        MACAddressDialog().show(childFragmentManager, "macAddress")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
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

    private fun savePoints(uuid:String){
        viewModel.savePointsToDB(uuid)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_aps_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save ->{
                val uuid = arguments?.getString("uuid")
                uuid?.let {
                    savePoints(uuid)
                    viewModel.markSessionComplete(uuid)

                    val bundle = bundleOf("uuid" to uuid)
                    findNavController().navigate(R.id.knownAP_to_viewSession, bundle)

                }
                if(uuid == null){
                    Toast.makeText(this.requireContext(), "Error: UUID is null", Toast.LENGTH_SHORT).show()
                }

            }
        }
        return super.onOptionsItemSelected(item)
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