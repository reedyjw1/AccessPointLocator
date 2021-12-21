package edu.udmercy.accesspointlocater.features.session.sub.viewSession

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.features.session.model.SessionUI
import kotlinx.android.synthetic.main.fragment_view_session.*

class ViewSessionFragment: Fragment(R.layout.fragment_view_session) {

    private val viewModel by viewModels<ViewSessionViewModel>()

    private val imageObserver =
        Observer { bitmap: Bitmap? ->
            if(bitmap != null) {
                buildingImageView.setImageBitmap(bitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
    }
}