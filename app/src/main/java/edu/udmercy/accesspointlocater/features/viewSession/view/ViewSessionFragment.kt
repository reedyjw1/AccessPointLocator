package edu.udmercy.accesspointlocater.features.viewSession.view

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import kotlinx.android.synthetic.main.fragment_view_session.*


class ViewSessionFragment: BaseFragment(R.layout.fragment_view_session) {
    companion object {
        private const val TAG = "ViewSessionFragment"
    }
    private val viewModel by viewModels<ViewSessionViewModel>()

    // These observers are used so that when the data is updated, the UI can automatically be updated
    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            if(bitmap != null) {
                accessPointImage.setImage(ImageSource.bitmap(bitmap.image))
            }
        }

    private val apLocationObserever =
        Observer { points: MutableList<Pair<Int, PointF>> ->
            Log.d(TAG, "Points: $points")
            accessPointImage.touchPoints = points
            accessPointImage.invalidate()

        }

    @SuppressLint("SetTextI18n")
    private val floorObserver =
        Observer { number: Int ->
            accessViewerFloor.text = "Floor ${number+1}"
        }

    private val adapter by lazy {
        AccessPointInfoRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val accessPointInfoListObserver =
        Observer { list: List<AccessPointInfo>? ->
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        // Sets up recycler view and button callbacks when fragment is first created
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
        accessPointInformationRecycler.adapter = adapter
        val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        accessPointInformationRecycler.addItemDecoration(decor)
        accessViewerPreviousFloorBtn.setOnClickListener { viewModel.moveImage(-1, uuid) }
        accessViewerNextFloorBtn.setOnClickListener { viewModel.moveImage(1, uuid) }
    }

    override fun onNavigationClick() {
        super.onNavigationClick()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.accessPointInfoList.observe(this, accessPointInfoListObserver)
        viewModel.currentFloor.observe(this, floorObserver)
        viewModel.accessPointLocations.observe(this, apLocationObserever)

    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.accessPointInfoList.removeObserver(accessPointInfoListObserver)
        viewModel.currentFloor.removeObserver(floorObserver)
        viewModel.currentBitmap.postValue(null)
        viewModel.accessPointLocations.removeObserver(apLocationObserever)
    }

}