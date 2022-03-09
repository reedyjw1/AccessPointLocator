package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import kotlinx.android.synthetic.main.fragment_access_chooser.*

class AccessPointChooserFragment: BaseFragment(R.layout.fragment_access_chooser) {

    private val viewModel by viewModels<AccessPointChooserViewModel>()
    private lateinit var wifiManager: WifiManager

    companion object {
        private const val TAG = "AccessPointChooserFragm"
    }


    private val adapter by lazy {
        AccessPointRecyclerAdapter().apply {
            onAccessPointClicked = {
                viewModel.accessPointClicked(it)
            }
        }
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
        val bundle = bundleOf("uuid" to arguments?.getString("uuid"))
        accessPointRecyclerView.adapter = adapter
        toExecuteSession.setOnClickListener {
            val uuid = arguments?.getString("uuid")
            viewModel.saveReferenceAccessPointData(uuid) {
                findNavController().navigate(R.id.action_accessChooser_to_executeSession, bundle)
            }
        }
        startScan()
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

        if (results.isEmpty()) {
            //toast("Scan Failed!")
        } else {
            //toast("Scan Complete!")
        }

        val wifiName = wifiManager.connectionInfo.ssid.toString()
        val filteredResults = results.filter {"\"" +  it.SSID + "\""== wifiName}
        viewModel.updateList(filteredResults)
    }

    private fun scanFailure() {
        //toast("Scan Failed!")
    }

    @SuppressLint("NotifyDataSetChanged")
    private val accessPointListObserver =
        Observer { list: List<AccessPointUI>? ->
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