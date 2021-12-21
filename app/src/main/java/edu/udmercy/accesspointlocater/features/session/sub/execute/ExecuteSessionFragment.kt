package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.fragment_view_session.*

class ExecuteSessionFragment: Fragment(R.layout.fragment_execute_session) {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    private val imageObserver =
        Observer { bitmap: Bitmap? ->
            if(bitmap != null) {

            }
        }

    private val titleObserver =
        Observer { title: String? ->
            if(title != null) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = title
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.sessionName.observe(this, titleObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.sessionName.removeObserver(titleObserver)
    }
}