package edu.udmercy.accesspointlocater.features.testDistance.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.testDistance.model.DistanceUI
import kotlinx.android.synthetic.main.fragment_distance_test.*

/**
 * This fragment was designed as a test utility to evaluate the different
 * path loss models by displaying the calculated distance on screen.
 * The distance can then measured by the user in the room
 */
class DistanceTestFragment: BaseFragment(R.layout.fragment_distance_test) {

    private val viewModel by viewModels<DistanceTestViewModel>()
    private lateinit var wifiManager: WifiManager

    companion object {
        private const val TAG = "DistanceTestFrag"
    }

    private val adapter by lazy {
        DistanceRecyclerAdapter()
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        distanceRecyclerView.adapter = adapter
        refreshDistanceList.setOnClickListener {
            if(!viewModel.isRunning) {
                viewModel.isRunning = true
                startScan()
            }
        }
    }

    private fun startScan() {
        Log.i(TAG, "startScan: Starting scan...")
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
        Log.i(TAG, "scanSuccess: ScanSuccess")
        requireActivity().unregisterReceiver(wifiScanReceiver)
        val results = wifiManager.scanResults

        val wifiName = wifiManager.connectionInfo.ssid.toString()
        val filteredResults = results.filter {"\"" +  it.SSID + "\""== wifiName}
        viewModel.updateList(filteredResults)
        viewModel.isRunning = false
    }

    private fun scanFailure() {
        viewModel.isRunning = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private val accessPointListObserver =
        Observer { list: List<DistanceUI>? ->
            Log.i(TAG, "scanSuccess: updating list")
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

    override fun onResume() {
        super.onResume()
        viewModel.accessPointList.observe(this, accessPointListObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.accessPointList.removeObserver(accessPointListObserver)
    }
}