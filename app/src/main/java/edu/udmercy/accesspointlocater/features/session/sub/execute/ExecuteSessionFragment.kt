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


class ExecuteSessionFragment: BaseFragment(R.layout.fragment_execute_session) {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    companion object {
        private const val TAG = "ExecuteSessionFragment"
    }

    private val imageObserver =
        Observer { bitmap: Bitmap? ->
            if(bitmap != null) {
                executeImageView.setImage(ImageSource.bitmap(bitmap))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        val uuid = arguments?.getString("uuid") ?: return

        val gestureDetector: GestureDetector =
            GestureDetector(requireContext(), object : SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (executeImageView.isReady) {
                        val sCoord: PointF? = executeImageView.viewToSourceCoord(e.x, e.y)
                        Log.i(TAG, "onSingleTapConfirmed: $sCoord ")

                    }
                    return true
                }
            })

        executeImageView.setOnTouchListener { subView, motionEvent ->
            subView.performClick()
            return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
        }
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