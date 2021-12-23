package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.net.wifi.WifiManager
import android.util.Log

import android.view.MotionEvent

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.arch.CircleViewPointListener
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import edu.udmercy.accesspointlocater.features.session.room.BuildingImage
import edu.udmercy.accesspointlocater.utils.Event
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ExecuteSessionFragment: BaseFragment(R.layout.fragment_execute_session), CircleViewPointListener {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    companion object {
        private const val TAG = "ExecuteSessionFragment"
    }

    private lateinit var wifiManager: WifiManager

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
            viewModel._isScanning = false
            viewModel.isScanning.postValue(Event(false))
        }
    }

    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            if(bitmap != null) {
                executeImageView.setImage(ImageSource.bitmap(bitmap.image))
            }
        }

    private val savedPointsObserver =
        Observer { points: List<AccessPoint> ->
            if(points.isNotEmpty()) {
                executeImageView.completedPointScans = points
            } else {
                executeImageView.completedPointScans = emptyList()
            }
            executeImageView.touchedPoint = null
            executeImageView.invalidate()
        }

    private val isScanningObserver =
        Observer { scanning: Event<Boolean> ->
            scanning.getContentIfNotHandledOrReturnNull()?.let {
                if(it) {
                    scanningContainer.visibility = View.VISIBLE
                } else {
                    scanningContainer.visibility = View.GONE
                    toast("Scan Complete!")
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private val floorObserver =
        Observer { number: Int ->
            floorInvalidBtn.text = "Floor ${number+1}"
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        val uuid = arguments?.getString("uuid") ?: return
        executeImageView.listener = this
        startScanBtn.setOnClickListener {
            startScan()
        }
        previousFloorBtn.setOnClickListener { viewModel.moveImage(-1, uuid) }
        nextFloorBtn.setOnClickListener { viewModel.moveImage(1, uuid) }

    }

    private fun startScan() {
        viewModel._isScanning = true
        viewModel.isScanning.postValue(Event(true))
        wifiManager = requireActivity().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        requireActivity().registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
    }

    private fun scanSuccess() {
        requireActivity().unregisterReceiver(wifiScanReceiver)
        val results = wifiManager.scanResults
        val wifiName = wifiManager.connectionInfo.ssid.toString()
        val filteredResults = results.filter {"\"" +  it.SSID + "\""== wifiName}
        val uuid = arguments?.getString("uuid") ?: return
        Log.i(TAG, "scanSuccess: $filteredResults")
        viewModel.saveResults(filteredResults, uuid)
    }

    private fun scanFailure() {

    }

    override fun onResume() {
        super.onResume()
        //val uuid = arguments?.getString("uuid") ?: return
        //viewModel.getCurrentSession(uuid)
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.floor.observe(this, floorObserver)
        viewModel.savedPoints.observe(this, savedPointsObserver)
        viewModel.isScanning.observe(this, isScanningObserver)
    }

    private fun toast(msg: String) {
        val safeView = view ?: return
        Snackbar.make(safeView, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.floor.removeObserver(floorObserver)
        viewModel.savedPoints.removeObserver(savedPointsObserver)
        viewModel.isScanning.removeObserver(isScanningObserver)
        viewModel.currentBitmap.postValue(null)
    }

    override fun onPointsChanged(currentPoint: PointF?) {
        viewModel.currentPosition = currentPoint
    }

    override fun onNavigationClick() {
        if(!viewModel._isScanning) {
            super.onNavigationClick()
            viewModel.onPause()
        } else {
            toast("Please wait until scanning is complete.")
        }
    }
}