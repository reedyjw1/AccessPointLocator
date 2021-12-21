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
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.fragment_execute_session.*
import android.graphics.PointF
import android.util.Log

import android.view.MotionEvent

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.arch.CircleViewPointListener


class ExecuteSessionFragment: BaseFragment(R.layout.fragment_execute_session), CircleViewPointListener {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    companion object {
        private const val TAG = "ExecuteSessionFragment"
    }

    private val imageObserver =
        Observer { bitmap: Bitmap? ->
            Log.i(TAG, "bitmap: ${bitmap?.byteCount}")
            if(bitmap != null) {
                executeImageView.setImage(ImageSource.bitmap(bitmap))
            }
        }

    private val numberOfPointsObserver =
        Observer { number: Int ->
            executeImageView.numberOfPoints = number
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
        executeImageView.listener = this

    }

    override fun onResume() {
        super.onResume()
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.allowedNumberOfPoints.observe(this, numberOfPointsObserver)
        Log.i(TAG, "onResume: resuming")
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.allowedNumberOfPoints.removeObserver(numberOfPointsObserver)
        viewModel.currentBitmap.postValue(null)
    }

    override fun onPointsChanged(list: List<PointF>) {
        if(list.isNotEmpty()) {
            viewModel.currentPosition = list.last()
        }
    }
}