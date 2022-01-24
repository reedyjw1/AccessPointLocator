package edu.udmercy.accesspointlocater.features.viewSession.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.session.model.SessionUI
import edu.udmercy.accesspointlocater.features.session.view.SessionRecyclerAdapter
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import kotlinx.android.synthetic.main.fragment_view_session.*

class ViewSessionFragment: BaseFragment(R.layout.fragment_view_session) {

    private val viewModel by viewModels<ViewSessionViewModel>()

    private val imageObserver =
        Observer { bitmap: Bitmap? ->
            if(bitmap != null) {
                //buildingImageView.setImageBitmap(bitmap)
            }
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
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
        accessPointInformationRecycler.adapter = adapter
    }

    override fun onNavigationClick() {
        findNavController().popBackStack(R.id.executeSession, false)
        super.onNavigationClick()
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.accessPointInfoList.observe(this, accessPointInfoListObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.accessPointInfoList.removeObserver(accessPointInfoListObserver)
    }

}